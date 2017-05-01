package be.ephys.utilitas.feature.fluid_hopper;

import be.ephys.utilitas.base.helpers.FluidHelper;
import be.ephys.utilitas.base.helpers.NBTHelper;
import be.ephys.utilitas.base.helpers.WorldHelper;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class TileEntityFluidHopper extends TileEntity implements ITickable {

    public static final int MAX_STACK_SIZE = 1000;
    public static final int RATE = 1000; // mb/s

    private final HopperTank tank = new HopperTank();

    private int cooldown = 0;

    @Override
    public void update() {
        if (this.worldObj == null) {
            return;
        }

        if (this.worldObj.isRemote) {
            return;
        }

        if (!BlockHopper.isEnabled(this.getBlockMetadata())) {
            return;
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        cooldown = 20;

        attemptDrainTarget();
        attemptFillTarget();
    }

    private boolean attemptFillTarget() {
        // TODO if above block is air and an fluid handler entity is present, use that

        EnumFacing orientation = BlockHopper.getFacing(this.getBlockMetadata());
        BlockPos targetPos = getPos().offset(orientation);

        TileEntity te = worldObj.getTileEntity(targetPos);
        if (te == null) {
            return false;
        }

        EnumFacing targetSide = orientation.getOpposite();
        if (!te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, targetSide)) {
            return false;
        }

        IFluidHandler targetFluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, targetSide);

        FluidStack[] fluidStacks = this.tank.fluidStacks;
        for (int i = 0; i < fluidStacks.length; i++) {
            FluidStack fluidStack = fluidStacks[i];

            if (fluidStack == null) {
                continue;
            }

            FluidStack clone = fluidStack.copy();
            clone.amount = Math.min(RATE, clone.amount);


            int filled = targetFluidHandler.fill(clone, true);

            if (filled == 0) {
                continue;
            }

            fluidStack.amount -= filled;
            if (fluidStack.amount <= 0) {
                fluidStacks[i] = null;
            }

            sendUpdate();

            return true;
        }

        return false;
    }

    private boolean attemptDrainTarget() {
        // TODO if above block is air and an fluid handler entity is present, use that

        TileEntity te = worldObj.getTileEntity(getPos().up());

        // parent block is not a tile entity. Maybe it's a fluid block.
        if (te == null) {
            return attemptBlockSuckUp();
        }

        if (!te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN)) {
            return false;
        }

        IFluidHandler source = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);

        FluidStack drain = source.drain(RATE, false);

        if (drain == null) {
            return false;
        }

        int filled = this.tank.fill(drain, true);

        source.drain(filled, true);

        return true;
    }

    private boolean attemptBlockSuckUp() {
        BlockPos abovePos = getPos().up();
        FluidStack fluidStack = FluidHelper.getFluidFromWorld(worldObj, abovePos);

        if (fluidStack == null) {
            return false;
        }

        if (fluidStack.getFluid() == FluidRegistry.WATER) {
            this.tank.fill(fluidStack, true);
            return true;
        } else {
            int filled = this.tank.fill(fluidStack, false);

            if (filled != 1000) {
                return false;
            }

            this.tank.fill(fluidStack, true);
            worldObj.setBlockToAir(abovePos);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        tank.readFluidsFromNBT(pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        tank.writeFluidsToNBT(nbt, true);

        return new SPacketUpdateTileEntity(getPos(), 1, nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);

        tank.writeFluidsToNBT(nbt, false);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        tank.readFluidsFromNBT(nbt);
    }

    public void getFluidsFromStack(ItemStack stack) {
        tank.readFluidsFromNBT(NBTHelper.getNBT(stack));
    }

    public void setFluidsToStack(ItemStack stack) {
        tank.writeFluidsToNBT(NBTHelper.getNBT(stack), false);
    }

    public int getComparatorInput() {
        return this.tank.getComparatorInput();
    }

    private void sendUpdate() {
        markDirty();

        WorldHelper.markTileForUpdate(this);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState != newSate;
    }

    public void onBlockUpdate(BlockPos pos) {
        attemptBlockSuckUp();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) this.tank;
        }

        return null;
    }

    private class HopperTank implements IFluidHandler {
        private FluidStack[] fluidStacks = new FluidStack[5];

        @Override
        public IFluidTankProperties[] getTankProperties() {
            IFluidTankProperties[] tanks = new IFluidTankProperties[fluidStacks.length];

            for (int i = 0; i < fluidStacks.length; i++) {
                if (fluidStacks[i] == null)
                    continue;

                tanks[i] = new FluidTankProperties(fluidStacks[i], MAX_STACK_SIZE);
            }

            return tanks;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            int slot = getSlotForFluid(resource);

            if (slot == -1)
                return 0;

            int[] filledPerSlot = new int[fluidStacks.length];
            int filledTotal = 0;

            // start by the slot containing the fluid
            int toFill = Math.min(fluidStacks[slot] == null ? MAX_STACK_SIZE : MAX_STACK_SIZE - fluidStacks[slot].amount, resource.amount);
            filledPerSlot[slot] = toFill;
            filledTotal += toFill;

            // then check the other slots
            for (int i = 0; i < fluidStacks.length && filledTotal < resource.amount; i++) {
                if (i == slot) continue;

                int availableSpace;
                if (fluidStacks[i] != null) {
                    if (!fluidStacks[i].isFluidEqual(resource)) continue;

                    availableSpace = MAX_STACK_SIZE - fluidStacks[i].amount;
                } else {
                    availableSpace = MAX_STACK_SIZE;
                }

                toFill = Math.min(resource.amount - filledTotal, availableSpace);

                filledPerSlot[i] = toFill;
                filledTotal += toFill;
            }

            if (doFill) {
                for (int i = 0; i < fluidStacks.length; i++) {
                    if (filledPerSlot[i] == 0) continue;

                    if (fluidStacks[i] == null) {
                        fluidStacks[i] = new FluidStack(resource.getFluid(), filledPerSlot[i], resource.tag);
                    } else {
                        fluidStacks[i].amount += filledPerSlot[i];
                    }
                }

                sendUpdate();
            }

            return filledTotal;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            int slot = getSlotForFluid(resource);

            if (slot == -1 || fluidStacks[slot] == null)
                return null;

            return drain(slot, resource.amount, doDrain);
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            int slot = getFirstFilledSlot();

            if (slot == -1) {
                return null;
            }

            return drain(slot, maxDrain, doDrain);
        }

        private FluidStack drain(int slot, int amount, boolean doDrain) {
            FluidStack stack = fluidStacks[slot];

            int drained = Math.min(Math.min(amount, RATE), stack.amount);

            if (doDrain) {
                stack.amount -= drained;

                if (stack.amount <= 0)
                    fluidStacks[slot] = null;

                sendUpdate();
            }

            return new FluidStack(stack.getFluid(), drained, stack.tag);
        }

        public int getComparatorInput() {
            int totalFilled = 0;

            for (FluidStack fluidStack : this.fluidStacks) {
                totalFilled += fluidStack.amount;
            }

            return (int) Math.ceil(((double) totalFilled) / (MAX_STACK_SIZE * this.fluidStacks.length));
        }

        private int getFirstFilledSlot() {
            for (int i = 0; i < fluidStacks.length; i++) {
                if (fluidStacks[i] != null)
                    return i;
            }

            return -1;
        }

        private int getSlotForFluid(FluidStack fluid) {
            int firstEmptySlot = -1;

            for (int i = fluidStacks.length - 1; i >= 0; i--) {
                if (fluidStacks[i] == null) {
                    firstEmptySlot = i;
                } else {
                    if (fluidStacks[i].amount < MAX_STACK_SIZE && fluidStacks[i].isFluidEqual(fluid)) {
                        return i;
                    }
                }
            }

            return firstEmptySlot;
        }

        private void writeFluidsToNBT(NBTTagCompound nbt, boolean writeIfEmpty) {
            NBTTagCompound fluidStackNBT = new NBTTagCompound();

            int nbSet = 0;
            for (int i = 0; i < fluidStacks.length; i++) {
                if (fluidStacks[i] != null) {
                    NBTTagCompound fluidNBT = new NBTTagCompound();
                    fluidStacks[i].writeToNBT(fluidNBT);

                    fluidStackNBT.setTag(Integer.toString(i), fluidNBT);

                    nbSet++;
                }
            }

            if (nbSet != 0 || writeIfEmpty) {
                nbt.setTag("fluidStacks", fluidStackNBT);
            }
        }

        private void readFluidsFromNBT(NBTTagCompound nbt) {
            if (!nbt.hasKey("fluidStacks")) {
                return;
            }

            NBTTagCompound fluidStacksNBT = nbt.getCompoundTag("fluidStacks");

            for (int i = 0; i < fluidStacks.length; i++) {
                if (fluidStacksNBT.hasKey(Integer.toString(i))) {
                    fluidStacks[i] = FluidStack.loadFluidStackFromNBT(fluidStacksNBT.getCompoundTag(Integer.toString(i)));
                } else {
                    fluidStacks[i] = null;
                }
            }
        }
    }
}

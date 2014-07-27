package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class TileEntityFluidHopper extends TileEntity implements IFluidHandler {
	private FluidStack[] fluidStacks = new FluidStack[5];
	public static final int MAX_STACK_SIZE = 5000;
	public static final int RATE = 1000;

	private void writeFluidsToNBT(NBTTagCompound nbt) {
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

		if (nbSet != 0)
			nbt.setTag("fluidStacks", fluidStackNBT);
	}

	private void readFluidsFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("fluidStacks")) {
			NBTTagCompound fluidStacksNBT = nbt.getCompoundTag("fluidStacks");

			for (int i = 0; i < fluidStacks.length; i++) {
				if (fluidStacksNBT.hasKey(Integer.toString(i))) {
					fluidStacks[i] = FluidStack.loadFluidStackFromNBT(fluidStacksNBT.getCompoundTag(Integer.toString(i)));
				} else {
					fluidStacks[i] = null;
				}
			}
		} else {
			fluidStacks = new FluidStack[fluidStacks.length];
		}
	}

	public void getFluidsFromStack(ItemStack stack) {
		readFluidsFromNBT(NBTHelper.getNBT(stack));
	}

	public void setFluidsToStack(ItemStack stack) {
		writeFluidsToNBT(NBTHelper.getNBT(stack));
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFluidsFromNBT(pkt.func_148857_g());
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeFluidsToNBT(nbt);

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		writeFluidsToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		readFluidsFromNBT(nbt);
	}

	public int getComparatorInput() {
		return 0;
	}

	public FluidStack[] getFluidStacks() {
		return fluidStacks;
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

	private int getFirstFilledSlot() {
		for (int i = 0; i < fluidStacks.length; i++) {
			if (fluidStacks[i] != null) return i;
		}

		return -1;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		int slot = getSlotForFluid(resource);

		if (slot == -1)
			return 0;

		FluidStack stack = fluidStacks[slot];

		int emptySpace = stack == null ? MAX_STACK_SIZE : MAX_STACK_SIZE - stack.amount;

		int filled = Math.min(Math.min(resource.amount, RATE), emptySpace);

		if (doFill) {
			if (stack == null)
				fluidStacks[slot] = new FluidStack(resource.getFluid().getID(), filled, resource.tag);
			else
				stack.amount += filled;

			sendUpdate();
		}

		return filled;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		int slot = getSlotForFluid(resource);

		if (slot == -1 || fluidStacks[slot] == null) return null;

		return drain(slot, resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		int slot = getFirstFilledSlot();

		if (slot == -1) return null;

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

		return new FluidStack(stack.getFluid().getID(), drained, stack.tag);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		FluidTankInfo[] tanks = new FluidTankInfo[fluidStacks.length];

		for (int i = 0; i < fluidStacks.length; i++) {
			if (fluidStacks[i] == null) continue;

			tanks[i] = new FluidTankInfo(fluidStacks[i], MAX_STACK_SIZE);
		}

		return tanks;
	}

	private void sendUpdate() {
		markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
}
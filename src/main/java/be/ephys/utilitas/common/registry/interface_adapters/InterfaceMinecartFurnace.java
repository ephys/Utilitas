package be.ephys.utilitas.common.registry.interface_adapters;

import be.ephys.utilitas.common.tileentity.TileEntityInterface;
import be.ephys.utilitas.common.util.EntityHelper;
import be.ephys.utilitas.common.util.NBTHelper;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.UUID;

public class InterfaceMinecartFurnace extends UniversalInterfaceAdapter {

    private FurnaceProxy proxy = new FurnaceProxy();
    private EntityMinecartFurnace minecart;
    private UUID uuid;

    public InterfaceMinecartFurnace(TileEntityInterface tileEntity) {
        super(tileEntity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
        GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);

        renderBlock(Blocks.FURNACE);
    }

    @Override
    public boolean setLink(Object link, EntityPlayer linker) {
        if (!(link instanceof EntityMinecartFurnace)) {
            return false;
        }

        minecart = (EntityMinecartFurnace) link;
        return true;

    }

    @Override
    public ITextComponent getName() {
        return minecart.getDisplayName();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTHelper.setEntityUuid(nbt, "minecart", minecart);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        uuid = NBTHelper.getUuid(nbt, "minecart", null);
    }

    @Override
    public void onTick(int tick) {
        if (isRemote()) {
            return;
        }

        if (minecart == null && uuid != null) {
            minecart = (EntityMinecartFurnace) EntityHelper.getEntityByUuid(uuid);
        }

        if (minecart == null || minecart.isDead) {
            getInterface().unlink();
        }
    }

    @Override
    public IInventory getInventory() {
        return minecart == null ? null : proxy;
    }

    @Override
    public boolean isNextTo(BlockPos pos) {
        return false;
    }

    @Override
    public int getDimension() {
        return minecart == null ? 0 : minecart.getEntityWorld().provider.getDimension();
    }

    @Override
    public void onBlockUpdate() {
    }

    @Override
    public void validate() {
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
    }

    public class FurnaceProxy implements IInventory {

        @Override
        public int getSizeInventory() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return null;
        }

        @Override
        public ItemStack decrStackSize(int slot, int amount) {
            return null;
        }

        @Nullable
        @Override
        public ItemStack removeStackFromSlot(int index) {
            return null;
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            if (minecart.fuel > 10) {
                return;
            }

            int burnTime = TileEntityFurnace.getItemBurnTime(stack);
            if (burnTime > 0) {
                minecart.fuel += burnTime;
                minecart.pushX = 5;
                minecart.pushZ = 5;
            }
        }

        @Override
        public int getInventoryStackLimit() {
            return 1;
        }

        @Override
        public void markDirty() {
        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
            return true;
        }

        @Override
        public void openInventory(EntityPlayer player) {
        }

        @Override
        public void closeInventory(EntityPlayer player) {
        }

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            return slot == 0 && minecart.fuel <= 10 && TileEntityFurnace.getItemBurnTime(stack) > 0;
        }

        @Override
        public int getField(int id) {
            return 0;
        }

        @Override
        public void setField(int id, int value) {

        }

        @Override
        public int getFieldCount() {
            return 0;
        }

        @Override
        public void clear() {

        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean hasCustomName() {
            return false;
        }

        @Override
        public ITextComponent getDisplayName() {
            return null;
        }
    }
}

package be.ephys.utilitas.common.registry.interface_adapters;

import be.ephys.utilitas.common.tileentity.TileEntityInterface;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

public class InterfaceItemFrame extends UniversalInterfaceAdapter {
    private ItemFrameProxy proxy = new ItemFrameProxy();
    private EntityItemFrame itemFrame;
    private UUID uuid;

    public InterfaceItemFrame(TileEntityInterface tileEntity) {
        super(tileEntity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
        UniversalInterfaceAdapter.defaultRenderInventory(tickCount);
    }

    @Override
    public boolean setLink(Object link, EntityPlayer linker) {
        if (link instanceof EntityItemFrame) {
            this.itemFrame = (EntityItemFrame) link;
            return true;
        }

        return false;
    }

    @Override
    public ITextComponent getName() {
        return itemFrame.getDisplayName();
    }

    @Override
    public void validate() {
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTHelper.setEntity(nbt, "itemframe", itemFrame);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.uuid = NBTHelper.getUUID(nbt, "itemframe", null);
    }

    @Override
    public void onBlockUpdate() {
    }

    @Override
    public void onTick(int tick) {
        if (isRemote()) {
            return;
        }

        if (itemFrame == null) {
            if (uuid != null) {
                itemFrame = (EntityItemFrame) EntityHelper.getEntityByUUID(uuid);

                uuid = null;
            } else {
                getInterface().unlink();
            }
        }

        if (itemFrame != null && itemFrame.isDead) {
            getInterface().unlink();
        }
    }

    @Override
    public IInventory getInventory() {
        return itemFrame == null ? null : proxy;
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return null;
    }

    private float[] relativeX = new float[]{-0.5F, 0.0625F, -0.5F, -1.0625F};
    private float[] relativeZ = new float[]{-1.0625F, -0.5F, 0.0625F, -0.5F};

    @Override
    public boolean isNextTo(int xCoord, int yCoord, int zCoord) {
        if (itemFrame == null) {
            return false;
        }

        if (Math.abs(yCoord - itemFrame.posY) != 0.5F) {
            return false;
        }

        EnumFacing facingDirection = itemFrame.facingDirection;

        if (facingDirection == null) {
            return false;
        }

        return xCoord - itemFrame.posX == relativeX[facingDirection.getHorizontalIndex()]
                && zCoord - itemFrame.posZ == relativeZ[facingDirection.getHorizontalIndex()];
    }

    @Override
    public int getDimension() {
        return itemFrame == null ? 0 : itemFrame.worldObj.provider.dimensionId;
    }

    public class ItemFrameProxy implements IInventory {

        @Override
        public int getSizeInventory() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return itemFrame.getDisplayedItem();
        }

        @Override
        public ItemStack decrStackSize(int slot, int amount) {
            return this.removeStackFromSlot(slot);
        }

        @Nullable
        @Override
        public ItemStack removeStackFromSlot(int index) {
            ItemStack item = itemFrame.getDisplayedItem();
            if (item != null) {
                itemFrame.setDisplayedItem(null);
            }

            return item;
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            if (stack != null)
                stack.setItemFrame(itemFrame);

            itemFrame.setDisplayedItem(stack);
        }

        @Override
        public int getInventoryStackLimit() {
            return 1;
        }

        @Override
        public void markDirty() {}

        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
            return true;
        }

        @Override
        public void openInventory(EntityPlayer player) {}

        @Override
        public void closeInventory(EntityPlayer player) {}

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            return true;
        }

        @Override
        public int getField(int id) {
            return 0;
        }

        @Override
        public void setField(int id, int value) {}

        @Override
        public int getFieldCount() {
            return 0;
        }

        @Override
        public void clear() {}

        @Override
        public String getName() {
            return itemFrame.getName();
        }

        @Override
        public boolean hasCustomName() {
            return itemFrame.hasCustomName();
        }

        @Override
        public ITextComponent getDisplayName() {
            return itemFrame.getDisplayName();
        }
    }
}

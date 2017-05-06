package be.ephys.utilitas.feature.universal_interface.interface_adapters;

import be.ephys.utilitas.api.registry.UniversalInterfaceAdapter;
import be.ephys.utilitas.base.helpers.EntityHelper;
import be.ephys.utilitas.base.helpers.NBTHelper;
import be.ephys.utilitas.feature.universal_interface.TileEntityInterface;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

public class InterfaceItemFrame extends UniversalInterfaceAdapter<EntityItemFrame> {

    private ItemFrameProxy proxy = new ItemFrameProxy();
    private EntityItemFrame itemFrame;
    private UUID uuid;

    private float[] relativeX = new float[]{-0.5F, 0.03125F, -0.5F, -1.03125F};
    private float[] relativeZ = new float[]{-1.03125F, -0.5F, 0.03125F, -0.5F};

    public InterfaceItemFrame(TileEntityInterface tileEntity) {
        super(tileEntity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(long tickCount, double x, double y, double z, float tickTime) {
        UniversalInterfaceAdapter.defaultRenderInventory(tickCount);
    }

    @Override
    public boolean setLink(EntityItemFrame link, EntityPlayer linker) {
        this.itemFrame = link;
        return true;
    }

    @Override
    public ITextComponent getName() {
        return itemFrame.getDisplayName();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTHelper.setEntityUuid(nbt, "target", itemFrame);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.uuid = NBTHelper.getUuid(nbt, "target", null);
    }

    @Override
    public void onBlockUpdate() {
    }

    @Override
    public void onTick(long tick) {
        if (isRemote()) {
            return;
        }

        if (itemFrame == null) {
            if (uuid != null) {
                itemFrame = (EntityItemFrame) EntityHelper.getEntityByUuid(uuid);

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
    public boolean isNextTo(BlockPos pos) {
        if (itemFrame == null) {
            return false;
        }

        if (Math.abs(pos.getY() - itemFrame.posY) > 0.5) {
            return false;
        }

        EnumFacing facingDirection = itemFrame.facingDirection;

        if (facingDirection == null) {
            return false;
        }

        return pos.getX() - itemFrame.posX == relativeX[facingDirection.getHorizontalIndex()]
            && pos.getZ() - itemFrame.posZ == relativeZ[facingDirection.getHorizontalIndex()];
    }

    @Override
    public int getDimension() {
        return itemFrame == null ? 0 : itemFrame.getEntityWorld().provider.getDimension();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
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
        public void markDirty() {
        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
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
            return true;
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

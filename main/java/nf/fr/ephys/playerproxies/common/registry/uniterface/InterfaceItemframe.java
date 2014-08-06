package nf.fr.ephys.playerproxies.common.registry.uniterface;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.cookiecore.helpers.EntityHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;

import java.util.UUID;

public class InterfaceItemframe extends UniversalInterface {
	private ItemFrameProxy proxy = new ItemFrameProxy();
	private EntityItemFrame itemFrame;
	private UUID uuid;

	public InterfaceItemframe(TileEntityInterface tileEntity) {
		super(tileEntity);
	}

	@Override
	public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {

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
	public String getName() {
		return itemFrame.getCommandSenderName();
	}

	@Override
	public void validate() {}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTHelper.setEntity(nbt, "itemframe", itemFrame);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		uuid = NBTHelper.getUUID(nbt, "itemframe", null);
	}

	@Override
	public void onBlockUpdate() {}

	@Override
	public void onTick(int tick) {
		if (itemFrame == null) {
			if (uuid != null) {
				itemFrame = (EntityItemFrame) EntityHelper.getEntityByUUID(uuid);

				uuid = null;
			} else {
				getTileEntity().unlink();
			}
		}

		if (itemFrame != null && itemFrame.isDead)
			getTileEntity().unlink();
	}

	@Override
	public IInventory getInventory() {
		return proxy;
	}

	@Override
	public IFluidHandler getFluidHandler() {
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
			if (amount < 1) return null;

			return getStackInSlotOnClosing(slot);
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			ItemStack stack = itemFrame.getDisplayedItem().copy();
			stack.setItemFrame(null);

			setInventorySlotContents(slot, null);

			return stack;
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack stack) {
			if (stack != null)
				stack.setItemFrame(itemFrame);

			itemFrame.setDisplayedItem(stack);
		}

		@Override
		public String getInventoryName() {
			return null;
		}

		@Override
		public boolean hasCustomInventoryName() {
			return false;
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
		public void openInventory() {}

		@Override
		public void closeInventory() {}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack stack) {
			return true;
		}
	}
}
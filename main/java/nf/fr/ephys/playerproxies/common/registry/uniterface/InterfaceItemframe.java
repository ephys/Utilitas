package nf.fr.ephys.playerproxies.common.registry.uniterface;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.cookiecore.helpers.EntityHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;
import nf.fr.ephys.cookiecore.helpers.RenderHelper;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import org.lwjgl.opengl.GL11;

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
		GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);

		RenderHelper.loadBlockMap();
		//Block block = blockEntity == null ? Blocks.chest : blockEntity.getBlockType();
		// todo: there is a lot of problems with rendering the thingy as an item, need to sort that out
		nf.fr.ephys.playerproxies.client.renderer.TileEntityInterfaceRenderer.renderBlocksInstance.renderBlockAsItem(Blocks.chest, 0, 1.0F);
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
		return itemFrame == null ? null : proxy;
	}

	@Override
	public IFluidHandler getFluidHandler() {
		return null;
	}

	private float[] relativeX = new float[] { -0.5F, 0.0625F, -0.5F, -1.0625F };
	private float[] relativeZ = new float[] { -1.0625F, -0.5F, 0.0625F, -0.5F };

	@Override
	public boolean isNextTo(int xCoord, int yCoord, int zCoord) {
		if (itemFrame == null) return false;

		if (Math.abs(yCoord - itemFrame.posY) != 0.5F) return false;

		return xCoord - itemFrame.posX == relativeX[itemFrame.hangingDirection] && zCoord - itemFrame.posZ == relativeZ[itemFrame.hangingDirection];
	}

	@Override
	public int getDim() {
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
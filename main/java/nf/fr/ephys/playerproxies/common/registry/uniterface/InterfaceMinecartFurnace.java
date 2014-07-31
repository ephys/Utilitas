package nf.fr.ephys.playerproxies.common.registry.uniterface;

import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.cookiecore.helpers.EntityHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;
import nf.fr.ephys.cookiecore.helpers.RenderHelper;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public class InterfaceMinecartFurnace extends UniversalInterface {
	private FurnaceProxy proxy = new FurnaceProxy();
	private EntityMinecartFurnace minecart;
	private UUID uuid;

	public InterfaceMinecartFurnace(TileEntityInterface tileEntity) {
		super(tileEntity);
	}

	@Override
	public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
		//RenderManager.instance.renderEntityWithPosYaw(minecart, x, y, z, 0, 0);

		// todo: real render duh
		GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);

		RenderHelper.loadBlockMap();
		nf.fr.ephys.playerproxies.client.renderer.TileEntityInterfaceRenderer.renderBlocksInstance.renderBlockAsItem(Blocks.furnace, 0, 1.0F);
	}

	@Override
	public boolean setLink(Object link, EntityPlayer linker) {
		if (link instanceof EntityMinecartFurnace) {
			minecart = (EntityMinecartFurnace) link;

			return true;
		}

		return false;
	}

	@Override
	public String getName() {
		return minecart.getCommandSenderName();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTHelper.setEntity(nbt, "minecart", minecart);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		uuid = NBTHelper.getUUID(nbt, "minecart", null);
	}

	@Override
	public void onTick(int tick) {
		if (minecart == null && uuid != null) {
			minecart = (EntityMinecartFurnace) EntityHelper.getEntityByUUID(uuid);
		}

		if (minecart == null || minecart.isDead)
			getTileEntity().unlink();
	}

	@Override
	public IInventory getInventory() {
		return minecart == null ? null : proxy;
	}

	@Override
	public IFluidHandler getFluidHandler() {
		return null;
	}

	@Override
	public void onBlockUpdate() {}

	@Override
	public void validate() {}

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

		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			return null;
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack stack) {
			if (minecart.fuel > 10) return;

			int burnTime = TileEntityFurnace.getItemBurnTime(stack);
			if (burnTime > 0) {
				minecart.fuel += burnTime;
				minecart.pushX = 5;
				minecart.pushZ = 5;
			}
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
		public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
			return true;
		}

		@Override
		public void openInventory() {}

		@Override
		public void closeInventory() {}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack stack) {
			return slot == 0 && minecart.fuel <= 10 && TileEntityFurnace.getItemBurnTime(stack) > 0;
		}
	}
}
package nf.fr.ephys.playerproxies.common.registry.uniterface;

import net.minecraft.block.BlockJukebox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.cookiecore.helpers.BlockHelper;
import nf.fr.ephys.cookiecore.helpers.ChatHelper;
import nf.fr.ephys.cookiecore.helpers.RenderHelper;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import org.lwjgl.opengl.GL11;

public class InterfaceJukebox extends UniversalInterface {
	private JukeBoxProxy jukeboxProxy;
	private int[] tileLocation;
	private int tileWorld;

	public InterfaceJukebox(TileEntityInterface tileEntity) {
		super(tileEntity);

		jukeboxProxy = new JukeBoxProxy();
	}

	@Override
	public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
		GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);

		RenderHelper.loadBlockMap();
		nf.fr.ephys.playerproxies.client.renderer.TileEntityInterfaceRenderer.renderBlocksInstance.renderBlockAsItem(Blocks.jukebox, 0, 1.0F);
	}

	@Override
	public boolean setLink(Object link, EntityPlayer linker) {
		if (link instanceof BlockJukebox.TileEntityJukebox) {
			jukeboxProxy.jukebox = (BlockJukebox.TileEntityJukebox) link;

			return true;
		}

		return false;
	}

	@Override
	public String getName() {
		return ChatHelper.getDisplayName(Blocks.jukebox);
	}

	@Override
	public void validate() {}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setIntArray("entityLocation", BlockHelper.getCoords(jukeboxProxy.jukebox));
		nbt.setInteger("entityDim", jukeboxProxy.jukebox.getWorldObj().provider.dimensionId);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		tileLocation = nbt.getIntArray("entityLocation");
		tileWorld = nbt.getInteger("entityDim");
	}

	@Override
	public void onBlockUpdate() {}

	@Override
	public void onTick(int tick) {
		if (getTileEntity().getWorldObj().isRemote) return;

		if (jukeboxProxy.jukebox == null) {
			if (tileLocation == null || tileLocation.length != 3) {
				this.getTileEntity().unlink();
				return;
			}

			World world = MinecraftServer.getServer().worldServerForDimension(tileWorld);
			TileEntity te = world.getTileEntity(tileLocation[0], tileLocation[1], tileLocation[2]);

			if (te instanceof BlockJukebox.TileEntityJukebox)
				jukeboxProxy.jukebox = (BlockJukebox.TileEntityJukebox) te;
			else {
				this.getTileEntity().unlink();
				return;
			}
		}

		if (jukeboxProxy.jukebox.isInvalid()) {
			jukeboxProxy.jukebox = null;
			this.getTileEntity().unlink();
		}
	}

	@Override
	public IInventory getInventory() {
		return jukeboxProxy.jukebox == null ? null : jukeboxProxy;
	}

	@Override
	public IFluidHandler getFluidHandler() {
		return null;
	}

	@Override
	public boolean isNextTo(int xCoord, int yCoord, int zCoord) {
		return jukeboxProxy.jukebox != null &&
				Math.abs(xCoord - jukeboxProxy.jukebox.xCoord)
						+ Math.abs(yCoord - jukeboxProxy.jukebox.yCoord)
						+ Math.abs(zCoord - jukeboxProxy.jukebox.zCoord) == 1;
	}

	@Override
	public int getDim() {
		return jukeboxProxy.jukebox == null ? 0 : jukeboxProxy.jukebox.getWorldObj().provider.dimensionId;
	}

	public static class JukeBoxProxy implements IInventory {
		private BlockJukebox.TileEntityJukebox jukebox;
		private long insertTime = Integer.MIN_VALUE;

		@Override
		public int getSizeInventory() {
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			if (slot != 0) return null;

			return jukebox.func_145856_a();
		}

		@Override
		public ItemStack decrStackSize(int slot, int amount) {
			if (slot != 0 || amount < 1) return null;

			// this is some hotfix to prevent the music from /not/ stopping when inserting/extracting too quickly
			if (jukebox.getWorldObj().getTotalWorldTime() - 3 < insertTime) return null;

			ItemStack previousStack = jukebox.func_145856_a();

			setInventorySlotContents(0, null);

			return previousStack;
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			return decrStackSize(slot, 1);
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack stack) {
			if (isItemValidForSlot(slot, stack))
				jukebox.func_145857_a(stack);

			if (stack == null) {
				jukebox.getWorldObj().playAuxSFX(1005, jukebox.xCoord, jukebox.yCoord, jukebox.zCoord, 0);
				jukebox.getWorldObj().playRecord(null, jukebox.xCoord, jukebox.yCoord, jukebox.zCoord);

				jukebox.getWorldObj().setBlockMetadataWithNotify(jukebox.xCoord, jukebox.yCoord, jukebox.zCoord, 0, 2);
			} else {
				((BlockJukebox)Blocks.jukebox).func_149926_b(jukebox.getWorldObj(), jukebox.xCoord, jukebox.yCoord, jukebox.zCoord, stack);
				jukebox.getWorldObj().playAuxSFXAtEntity(null, 1005, jukebox.xCoord, jukebox.yCoord, jukebox.zCoord, Item.getIdFromItem(stack.getItem()));

				insertTime = jukebox.getWorldObj().getTotalWorldTime();
			}
		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack stack) {
			return slot == 0 && (stack == null || stack.getItem() instanceof ItemRecord);
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
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
		public void markDirty() {
			jukebox.markDirty();
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
			return true;
		}

		@Override
		public void openInventory() {}

		@Override
		public void closeInventory() {}
	}
}
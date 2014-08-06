package nf.fr.ephys.playerproxies.common.registry.uniterface;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
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

public class  InterfaceTileEntity extends UniversalInterface {
	private TileEntity blockEntity = null;

	private int[] tileLocation = null;
	private int tileWorld;

	public InterfaceTileEntity(TileEntityInterface tileEntity) {
		super(tileEntity);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventory(int tickCount, double par1, double par3, double par5, float par7) {
		GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);

		RenderHelper.loadBlockMap();
		//Block block = blockEntity == null ? Blocks.chest : blockEntity.getBlockType();
		// todo: there is a lot of problems with rendering the thingy as an item, need to sort that out
		nf.fr.ephys.playerproxies.client.renderer.TileEntityInterfaceRenderer.renderBlocksInstance.renderBlockAsItem(Blocks.chest, 0, 1.0F);
	}

	@Override
	public boolean setLink(Object link, EntityPlayer linker) {
		if (link instanceof TileEntity && (link instanceof IInventory || link instanceof IFluidHandler)) {
			this.blockEntity = (TileEntity) link;

			return true;
		}

		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setIntArray("entityLocation", BlockHelper.getCoords(blockEntity));
		nbt.setInteger("entityWorld", blockEntity.getWorldObj().provider.dimensionId);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.tileLocation = nbt.getIntArray("entityLocation");
		this.tileWorld = nbt.getInteger("entityWorld");
	}

	@Override
	public void onBlockUpdate() {}

	@Override
	public IInventory getInventory() {
		return blockEntity instanceof IInventory ? (IInventory) blockEntity : null;
	}

	@Override
	public IFluidHandler getFluidHandler() {
		return blockEntity instanceof IFluidHandler ? (IFluidHandler) blockEntity : null;
	}

	@Override
	public boolean isNextTo(int xCoord, int yCoord, int zCoord) {
		return blockEntity != null &&
				Math.abs(xCoord - blockEntity.xCoord)
						+ Math.abs(yCoord - blockEntity.yCoord)
						+ Math.abs(zCoord - blockEntity.zCoord) == 1;
	}

	@Override
	public int getDim() {
		return blockEntity == null ? 0 : blockEntity.getWorldObj().provider.dimensionId;
	}

	@Override
	public void onTick(int tick) {
		if (blockEntity == null) {
			if (tileLocation == null || tileLocation.length != 3) {
				this.getTileEntity().unlink();
				return;
			}

			World world = MinecraftServer.getServer().worldServerForDimension(tileWorld);
			this.blockEntity = world.getTileEntity(tileLocation[0], tileLocation[1], tileLocation[2]);
		}

		if (this.blockEntity.isInvalid()) {
			this.getTileEntity().unlink();
		}
	}

	@Override
	public String getName() {
		return ChatHelper.getDisplayName(blockEntity);
	}

	@Override
	public void validate() {}
}

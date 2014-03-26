package nf.fr.ephys.playerproxies.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityItemTicker;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;

public class BlockItemTicker extends BlockContainer {
	public static int BLOCK_ID = 808;

	private Icon iconTop;
	private Icon iconSide;
	private Icon iconBottom;

	public BlockItemTicker() {
		super(BLOCK_ID, Material.iron);

		setHardness(2.5F);
		setCreativeTab(CreativeTabs.tabDecorations);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8F, 1.0F);
	}

	@Override
	public Icon getIcon(int side, int metadata) {
		switch (side) {
		case 0:
			return iconBottom;
		case 1:
			return iconTop;
		default:
			return iconSide;
		}
	}

	@Override
	public void registerIcons(IconRegister register) {
		iconTop = register.registerIcon("ephys.pp:itemTickerTop");
		iconBottom = register.registerIcon("ephys.pp:itemTickerBottom");
		iconSide = register.registerIcon("ephys.pp:itemTickerSide");
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityItemTicker();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (world.isRemote)
			return true;

		TileEntityItemTicker te = (TileEntityItemTicker) world
				.getBlockTileEntity(x, y, z);

		if (te != null) {
			if (te.hasStackInSlot(0))
				BlockHelper.dropContents(te, world, x, y, z);
			else if (player.getHeldItem() != null) {
				te.setInventorySlotContents(0, player.getHeldItem().copy());
				player.getHeldItem().stackSize--;
			}

			world.markBlockForUpdate(x, y, z);
		}

		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int metadata) {
		TileEntityItemTicker te = (TileEntityItemTicker) world
				.getBlockTileEntity(x, y, z);

		if (te != null)
			BlockHelper.dropContents(te, world, x, y, z);

		super.onBlockPreDestroy(world, x, y, z, metadata);
	}
}

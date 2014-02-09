package nf.fr.ephys.playerproxies.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeChanger;

public class BlockBiomeChanger extends BlockContainer {
	public static int blockID = 806;
	
	private Icon iconTop;
	private Icon iconSide;
	private Icon iconBottom;
	
	public BlockBiomeChanger() {
		super(blockID, Material.wood);
	}

	@Override
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2,
			int par3, int par4, int par5) {
		
		switch(par5) {
			case 0: return iconBottom;
			case 1: return iconTop;
			default: return iconSide;
		}
	}
	
	@Override
	public Icon getIcon(int par1, int par2) {
		switch(par1) {
			case 0: return iconBottom;
			case 1: return iconTop;
			default: return iconSide;
		}
	}

	@Override
	public void registerIcons(IconRegister register) {
		iconTop    = register.registerIcon("ephys.pp:biomeChangerTop");
		iconBottom = register.registerIcon("ephys.pp:biomeChangerBottom");
		iconSide   = register.registerIcon("ephys.pp:biomeChangerSide");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityBiomeChanger();
	}
}

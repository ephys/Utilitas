package nf.fr.ephys.playerproxies.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;

public class BlockToughwoodPlank extends Block {
	public static int BLOCK_ID = 807;

	private Icon iconSide;
	private Icon iconBottom;
	
	public BlockToughwoodPlank() {
		super(BLOCK_ID, Material.wood);

		setHardness(1.0F);
		setCreativeTab(CreativeTabs.tabBlock);

		setStepSound(soundWoodFootstep);
	}

	@Override
	public Icon getIcon(int par1, int par2) {
		return (par1 == 0 || par1 == 1) ? iconBottom : iconSide;
	}

	@Override
	public void registerIcons(IconRegister register) {
		iconBottom = register.registerIcon("ephys.pp:biomeChangerBottom");
		iconSide   = register.registerIcon("ephys.pp:biomeScannerSide");
	}
}

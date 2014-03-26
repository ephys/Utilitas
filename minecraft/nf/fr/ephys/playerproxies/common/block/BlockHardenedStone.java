package nf.fr.ephys.playerproxies.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockHardenedStone extends Block {
	public static int BLOCK_ID = 802;
	
	public BlockHardenedStone() {
		super(BlockHardenedStone.BLOCK_ID, Material.iron);

		setHardness(2.5F);
		setResistance(5000.0F);
		setCreativeTab(CreativeTabs.tabBlock);
		setTextureName("ephys.pp:hardenedStone");
	}
}
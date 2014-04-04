package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

public class BlockToughwoodPlank extends Block {
	public static int BLOCK_ID = 807;

	private Icon iconSide;
	private Icon iconBottom;
	
	public static void register() {
		PlayerProxies.blockToughwoodPlank = new BlockToughwoodPlank();
		PlayerProxies.blockToughwoodPlank.setUnlocalizedName("PP_ToughwoodPlank");
		GameRegistry.registerBlock(PlayerProxies.blockToughwoodPlank, "PP_ToughwoodPlank");
		LanguageRegistry.instance().addName(PlayerProxies.blockToughwoodPlank, "Toughwood");
	}
	
	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockToughwoodPlank, 4), 
				" b ", "bwb", " b ",
				'b', new ItemStack(Block.planks, 1, 2),
				'w', new ItemStack(Block.wood)
		);
	}
	
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

package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

public class BlockHardenedStone extends Block {
	public static int BLOCK_ID = 802;
	
	public static void register() {
		PlayerProxies.blockHardenedStone = new BlockHardenedStone();
		PlayerProxies.blockHardenedStone.setUnlocalizedName("PP_HardenedStone");
		GameRegistry.registerBlock(PlayerProxies.blockHardenedStone, "PP_HardenedStone");
		LanguageRegistry.instance().addName(PlayerProxies.blockHardenedStone, "Hardened Stone");
	}
	
	public static void registerCraft() {
		if(Loader.isModLoaded("IC2")) {
			GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockHardenedStone, 8),
					"ioi", "oso", "ioi", 
					'i', ic2.api.item.Items.getItem("advancedAlloy"), 
					's', ic2.api.item.Items.getItem("reinforcedStone"), 
					'o', new ItemStack(Block.obsidian));
		} else {
			GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockHardenedStone, 6),
					"ioi", "oso", "ioi", 
					'i', new ItemStack(Item.ingotIron), 
					's', new ItemStack(Block.stone), 
					'o', new ItemStack(Block.obsidian));
		}
	}
	
	public BlockHardenedStone() {
		super(BlockHardenedStone.BLOCK_ID, Material.iron);

		setHardness(2.5F);
		setResistance(5000.0F);
		setCreativeTab(CreativeTabs.tabBlock);
		setTextureName("ephys.pp:hardenedStone");
	}
}
package nf.fr.ephys.playerproxies.common.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.BlockInterface;
import nf.fr.ephys.playerproxies.common.block.BlockSpawnerLoader;
import nf.fr.ephys.playerproxies.common.item.ItemLinkFocus;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TESpawnerLoader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dan200.computer.api.ComputerCraftAPI;
import dan200.turtle.api.ITurtleUpgrade;
import dan200.turtle.api.TurtleAPI;

public class CommonProxy {
	public void initMod() {
		// Block & TE registry
		PlayerProxies.blockInterface = new BlockInterface();
		PlayerProxies.blockInterface.setUnlocalizedName("PP_UniversalInterface");
		
		PlayerProxies.blockSpawnerLoader = new BlockSpawnerLoader();
		PlayerProxies.blockSpawnerLoader.setUnlocalizedName("PP_SpawnerLoader");
		
		GameRegistry.registerBlock(PlayerProxies.blockInterface, "PP_UniversalInterface");
        GameRegistry.registerTileEntity(TEBlockInterface.class, "PP_UniversalInterface"); 
        
		GameRegistry.registerBlock(PlayerProxies.blockSpawnerLoader, "PP_SpawnerLoader");
        GameRegistry.registerTileEntity(TESpawnerLoader.class, "PP_SpawnerLoader"); 
        
        LanguageRegistry.instance().addName(PlayerProxies.blockInterface, "Universal Interface");
        LanguageRegistry.instance().addName(PlayerProxies.blockSpawnerLoader, "Spawner Loader");
	
        // Item registry
        PlayerProxies.itemLinker = new ItemLinker();
        PlayerProxies.itemLinker.setUnlocalizedName("PP_LinkWand");
        
        PlayerProxies.itemLinkFocus = new ItemLinkFocus();
        PlayerProxies.itemLinkFocus.setUnlocalizedName("PP_LinkFocus");
        
        GameRegistry.registerItem(PlayerProxies.itemLinker, "PP_LinkWand");
        GameRegistry.registerItem(PlayerProxies.itemLinkFocus, "PP_LinkFocus");
        
        LanguageRegistry.instance().addName(PlayerProxies.itemLinker, "Link device");
        LanguageRegistry.instance().addName(PlayerProxies.itemLinkFocus, "Link Focus");
        
        GameRegistry.addRecipe(new ItemStack(PlayerProxies.itemLinkFocus), "ipi", "qeq", "ipi",
        		'e', new ItemStack(Item.emerald),
        		'p', new ItemStack(Item.enderPearl),
        		'q', new ItemStack(Item.eyeOfEnder),
        		'i', new ItemStack(Item.blazePowder)
        );
        
        GameRegistry.addRecipe(new ItemStack(PlayerProxies.itemLinker), " il", " si", "s  ",
        		'l', new ItemStack(PlayerProxies.itemLinkFocus),
        		'i', new ItemStack(Item.ingotIron),
        		's', new ItemStack(Item.stick)
        );

        GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockInterface), "dld", "geg", "dgd",
        		'd', new ItemStack(Item.diamond),
        		'l', new ItemStack(PlayerProxies.itemLinkFocus),
        		'g', new ItemStack(Block.glass),
        		'e', new ItemStack(Block.enderChest)
        );
	}
}

package nf.fr.ephys.playerproxies.common.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.BlockHardenedStone;
import nf.fr.ephys.playerproxies.common.block.BlockInterface;
import nf.fr.ephys.playerproxies.common.block.BlockParticleGenerator;
import nf.fr.ephys.playerproxies.common.block.BlockSpawnerLoader;
import nf.fr.ephys.playerproxies.common.entity.Ghost;
import nf.fr.ephys.playerproxies.common.item.ItemLinkFocus;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TESpawnerLoader;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dan200.computer.api.ComputerCraftAPI;
import dan200.turtle.api.ITurtleUpgrade;
import dan200.turtle.api.TurtleAPI;

public class CommonProxy {
	public void initMod() {
		registerBlocks();
		registerItems();
		registerCrafts();
		registerEntities();
		registerGuis();
	}
	
	private void registerBlocks() {
		PlayerProxies.blockInterface = new BlockInterface();
		PlayerProxies.blockInterface
				.setUnlocalizedName("PP_UniversalInterface");

		PlayerProxies.blockSpawnerLoader = new BlockSpawnerLoader();
		PlayerProxies.blockSpawnerLoader.setUnlocalizedName("PP_SpawnerLoader");

		PlayerProxies.blockHardenedStone = new BlockHardenedStone();
		PlayerProxies.blockHardenedStone.setUnlocalizedName("PP_HardenedStone");

		PlayerProxies.blockParticleGenerator = new BlockParticleGenerator();
		PlayerProxies.blockParticleGenerator.setUnlocalizedName("PP_ParticleGenerator");

		GameRegistry.registerBlock(PlayerProxies.blockInterface,
				"PP_UniversalInterface");
		GameRegistry.registerTileEntity(TEBlockInterface.class,
				"PP_UniversalInterface");

		GameRegistry.registerBlock(PlayerProxies.blockSpawnerLoader,
				"PP_SpawnerLoader");
		GameRegistry.registerTileEntity(TESpawnerLoader.class,
				"PP_SpawnerLoader");

		GameRegistry.registerBlock(PlayerProxies.blockHardenedStone,
				"PP_HardenedStone");

		GameRegistry.registerBlock(PlayerProxies.blockParticleGenerator,
				"PP_ParticleGenerator");
		
		LanguageRegistry.instance().addName(PlayerProxies.blockInterface,
				"Universal Interface");
		LanguageRegistry.instance().addName(PlayerProxies.blockSpawnerLoader,
				"Ghost Stabilizer");
		LanguageRegistry.instance().addName(PlayerProxies.blockHardenedStone,
				"Hardened Stone");
		LanguageRegistry.instance().addName(PlayerProxies.blockParticleGenerator,
				"Particle Generator");
	}
	
	private void registerItems() {
		PlayerProxies.itemLinker = new ItemLinker();
		PlayerProxies.itemLinker.setUnlocalizedName("PP_LinkWand");

		PlayerProxies.itemLinkFocus = new ItemLinkFocus();
		PlayerProxies.itemLinkFocus.setUnlocalizedName("PP_LinkFocus");

		GameRegistry.registerItem(PlayerProxies.itemLinker, "PP_LinkWand");
		GameRegistry.registerItem(PlayerProxies.itemLinkFocus, "PP_LinkFocus");

		LanguageRegistry.instance().addName(PlayerProxies.itemLinker,
				"Link device");
		LanguageRegistry.instance().addName(PlayerProxies.itemLinkFocus,
				"Link Focus");
	}
	
	private void registerCrafts() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.itemLinkFocus),
				"ipi", "qeq", "ipi", 'e', new ItemStack(Item.emerald), 'p',
				new ItemStack(Item.enderPearl), 'q', new ItemStack(
						Item.eyeOfEnder), 'i', new ItemStack(Item.blazePowder));

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.itemLinker), " il",
				" si", "s  ", 'l', new ItemStack(PlayerProxies.itemLinkFocus),
				'i', new ItemStack(Item.ingotIron), 's', new ItemStack(
						Item.stick));

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockInterface),
				"dld", "geg", "dgd", 'd', new ItemStack(Item.diamond), 'l',
				new ItemStack(PlayerProxies.itemLinkFocus), 'g', new ItemStack(
						Block.glass), 'e', new ItemStack(Block.enderChest));

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockHardenedStone),
				"ioi", "oso", "ioi", 'i', new ItemStack(Item.ingotIron), 's',
				new ItemStack(Block.stone), 'o', new ItemStack(Block.obsidian));

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockSpawnerLoader),
				"hlh", "hdh", "hhh", 'h', new ItemStack(
						PlayerProxies.blockHardenedStone), 'l', new ItemStack(
						PlayerProxies.itemLinkFocus), 'd', new ItemStack(
						Item.diamond));
	}
	
	private void registerEntities() {
		EntityRegistry.registerModEntity(Ghost.class, "PP_Ghost",
				EntityRegistry.findGlobalUniqueEntityId(),
				PlayerProxies.instance, 100, 20, true);
	}
	
	private void registerGuis() {
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
	}
}

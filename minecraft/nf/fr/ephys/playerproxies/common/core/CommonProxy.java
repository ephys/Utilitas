package nf.fr.ephys.playerproxies.common.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.BlockBiomeScanner;
import nf.fr.ephys.playerproxies.common.block.BlockBaseShineyGlass;
import nf.fr.ephys.playerproxies.common.block.BlockGravitationalField;
import nf.fr.ephys.playerproxies.common.block.BlockHardenedStone;
import nf.fr.ephys.playerproxies.common.block.BlockHomeShield;
import nf.fr.ephys.playerproxies.common.block.BlockItemTicker;
import nf.fr.ephys.playerproxies.common.block.BlockParticleGenerator;
import nf.fr.ephys.playerproxies.common.block.BlockProximitySensor;
import nf.fr.ephys.playerproxies.common.block.BlockSpawnerLoader;
import nf.fr.ephys.playerproxies.common.block.BlockToughwoodPlank;
import nf.fr.ephys.playerproxies.common.enchantment.EnchantmentNoVoidFog;
import nf.fr.ephys.playerproxies.common.item.ItemBiomeStorage;
import nf.fr.ephys.playerproxies.common.item.ItemDebug;
import nf.fr.ephys.playerproxies.common.item.ItemLinkFocus;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import nf.fr.ephys.playerproxies.common.openperipheral.AdaptorGravitationalField;
import nf.fr.ephys.playerproxies.common.openperipheral.AdaptorProximitySensor;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntitySpawnerLoader;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeReplicator;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityItemTicker;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityProximitySensor;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy {
	public void preInit() {
		//LanguageRegistry.instance().addStringLocalization("itemGroup.playerProxies", "en_US", "Player Proxies");
	}
	
	public void init() {
		registerBlocks();
		registerItems();
		
		//EnchantmentNoVoidFog.register();
	}
	
	public void postInit() {
		registerHandlers();
		registerCrafts();
		
		if (BlockHomeShield.requiresTwilightForest)
			BlockHomeShield.register();
	}

	private void registerBlocks() {
		// BlockSpawnerLoader.register();
		BlockHardenedStone.register();
		BlockParticleGenerator.register();
		BlockBaseShineyGlass.register();
		BlockProximitySensor.register();
		BlockBiomeScanner.register();
		BlockToughwoodPlank.register();
		BlockItemTicker.register();
		BlockGravitationalField.register();
		
		if (!BlockHomeShield.requiresTwilightForest)
			BlockHomeShield.register();
	}
	
	public void registerCrafts() {
		// BlockSpawnerLoader.registerCraft();
		BlockHardenedStone.registerCraft();
		BlockParticleGenerator.registerCraft();
		BlockBaseShineyGlass.registerCraft();
		BlockProximitySensor.registerCraft();
		BlockBiomeScanner.registerCraft();
		BlockToughwoodPlank.registerCraft();
		BlockItemTicker.registerCraft();
		BlockGravitationalField.registerCraft();
		BlockHomeShield.registerCraft();

		ItemLinkFocus.registerCraft();
		ItemLinker.registerCraft();
	}

	private void registerItems() {
		ItemLinkFocus.register();
		ItemLinker.register();
		ItemBiomeStorage.register();

		if (PlayerProxies.DEV_MODE) {
			ItemDebug.register();
		}
	}

	private void registerHandlers() {
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		
		if (Loader.isModLoaded("OpenPeripheralCore")) {
			AdaptorGravitationalField.register();
			AdaptorProximitySensor.register();
		}
	}
}

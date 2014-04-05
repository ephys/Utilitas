package nf.fr.ephys.playerproxies.common.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.BlockBiomeReplicator;
import nf.fr.ephys.playerproxies.common.block.BlockBiomeScanner;
import nf.fr.ephys.playerproxies.common.block.BlockEtherealGlass;
import nf.fr.ephys.playerproxies.common.block.BlockGravitationalField;
import nf.fr.ephys.playerproxies.common.block.BlockHardenedStone;
import nf.fr.ephys.playerproxies.common.block.BlockInterface;
import nf.fr.ephys.playerproxies.common.block.BlockItemTicker;
import nf.fr.ephys.playerproxies.common.block.BlockParticleGenerator;
import nf.fr.ephys.playerproxies.common.block.BlockProximitySensor;
import nf.fr.ephys.playerproxies.common.block.BlockSpawnerLoader;
import nf.fr.ephys.playerproxies.common.block.BlockToughwoodPlank;
import nf.fr.ephys.playerproxies.common.entity.Ghost;
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
import dan200.computer.api.ComputerCraftAPI;
import dan200.turtle.api.ITurtleUpgrade;
import dan200.turtle.api.TurtleAPI;

public class CommonProxy {
	public void initMod() {
		registerBlocks();
		registerItems();
		registerHandlers();
	}

	private void registerBlocks() {
		BlockInterface.register();
		// BlockSpawnerLoader.register();
		BlockHardenedStone.register();
		BlockParticleGenerator.register();
		BlockEtherealGlass.register();
		BlockProximitySensor.register();
		BlockBiomeReplicator.register();
		BlockBiomeScanner.register();
		BlockToughwoodPlank.register();
		BlockItemTicker.register();
		BlockGravitationalField.register();
	}
	
	public void registerCrafts() {
		BlockInterface.registerCraft();
		// BlockSpawnerLoader.registerCraft();
		BlockHardenedStone.registerCraft();
		BlockParticleGenerator.registerCraft();
		BlockEtherealGlass.registerCraft();
		BlockProximitySensor.registerCraft();
		BlockBiomeReplicator.registerCraft();
		BlockBiomeScanner.registerCraft();
		BlockToughwoodPlank.registerCraft();
		BlockItemTicker.registerCraft();
		BlockGravitationalField.registerCraft();

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

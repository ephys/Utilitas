package nf.fr.ephys.playerproxies.common;

import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
import nf.fr.ephys.playerproxies.common.block.BlockBiomeReplicator;
import nf.fr.ephys.playerproxies.common.block.BlockBiomeScanner;
import nf.fr.ephys.playerproxies.common.block.BlockEtherealGlass;
import nf.fr.ephys.playerproxies.common.block.BlockHardenedStone;
import nf.fr.ephys.playerproxies.common.block.BlockInterface;
import nf.fr.ephys.playerproxies.common.block.BlockItemTicker;
import nf.fr.ephys.playerproxies.common.block.BlockParticleGenerator;
import nf.fr.ephys.playerproxies.common.block.BlockProximitySensor;
import nf.fr.ephys.playerproxies.common.block.BlockSpawnerLoader;
import nf.fr.ephys.playerproxies.common.block.BlockToughwoodPlank;
import nf.fr.ephys.playerproxies.common.core.CommonProxy;
import nf.fr.ephys.playerproxies.common.core.PacketHandler;
import nf.fr.ephys.playerproxies.common.item.ItemBiomeStorage;
import nf.fr.ephys.playerproxies.common.item.ItemLinkFocus;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "ephys.playerproxies", name = "Player Proxies",
	 version = PlayerProxies.version
)

@NetworkMod(clientSideRequired = true,
			serverSideRequired = false,
			channels = {
				"PlayerProxies"
			},
			packetHandler = PacketHandler.class
)

public class PlayerProxies {
	public static final String version = "1.6.4-0.0.2";

	public static final int GUI_UNIVERSAL_INTERFACE = 0;
	public static final int GUI_BIOME_SCANNER = 1;
	
	public static final boolean DEV_MODE = true;
	private static boolean REQUIRES_POWER = false;
	
	public static boolean requiresPower() {
		return REQUIRES_POWER;
	}

	@Instance("ephys.playerproxies")
    public static PlayerProxies instance;

	@SidedProxy(clientSide = "nf.fr.ephys.playerproxies.client.core.ClientProxy", 
				serverSide = "nf.fr.ephys.playerproxies.common.core.CommonProxy")
    public static CommonProxy proxy;

	private static Logger logger;

	// blocks
	public static BlockInterface blockInterface;
	public static BlockSpawnerLoader blockSpawnerLoader;
	public static BlockHardenedStone blockHardenedStone;
	public static BlockParticleGenerator blockParticleGenerator;
	public static BlockEtherealGlass blockEtherealGlass;
	public static BlockProximitySensor blockProximitySensor;
	public static BlockBiomeReplicator blockBiomeChanger;
	public static BlockBiomeScanner blockBiomeScanner;
	public static BlockToughwoodPlank blockToughwoodPlank;
	public static BlockItemTicker blockItemTicker;

	// items
	public static ItemLinker itemLinker;
	public static ItemLinkFocus itemLinkFocus;
	public static ItemBiomeStorage itemBiomeStorage;

    @EventHandler
    public void preLoad(FMLPreInitializationEvent event) {
        logger = Logger.getLogger("ephys.playerproxies");
        logger.setParent(FMLLog.getLogger());
        
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        
        ItemLinker.ITEM_ID = config.getItem("ItemLinker", ItemLinker.ITEM_ID).getInt();
        ItemLinkFocus.ITEM_ID = config.getItem("ItemLinkFocus", ItemLinkFocus.ITEM_ID).getInt();
        ItemBiomeStorage.ITEM_ID = config.getItem("ItemBiomeStorage", ItemBiomeStorage.ITEM_ID).getInt();
        
        BlockInterface.BLOCK_ID = config.getBlock("BlockInterface", BlockInterface.BLOCK_ID).getInt();
        BlockSpawnerLoader.BLOCK_ID = config.getBlock("BlockSpawnerLoader", BlockSpawnerLoader.BLOCK_ID).getInt();
        BlockHardenedStone.BLOCK_ID = config.getBlock("BlockHardenedStone", BlockHardenedStone.BLOCK_ID).getInt();
        BlockParticleGenerator.BLOCK_ID = config.getBlock("BlockParticleGenerator", BlockParticleGenerator.BLOCK_ID).getInt();
        BlockEtherealGlass.BLOCK_ID = config.getBlock("BlockEtherealGlass", BlockEtherealGlass.BLOCK_ID).getInt();
        BlockProximitySensor.BLOCK_ID = config.getBlock("BlockProximitySensor", BlockProximitySensor.BLOCK_ID).getInt();
        BlockBiomeReplicator.BLOCK_ID = config.getBlock("BlockBiomeReplicator", BlockBiomeReplicator.BLOCK_ID).getInt();
        BlockBiomeScanner.BLOCK_ID = config.getBlock("BlockBiomeScanner", BlockBiomeScanner.BLOCK_ID).getInt();
        BlockToughwoodPlank.BLOCK_ID = config.getBlock("BlockToughwoodPlank", BlockToughwoodPlank.BLOCK_ID).getInt();
        BlockItemTicker.BLOCK_ID = config.getBlock("BlockItemTicker", BlockItemTicker.BLOCK_ID).getInt();

        config.save();
    }
    
    public static Logger getLogger() {
    	return logger;
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.initMod();
    }
    
    @EventHandler
    public void postLoad(FMLPostInitializationEvent event) {
    	proxy.registerCrafts();
    	
    	REQUIRES_POWER = Loader.isModLoaded("ThermalExpansion");
    }
}
package nf.fr.ephys.playerproxies.common;

import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
import nf.fr.ephys.playerproxies.common.block.BlockHardenedStone;
import nf.fr.ephys.playerproxies.common.block.BlockInterface;
import nf.fr.ephys.playerproxies.common.block.BlockSpawnerLoader;
import nf.fr.ephys.playerproxies.common.core.CommonProxy;
import nf.fr.ephys.playerproxies.common.core.PacketHandler;
import nf.fr.ephys.playerproxies.common.item.ItemLinkFocus;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "ephys.playerproxies", name = "Player Proxies",
	 version = PlayerProxies.version
)

@NetworkMod(clientSideRequired = true,
			serverSideRequired = false,
			channels = {
				"PP_enderToggle"
			},
			packetHandler = PacketHandler.class
)

public class PlayerProxies {
	public static final String version = "1.6.4-0.0.1";

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

	// items
	public static ItemLinker itemLinker;
	public static ItemLinkFocus itemLinkFocus;

    @EventHandler
    public void preLoad(FMLPreInitializationEvent event) {
        logger = Logger.getLogger("ephys.playerproxies");
        logger.setParent(FMLLog.getLogger());
        
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        
        ItemLinker.itemID = config.getItem("ItemLinker", ItemLinker.itemID).getInt();
        ItemLinkFocus.itemID = config.getItem("ItemLinkFocus", ItemLinkFocus.itemID).getInt();
        
        BlockInterface.blockID = config.getBlock("BlockInterface", BlockInterface.blockID).getInt();
        BlockSpawnerLoader.blockID = config.getBlock("BlockSpawnerLoader", BlockSpawnerLoader.blockID).getInt();
        BlockHardenedStone.blockID = config.getBlock("BlockHardenedStone", BlockHardenedStone.blockID).getInt();
        
        config.save();
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.initMod();
    }
}
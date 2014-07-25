package nf.fr.ephys.playerproxies.common;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;
import nf.fr.ephys.playerproxies.client.gui.GuiModConfigFactory;
import nf.fr.ephys.playerproxies.common.block.*;
import nf.fr.ephys.playerproxies.common.command.CommandNickname;
import nf.fr.ephys.playerproxies.common.core.CommonProxy;
import nf.fr.ephys.playerproxies.common.core.ConfigHandler;
import nf.fr.ephys.playerproxies.common.core.IMCHandler;
import nf.fr.ephys.playerproxies.common.item.*;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

@Mod(modid = PlayerProxies.MODID, name = PlayerProxies.NAME,
	version = PlayerProxies.VERSION, dependencies="after:IC2,TwilightForest",
	guiFactory = GuiModConfigFactory.CLASSNAME
)
public class PlayerProxies extends DummyModContainer {
	public static final String VERSION = "1.7.10-1.0.1";
	public static final String MODID = "ephys.playerproxies";
	public static final String NAME = "Player Proxies";

	public static final int GUI_BIOME_SCANNER = 1;

	public static final boolean DEV_MODE = false;

	@Instance("ephys.playerproxies")
	public static PlayerProxies instance;

	@SidedProxy(clientSide = "nf.fr.ephys.playerproxies.client.core.ClientProxy",
				serverSide = "nf.fr.ephys.playerproxies.common.core.CommonProxy")
	public static CommonProxy proxy;

	private Logger logger;
	private ConfigHandler config;
	private SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static final CreativeTabs creativeTab = new CreativeTabs(MODID) {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(Blocks.proximitySensor);
		}
	};

	public static class Blocks {
		public static BlockHardenedStone hardenedStone;
		public static BlockParticleGenerator particleGenerator;
		public static BlockBaseShineyGlass baseShineyGlass;
		public static BlockProximitySensor proximitySensor;
		public static BlockBiomeScanner biomeScanner;
		public static BlockToughwoodPlank toughwoodPlank;
		public static BlockItemTicker itemTicker;
		public static BlockGravitationalField gravitationalField;
		public static BlockHomeShield homeShield;
		public static BlockBeaconTierII betterBeacon;
		public static BlockDragonscale dragonScale;

		public static BlockEnderDragonSpawner dragonSpawner;
	}

	public static class Items {
		public static ItemLinker linkDevice;
		public static Item linkFocus;
		public static ItemBiomeStorage biomeStorage;
		public static ItemPotionDiffuser potionDiffuser;

		public static ItemUnemptyingBucket unemptyingBucket;
		public static ItemDragonScale dragonScale;
		public static ItemDragonScale dragonScaleIngot;
		public static ItemDragonPickaxe dragonPickaxe;
		public static ItemDragonHoe dragonHoe;
		public static Item.ToolMaterial matDragonScale = EnumHelper.addToolMaterial("DRAGONSCALE", 4, 1200, 10F, 8F, 0);
	}

	public static class Enchantments {}

	public PlayerProxies() {
		super(new ModMetadata());

		ModMetadata meta = getMetadata();
		meta.modId = MODID;
		meta.name = NAME;
		meta.version = VERSION;
		meta.authorList = Arrays.asList("ephyspotato");
		meta.url = "https://github.com/Ephys/PlayerProxies";
		//meta.description = ""; // that desc sucks
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new ConfigHandler(event.getSuggestedConfigurationFile());
		config.syncConfig();

		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandNickname());
	}

	@EventHandler
	public void imcCallback(FMLInterModComms.IMCEvent event) {
		for (FMLInterModComms.IMCMessage message : event.getMessages()) {
			IMCHandler.handle(message);
		}
	}

	public static Logger getLogger() {
		return instance.logger;
	}

	public static SimpleNetworkWrapper getNetHandler() {
		return instance.netHandler;
	}

	public static ConfigHandler getConfig() { return instance.config; }
}
package be.ephys.utilitas.common;

import be.ephys.utilitas.common.block.BlockShinyGlass;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

@Mod(modid = Utilitas.MODID, name = Utilitas.NAME,
	version = Utilitas.VERSION, dependencies="after:required-after:cookiecore@[1.0.0,);required-after:Forge@[12.18.3.2185,)",
	guiFactory = GuiModConfigFactory.CLASSNAME
)
public class Utilitas {
	public static final String VERSION = "1.10.2-2.0.0";
	public static final String MODID = "utilitas";
	public static final String NAME = "Utilitas";

	public static final int GUI_FLUID_HOPPER = 0;
	public static final int GUI_BIOME_SCANNER = 1;

	public static final boolean DEV_MODE = false;

	@Mod.Instance(Utilitas.MODID)
	public static Utilitas instance;

	@SidedProxy(clientSide = "be.ephysnf.fr.be.ephys.utilitas.client.core.ClientProxy",
				serverSide = "nf.fr.be.ephys.utilitas.common.core.CommonProxy")
	public static CommonProxy proxy;

	private Logger logger;
	private ConfigHandler config;
	public static final SimpleNetworkWrapper NET_HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static final CreativeTabs creativeTab = new CreativeTabs(MODID) {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(Blocks.shinyGlass);
		}
	};

	public static class Blocks {
		public static Block hardenedStone;
		public static BlockFluidDiffuser fluidDiffuser;
		public static BlockParticleGenerator particleGenerator;
		public static BlockShinyGlass shinyGlass;
		public static BlockProximitySensor proximitySensor;
		public static BlockBiomeScanner biomeScanner;
		public static BlockToughwoodPlank toughwoodPlank;
		public static BlockItemTicker itemTicker;
		public static BlockGravitationalField gravitationalField;
		public static BlockHomeShield homeShield;
		public static BlockBeaconTierII betterBeacon;
		public static BlockDragonscale dragonScale;
		public static BlockFluidHopper fluidHopper;

		public static BlockEnderDragonSpawner dragonSpawner;
	}

	public static class Items {
		public static ItemInterfaceUpgrade interfaceUpgrade;
		public static ItemLinker linkDevice;
		public static Item linkFocus;
		public static ItemBiomeStorage biomeStorage;
		public static ItemPotionDiffuser potionDiffuser;

		public static ItemUnemptyingBucket unemptyingBucket;
		public static ItemDragonScale dragonScale;
		public static ItemDragonScale dragonScaleIngot;
		public static ItemDragonPickaxe dragonPickaxe;
		public static ItemDragonHoe dragonHoe;
		public static ItemDragonWand dragonWand;

		public static Item.ToolMaterial matDragonScale = EnumHelper.addToolMaterial("DRAGONSCALE", 4, 1300, 10F, 8F, 0);
	}

	public Utilitas() {
		super(new ModMetadata());

		ModMetadata meta = getMetadata();
		meta.modId = MODID;
		meta.name = NAME;
		meta.version = VERSION;
		meta.authorList = Arrays.asList("ephyspotato");
		meta.url = "https://github.com/Ephys/Utilitas";
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
		proxy.serverStarting(event);
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

	public static ConfigHandler getConfig() { return instance.config; }
}

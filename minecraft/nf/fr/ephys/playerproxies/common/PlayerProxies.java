package nf.fr.ephys.playerproxies.common;

import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
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
import nf.fr.ephys.playerproxies.common.command.NicknameCommand;
import nf.fr.ephys.playerproxies.common.core.CommonProxy;
import nf.fr.ephys.playerproxies.common.core.PacketHandler;
import nf.fr.ephys.playerproxies.common.enchantment.EnchantmentNoVoidFog;
import nf.fr.ephys.playerproxies.common.item.ItemBiomeStorage;
import nf.fr.ephys.playerproxies.common.item.ItemLinkFocus;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = PlayerProxies.modid, name = PlayerProxies.name,
	 version = PlayerProxies.version, dependencies="after:IC2,TwilightForest,OpenPeripheralCore" 
)

@NetworkMod(clientSideRequired = true,
			serverSideRequired = false,
			channels = {
				"PlayerProxies"
			},
			packetHandler = PacketHandler.class
)

public class PlayerProxies extends DummyModContainer {
	public static final String version = "1.6.4-0.1.2";
	public static final String modid = "ephys.playerproxies";
	public static final String name = "Player Proxies";

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
	
	public static final CreativeTabs creativeTab = new CreativeTabs("playerProxies") {
		public ItemStack getIconItemStack() {
			return new ItemStack(Blocks.proximitySensor, 1, 0);
		}
	};

	public static final class Blocks {
		public static BlockSpawnerLoader spawnerLoader;
		public static BlockHardenedStone hardenedStone;
		public static BlockParticleGenerator particleGenerator;
		public static BlockBaseShineyGlass baseShineyGlass;
		public static BlockProximitySensor proximitySensor;
		public static BlockBiomeScanner biomeScanner;
		public static BlockToughwoodPlank toughwoodPlank;
		public static BlockItemTicker itemTicker;
		public static BlockGravitationalField gravitationalField;
		public static BlockHomeShield homeShield;
	}

	public static final class Items {
		public static ItemLinker linkeDevice;
		public static ItemLinkFocus linkFocus;
		public static ItemBiomeStorage biomeStorage;
	}

	public static final class Enchantments {
		public static EnchantmentNoVoidFog noVoidFog;
	}
	
	public PlayerProxies() {
		super(new ModMetadata());

		ModMetadata meta = getMetadata();
		meta.modId = this.modid;
		meta.name = this.name;
		meta.version = this.version;
		meta.authorList = Arrays.asList("ephyspotato", "Mikee");
		meta.url = "https://github.com/Ephys/PlayerProxies";
		meta.description = "Must. Have. More. Blocks.";
	}
	
	@EventHandler
	public void preLoad(FMLPreInitializationEvent event) {
		logger = Logger.getLogger("ephys.playerproxies");
		logger.setParent(FMLLog.getLogger());

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		ItemLinker.ITEM_ID = config.getItem("ItemLinker", ItemLinker.ITEM_ID).getInt();
		ItemLinkFocus.ITEM_ID = config.getItem("ItemLinkFocus", ItemLinkFocus.ITEM_ID).getInt();
		ItemBiomeStorage.ITEM_ID = config.getItem("ItemBiomeStorage", ItemBiomeStorage.ITEM_ID).getInt();

		BlockBaseShineyGlass.BLOCK_ID = config.getBlock("BlockBaseShineyGlass", BlockBaseShineyGlass.BLOCK_ID).getInt();

		BlockBiomeScanner.BLOCK_ID = config.getBlock("BlockBiomeScanner", BlockBiomeScanner.BLOCK_ID).getInt();
		BlockToughwoodPlank.BLOCK_ID = config.getBlock("BlockToughwoodPlank", BlockToughwoodPlank.BLOCK_ID).getInt();

		BlockHardenedStone.BLOCK_ID = config.getBlock("BlockHardenedStone", BlockHardenedStone.BLOCK_ID).getInt();
		BlockProximitySensor.BLOCK_ID = config.getBlock("BlockProximitySensor", BlockProximitySensor.BLOCK_ID).getInt();

		BlockParticleGenerator.BLOCK_ID = config.getBlock("BlockParticleGenerator", BlockParticleGenerator.BLOCK_ID).getInt();
		BlockItemTicker.BLOCK_ID = config.getBlock("BlockItemTicker", BlockItemTicker.BLOCK_ID).getInt();
		BlockGravitationalField.BLOCK_ID = config.getBlock("BlockGravitationalField", BlockGravitationalField.BLOCK_ID).getInt();
		BlockSpawnerLoader.BLOCK_ID = config.getBlock("BlockSpawnerLoader", BlockSpawnerLoader.BLOCK_ID).getInt();
		
		BlockHomeShield.BLOCK_ID = config.getBlock("BlockHomeShield", BlockHomeShield.BLOCK_ID).getInt();
		
		Property property = config.get("BlockProperties", "HomeshieldRequiresTwilightForest", BlockHomeShield.requiresTwilightForest);
		property.comment = "True: Will overwrite the TF Stronghold Shield to add a locked state [unbreakable but unlockable]. \nFalse: Will add a new Shield Block having that behavior";
		BlockHomeShield.requiresTwilightForest = property.getBoolean(BlockHomeShield.requiresTwilightForest);

		property = config.get("BlockProperties", "HomeshieldRequiresSilkTouch", BlockHomeShield.requiresSilkTouch);
		property.comment = "True if the shield only drops if silk touched";
		BlockHomeShield.requiresSilkTouch = property.getBoolean(BlockHomeShield.requiresSilkTouch);

		property = config.get("Enchantments", "NoVoidFodID", EnchantmentNoVoidFog.ENCHANTMENT_ID);
		EnchantmentNoVoidFog.ENCHANTMENT_ID = property.getInt(EnchantmentNoVoidFog.ENCHANTMENT_ID);
		
		config.save();
		
		proxy.preInit();
	}
	
	public static Logger getLogger() {
		return logger;
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.init();
	}
	
	@EventHandler
	public void postLoad(FMLPostInitializationEvent event) {
		proxy.postInit();

		REQUIRES_POWER = Loader.isModLoaded("ThermalExpansion");
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new NicknameCommand());
	}
}
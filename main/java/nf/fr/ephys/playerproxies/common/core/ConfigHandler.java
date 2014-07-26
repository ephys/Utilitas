package nf.fr.ephys.playerproxies.common.core;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.*;
import nf.fr.ephys.playerproxies.common.command.CommandNickname;
import nf.fr.ephys.playerproxies.common.item.ItemDragonHoe;
import nf.fr.ephys.playerproxies.common.item.ItemDragonPickaxe;
import nf.fr.ephys.playerproxies.common.item.ItemPotionDiffuser;
import nf.fr.ephys.playerproxies.common.item.ItemUnemptyingBucket;

import java.io.File;

public class ConfigHandler {
	public final Configuration CONFIG;

	public ConfigHandler(File location) {
		CONFIG = new Configuration(location);
		CONFIG.load();
	}

	private boolean requiresPower = false;
	private boolean opEnderPearls = true;
	private boolean addDragonEggTab = true;

	public boolean requiresPower() { return requiresPower; }
	public boolean areEnderPearlsOP() { return opEnderPearls; }
	public boolean addDragonEggTab() { return addDragonEggTab; }

	public static final String PROPERTIES = "Properties";
	public static final String VANILLA_TWEAKS = "Vanilla Tweaks";
	public static final String MODULES = "Enabled Modules";

	public void syncConfig() {
		Property property;

		// MODULES
		ConfigCategory modules = CONFIG.getCategory(MODULES);
		modules.setRequiresMcRestart(true);
		modules.setComment("Set to false to disable a feature");

		BlockBaseShineyGlass.interfaceEnabled = CONFIG.get(MODULES, "Universal Interface", BlockBaseShineyGlass.interfaceEnabled).getBoolean(BlockBaseShineyGlass.interfaceEnabled);
		BlockBeaconTierII.overwrite = CONFIG.get(MODULES, "Vanilla beacon overwrite", BlockBeaconTierII.overwrite).getBoolean(BlockBeaconTierII.overwrite);
		BlockBiomeScanner.enabled = CONFIG.get(MODULES, "Biome Scanner", BlockBiomeScanner.enabled).getBoolean(BlockBiomeScanner.enabled);
		BlockToughwoodPlank.transmuterEnabled = CONFIG.get(MODULES, "Biome Transmuter", BlockToughwoodPlank.transmuterEnabled).getBoolean(BlockToughwoodPlank.transmuterEnabled);

		BlockGravitationalField.enabled = CONFIG.get(MODULES, "Gravitational Field Handler", BlockGravitationalField.enabled).getBoolean(BlockGravitationalField.enabled);
		BlockHardenedStone.diffuserEnabled = CONFIG.get(MODULES, "Fluid diffuser", BlockHardenedStone.diffuserEnabled).getBoolean(BlockHardenedStone.diffuserEnabled);
		BlockParticleGenerator.enabled = CONFIG.get(MODULES, "Particle generator", BlockParticleGenerator.enabled).getBoolean(BlockParticleGenerator.enabled);
		BlockProximitySensor.enabled = CONFIG.get(MODULES, "Proximity Sensor", BlockProximitySensor.enabled).getBoolean(BlockProximitySensor.enabled);

		BlockHomeShield.enabled = CONFIG.get(MODULES, "HomeShield", BlockHomeShield.enabled).getBoolean(BlockHomeShield.enabled);
		BlockItemTicker.enabled = CONFIG.get(MODULES, "Sylladex", BlockItemTicker.enabled).getBoolean(BlockItemTicker.enabled);

		CommandNickname.enabled = CONFIG.get(MODULES, "nickname command", CommandNickname.enabled).getBoolean(CommandNickname.enabled);

		ItemDragonPickaxe.enabled = CONFIG.get(MODULES, "DragonScale pickaxe", ItemDragonPickaxe.enabled).getBoolean(ItemDragonPickaxe.enabled);
		ItemDragonHoe.enabled = CONFIG.get(MODULES, "DragonScale hoe", ItemDragonHoe.enabled).getBoolean(ItemDragonHoe.enabled);
		ItemUnemptyingBucket.enabled = CONFIG.get(MODULES, "Omnibucket", ItemUnemptyingBucket.enabled).getBoolean(ItemUnemptyingBucket.enabled);
		ItemPotionDiffuser.enabled = CONFIG.get(MODULES, "Handheld potion diffuser", ItemPotionDiffuser.enabled).getBoolean(ItemPotionDiffuser.enabled);

		// PROPERTIES
		property = CONFIG.get(PROPERTIES, "Homeshield Requires Twilight Forest", BlockHomeShield.requiresTwilightForest, "True: Will overwrite the TF Stronghold Shield to add a locked state [unbreakable but unlockable]. \nFalse: Will add a new Shield Block having that behavior");
		property.setRequiresMcRestart(true);
		BlockHomeShield.requiresTwilightForest = property.getBoolean(BlockHomeShield.requiresTwilightForest);

		BlockHomeShield.requiresSilkTouch = CONFIG.get(PROPERTIES, "Homeshield Requires Silk Touch", BlockHomeShield.requiresSilkTouch, "True if the shield only drops if silk touched").getBoolean(BlockHomeShield.requiresSilkTouch);
		requiresPower = CONFIG.get(PROPERTIES, "Requires Energy", requiresPower).getBoolean(requiresPower);

		ItemUnemptyingBucket.range = CONFIG.get(PROPERTIES, "Omnibucket bound range", ItemUnemptyingBucket.range, "set to -1 to remove range limit").getInt(ItemUnemptyingBucket.range);
		ItemUnemptyingBucket.crossDim = CONFIG.get(PROPERTIES, "Omnibucket bound works cross-dim", ItemUnemptyingBucket.crossDim).getBoolean(ItemUnemptyingBucket.crossDim);

		// VANILLA_TWEAKS
		opEnderPearls = CONFIG.get(VANILLA_TWEAKS, "Ender Pearls Are OP", opEnderPearls, "EnderPearls don't damage users").getBoolean(opEnderPearls);
		addDragonEggTab = CONFIG.get(VANILLA_TWEAKS, "Add dragon egg to creative", addDragonEggTab).getBoolean(addDragonEggTab);

		CONFIG.load();

		if (CONFIG.hasChanged())
			CONFIG.save();

		PlayerProxies.proxy.onConfigChanges(this);
	}

	@SubscribeEvent
	public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (!event.modID.equals(PlayerProxies.MODID)) return;

		syncConfig();
	}
}
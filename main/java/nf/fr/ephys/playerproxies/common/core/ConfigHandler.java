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
		ConfigCategory modules = CONFIG.getCategory(MODULES.toLowerCase());
		modules.setRequiresMcRestart(true);
		modules.setComment("Set to false to disable a feature");

		BlockBaseShineyGlass.interfaceEnabled = CONFIG.get(MODULES, "Universal Interface", true).getBoolean(BlockBaseShineyGlass.interfaceEnabled);
		BlockBeaconTierII.overwrite = CONFIG.get(MODULES, "Vanilla beacon overwrite", true).getBoolean(BlockBeaconTierII.overwrite);
		BlockBiomeScanner.enabled = CONFIG.get(MODULES, "Biome Scanner", true).getBoolean(BlockBiomeScanner.enabled);
		BlockToughwoodPlank.transmuterEnabled = CONFIG.get(MODULES, "Biome Transmuter", true).getBoolean(BlockToughwoodPlank.transmuterEnabled);

		BlockGravitationalField.enabled = CONFIG.get(MODULES, "Gravitational Field Handler", true).getBoolean(BlockGravitationalField.enabled);
		BlockFluidDiffuser.enabled = CONFIG.get(MODULES, "Fluid diffuser", true).getBoolean(BlockFluidDiffuser.enabled);
		BlockParticleGenerator.enabled = CONFIG.get(MODULES, "Particle generator", true).getBoolean(BlockParticleGenerator.enabled);
		BlockProximitySensor.enabled = CONFIG.get(MODULES, "Proximity Sensor", true).getBoolean(BlockProximitySensor.enabled);

		BlockHomeShield.enabled = CONFIG.get(MODULES, "HomeShield", true).getBoolean(BlockHomeShield.enabled);
		BlockItemTicker.enabled = CONFIG.get(MODULES, "Sylladex", true).getBoolean(BlockItemTicker.enabled);
		BlockFluidHopper.enabled = CONFIG.get(MODULES, "Fluid Hopper", true).getBoolean(BlockFluidHopper.enabled);

		CommandNickname.enabled = CONFIG.get(MODULES, "nickname command", true).getBoolean(CommandNickname.enabled);

		ItemDragonPickaxe.enabled = CONFIG.get(MODULES, "DragonScale pickaxe", true).getBoolean(ItemDragonPickaxe.enabled);
		ItemDragonHoe.enabled = CONFIG.get(MODULES, "DragonScale hoe", true).getBoolean(ItemDragonHoe.enabled);
		ItemUnemptyingBucket.enabled = CONFIG.get(MODULES, "Omnibucket", true).getBoolean(ItemUnemptyingBucket.enabled);
		ItemPotionDiffuser.enabled = CONFIG.get(MODULES, "Handheld potion diffuser", true).getBoolean(ItemPotionDiffuser.enabled);

		// PROPERTIES
		property = CONFIG.get(PROPERTIES, "Homeshield Requires Twilight Forest", true, "True: Will overwrite the TF Stronghold Shield to add a locked state [unbreakable but unlockable]. \nFalse: Will add a new Shield Block having that behavior");
		property.setRequiresMcRestart(true);
		BlockHomeShield.requiresTwilightForest = property.getBoolean(BlockHomeShield.requiresTwilightForest);

		BlockHomeShield.requiresSilkTouch = CONFIG.get(PROPERTIES, "Homeshield Requires Silk Touch", false, "True if the shield only drops if silk touched").getBoolean(BlockHomeShield.requiresSilkTouch);
		requiresPower = CONFIG.get(PROPERTIES, "Requires Energy", false).getBoolean(requiresPower);

		ItemUnemptyingBucket.range = CONFIG.get(PROPERTIES, "Omnibucket bound range", 16, "set to -1 to remove range limit").getInt(ItemUnemptyingBucket.range);
		ItemUnemptyingBucket.crossDim = CONFIG.get(PROPERTIES, "Omnibucket bound works cross-dim", false).getBoolean(ItemUnemptyingBucket.crossDim);

		// VANILLA_TWEAKS
		opEnderPearls = CONFIG.get(VANILLA_TWEAKS, "Ender Pearls Are OP", true, "EnderPearls don't damage users").getBoolean(opEnderPearls);
		addDragonEggTab = CONFIG.get(VANILLA_TWEAKS, "Add dragon egg to creative", true).getBoolean(addDragonEggTab);

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
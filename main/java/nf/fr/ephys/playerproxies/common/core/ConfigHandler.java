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

		BlockBaseShineyGlass.interfaceEnabled = modules.get("Universal Interface").getBoolean(BlockBaseShineyGlass.interfaceEnabled);
		BlockBeaconTierII.overwrite = modules.get("Vanilla beacon overwrite").getBoolean(BlockBeaconTierII.overwrite);
		BlockBiomeScanner.enabled = modules.get("Biome Scanner").getBoolean(BlockBiomeScanner.enabled);
		BlockToughwoodPlank.transmuterEnabled = modules.get("Biome Transmuter").getBoolean(BlockToughwoodPlank.transmuterEnabled);

		BlockGravitationalField.enabled = modules.get("Gravitational Field Handler").getBoolean(BlockGravitationalField.enabled);
		BlockHardenedStone.diffuserEnabled = modules.get("Fluid diffuser").getBoolean(BlockHardenedStone.diffuserEnabled);
		BlockParticleGenerator.enabled = modules.get("Particle generator").getBoolean(BlockParticleGenerator.enabled);
		BlockProximitySensor.enabled = modules.get("Proximity Sensor").getBoolean(BlockProximitySensor.enabled);

		BlockHomeShield.enabled = modules.get("HomeShield").getBoolean(BlockHomeShield.enabled);
		BlockItemTicker.enabled = modules.get("Sylladex").getBoolean(BlockItemTicker.enabled);

		CommandNickname.enabled = modules.get("nickname command").getBoolean(CommandNickname.enabled);

		ItemDragonPickaxe.enabled = modules.get("DragonScale pickaxe").getBoolean(ItemDragonPickaxe.enabled);
		ItemDragonHoe.enabled = modules.get("DragonScale hoe").getBoolean(ItemDragonHoe.enabled);
		ItemUnemptyingBucket.enabled = modules.get("Omnibucket").getBoolean(ItemUnemptyingBucket.enabled);
		ItemPotionDiffuser.enabled = modules.get("Handheld potion diffuser").getBoolean(ItemPotionDiffuser.enabled);

		// PROPERTIES
		property = CONFIG.get(PROPERTIES, "Homeshield Requires Twilight Forest", BlockHomeShield.requiresTwilightForest, "True: Will overwrite the TF Stronghold Shield to add a locked state [unbreakable but unlockable]. \nFalse: Will add a new Shield Block having that behavior");
		property.setRequiresMcRestart(true);
		BlockHomeShield.requiresTwilightForest = property.getBoolean(BlockHomeShield.requiresTwilightForest);

		BlockHomeShield.requiresSilkTouch = CONFIG.get(PROPERTIES, "Homeshield Requires Silk Touch", BlockHomeShield.requiresSilkTouch, "True if the shield only drops if silk touched").getBoolean(BlockHomeShield.requiresSilkTouch);
		requiresPower = CONFIG.get(PROPERTIES, "Requires Energy", requiresPower).getBoolean(requiresPower);

		ItemUnemptyingBucket.range = CONFIG.get(PROPERTIES, "Omnibucket bound range", ItemUnemptyingBucket.range).getInt(ItemUnemptyingBucket.range);
		ItemUnemptyingBucket.crossDim = CONFIG.get(PROPERTIES, "Omnibucket bound works cross-dim", ItemUnemptyingBucket.range).getBoolean(ItemUnemptyingBucket.crossDim);

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
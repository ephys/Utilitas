package nf.fr.ephys.playerproxies.common.core;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.BlockBeaconTierII;
import nf.fr.ephys.playerproxies.common.block.BlockHomeShield;

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

	public static final String BLOCK_PROPERTIES = "BlockProperties";
	public static final String VANILLA_TWEAKS = "VanillaTweaks";

	public void SyncConfig() {
		Property property = CONFIG.get(BLOCK_PROPERTIES, "Homeshield Requires Twilight Forest", BlockHomeShield.requiresTwilightForest);
		property.comment = "True: Will overwrite the TF Stronghold Shield to add a locked state [unbreakable but unlockable]. \nFalse: Will add a new Shield Block having that behavior";
		property.requiresMcRestart();
		BlockHomeShield.requiresTwilightForest = property.getBoolean(BlockHomeShield.requiresTwilightForest);

		property = CONFIG.get(BLOCK_PROPERTIES, "Homeshield Requires Silk Touch", BlockHomeShield.requiresSilkTouch);
		property.comment = "True if the shield only drops if silk touched";
		BlockHomeShield.requiresSilkTouch = property.getBoolean(BlockHomeShield.requiresSilkTouch);

		property = CONFIG.get(BLOCK_PROPERTIES, "Requires Energy", requiresPower);
		requiresPower = property.getBoolean(requiresPower);

		property = CONFIG.get(VANILLA_TWEAKS, "Ender Pearls Are OP", opEnderPearls);
		property.comment = "EnderPearls don't damage users";
		opEnderPearls = property.getBoolean(opEnderPearls);

		property = CONFIG.get(VANILLA_TWEAKS, "Overwrite vanilla beacon", BlockBeaconTierII.overwrite);
		property.comment = "Replaces the vanilla Beacon block by an enhanced (probably) version of it";
		property.requiresMcRestart();
		BlockBeaconTierII.overwrite = property.getBoolean(BlockBeaconTierII.overwrite);

		property = CONFIG.get(VANILLA_TWEAKS, "Add dragon egg to creative", addDragonEggTab);
		addDragonEggTab = property.getBoolean(addDragonEggTab);

		if (CONFIG.hasChanged())
			CONFIG.save();
	}

	@SubscribeEvent
	public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (!event.modID.equals(PlayerProxies.MODID)) return;

		SyncConfig();

		Blocks.dragon_egg.setCreativeTab(addDragonEggTab() ? CreativeTabs.tabDecorations : null);
	}
}
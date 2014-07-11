package nf.fr.ephys.playerproxies.client.gui;

import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.core.ConfigHandler;

import java.util.ArrayList;
import java.util.List;

public class GuiModConfig extends GuiConfig {
	public GuiModConfig(GuiScreen parent) {
		super(parent, getMenuElements(), PlayerProxies.MODID, false, false, PlayerProxies.NAME);
	}

	private static List<IConfigElement> getMenuElements() {
		List<IConfigElement> list = new ArrayList<>();
		list.add(new DummyConfigElement.DummyCategoryElement("ppVanillaTweaks", "playerproxies.configgui.ctgy.vanillatweaks", VanillaTweaksEntry.class));
		list.add(new DummyConfigElement.DummyCategoryElement("ppBlockProperties", "playerproxies.configgui.ctgy.blockproperties", BlockPropertiesEntry.class));

		return list;
	}

	public static class VanillaTweaksEntry extends GuiConfigEntries.CategoryEntry {
		public VanillaTweaksEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen,
					new ConfigElement(PlayerProxies.getConfig().CONFIG.getCategory(ConfigHandler.VANILLA_TWEAKS.toLowerCase())).getChildElements(),
					this.owningScreen.modID,
					ConfigHandler.VANILLA_TWEAKS.toLowerCase(),
					this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
					this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
					"Vanilla Tweaks");
		}
	}

	public static class BlockPropertiesEntry extends GuiConfigEntries.CategoryEntry {
		public BlockPropertiesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen,
					new ConfigElement(PlayerProxies.getConfig().CONFIG.getCategory(ConfigHandler.BLOCK_PROPERTIES.toLowerCase())).getChildElements(),
					this.owningScreen.modID,
					ConfigHandler.BLOCK_PROPERTIES.toLowerCase(),
					this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
					this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
					"Block Properties");
		}
	}
}
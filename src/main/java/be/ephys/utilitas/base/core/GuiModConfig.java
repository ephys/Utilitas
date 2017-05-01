//package be.ephys.utilitas.base.core;
//
//import be.ephys.utilitas.Utilitas;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraftforge.common.config.ConfigElement;
//import net.minecraftforge.fml.client.config.DummyConfigElement;
//import net.minecraftforge.fml.client.config.GuiConfig;
//import net.minecraftforge.fml.client.config.GuiConfigEntries;
//import net.minecraftforge.fml.client.config.IConfigElement;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class GuiModConfig extends GuiConfig {
//
//    public GuiModConfig(GuiScreen parent) {
//        super(parent, getMenuElements(), Utilitas.MODID, false, false, PlayerProxies.NAME);
//    }
//
//    private static List<IConfigElement> getMenuElements() {
//        List<IConfigElement> list = new ArrayList<>();
//        list.add(new DummyConfigElement.DummyCategoryElement("ppVanillaTweaks", "playerproxies.configgui.ctgy.vanillatweaks", VanillaTweaksEntry.class));
//        list.add(new DummyConfigElement.DummyCategoryElement("ppBlockProperties", "playerproxies.configgui.ctgy.blockproperties", BlockPropertiesEntry.class));
//        list.add(new DummyConfigElement.DummyCategoryElement("ppModules", "playerproxies.configgui.ctgy.modules", ModulesEntry.class));
//
//        return list;
//    }
//
//    public static class VanillaTweaksEntry extends GuiConfigEntries.CategoryEntry {
//        public VanillaTweaksEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
//            super(owningScreen, owningEntryList, configElement);
//        }
//
//        @Override
//        protected GuiScreen buildChildScreen() {
//            return new GuiConfig(this.owningScreen,
//                new ConfigElement(Utilitas.getConfig().CONFIG.getCategory(ConfigHandler.VANILLA_TWEAKS.toLowerCase())).getChildElements(),
//                this.owningScreen.modID,
//                ConfigHandler.VANILLA_TWEAKS.toLowerCase(),
//                this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
//                this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
//                ConfigHandler.VANILLA_TWEAKS);
//        }
//    }
//
//    public static class BlockPropertiesEntry extends GuiConfigEntries.CategoryEntry {
//        public BlockPropertiesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
//            super(owningScreen, owningEntryList, configElement);
//        }
//
//        @Override
//        protected GuiScreen buildChildScreen() {
//            return new GuiConfig(this.owningScreen,
//                new ConfigElement(Utilitas.getConfig().CONFIG.getCategory(ConfigHandler.PROPERTIES.toLowerCase())).getChildElements(),
//                this.owningScreen.modID,
//                ConfigHandler.PROPERTIES.toLowerCase(),
//                this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
//                this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
//                ConfigHandler.PROPERTIES);
//        }
//    }
//
//    public static class ModulesEntry extends GuiConfigEntries.CategoryEntry {
//        public ModulesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
//            super(owningScreen, owningEntryList, configElement);
//        }
//
//        @Override
//        protected GuiScreen buildChildScreen() {
//            return new GuiConfig(this.owningScreen,
//                new ConfigElement(Utilitas.getConfig().CONFIG.getCategory(ConfigHandler.MODULES.toLowerCase())).getChildElements(),
//                this.owningScreen.modID,
//                ConfigHandler.MODULES.toLowerCase(),
//                this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
//                this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
//                ConfigHandler.MODULES);
//        }
//    }
//}

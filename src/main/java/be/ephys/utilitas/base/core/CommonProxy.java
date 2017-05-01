package be.ephys.utilitas.base.core;

import be.ephys.utilitas.Utilitas;
import be.ephys.utilitas.base.feature.Config;
import be.ephys.utilitas.base.feature.Feature;
import be.ephys.utilitas.base.feature.FeatureMeta;
import be.ephys.utilitas.feature.fluid_hopper.FeatureFluidHopper;
import be.ephys.utilitas.feature.link_wand.FeatureLinkWand;
import be.ephys.utilitas.feature.universal_interface.FeatureUniversalInterface;
import be.ephys.utilitas.feature.vanilla_tweaks.VanillaTweaksFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonProxy {

    public static final String CLASS_NAME = "be.ephys.utilitas.base.core.CommonProxy";

    private ConfigHandler config;
    private final Pattern IMC_PATTERN = Pattern.compile("^([^:]+):(.+)");

    protected final List<Feature> features = new ArrayList<>(4);

    protected final List<Class<? extends Feature>> featureClasses = new ArrayList<>(4);

    {
        featureClasses.add(FeatureLinkWand.class);
        featureClasses.add(FeatureUniversalInterface.class);
        featureClasses.add(VanillaTweaksFeature.class);
        featureClasses.add(FeatureFluidHopper.class);
    }

    public void preInit(FMLPreInitializationEvent event) {

        this.config = new ConfigHandler(event.getSuggestedConfigurationFile());

        this.buildFeatures(config);
        config.persist();

        for (Feature f : features) {
            f.registerContents(event);
            f.registerPackets(event);
            f.preInit(event);
        }
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(Utilitas.instance, Utilitas.instance.guiHandler);

        for (Feature f : features) {
            f.registerCrafts(event);
            f.init(event);
        }
    }

    public void postInit(FMLPostInitializationEvent event) {

        for (Feature f : features) {
            f.postInit(event);
        }
    }

    private void buildFeatures(ConfigHandler configHandler) {
        Utilitas.getLogger().info("Initiating features");

        MinecraftForge.EVENT_BUS.register(configHandler);

        // TODO sort featureClasses by annotation.dependencies();
        // TODO don't init features which depend on a crashed feature
        // TODO features cannot have a config prop called "enabled"
        // TODO force enable disabled features if something depends on them.
        // TODO print the list of dependencies / dependants in the config comments.

        for (Class<? extends Feature> featureClass : featureClasses) {
            FeatureMeta annotation = featureClass.getAnnotation(FeatureMeta.class);
            if (annotation == null) {
                throw new RuntimeException("Feature '" + featureClass.getCanonicalName() + "' is missing annotation @FeatureMeta");
            }

            String featureName = annotation.name();

            configHandler.defineCategory(featureName, annotation.description());

            boolean enabled = configHandler.get(featureName, "enabled", "Enable the feature", true, Boolean.class, annotation.defaultEnabled());

            if (!enabled) {
                Utilitas.getLogger().info("x " + featureName);
                continue;
            }

            Utilitas.getLogger().info("✔ " + featureName);

            Feature featureInstance;
            try {
                featureInstance = featureClass.newInstance();
                this.features.add(featureInstance);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                Utilitas.getLogger().info("x " + featureName + " (crashed)");

                continue;
            }

            for (Field field : featureClass.getDeclaredFields()) {
                Config fieldConfig = field.getAnnotation(Config.class);
                if (fieldConfig == null) {
                    continue;
                }

                String optionName = fieldConfig.name();
                if (optionName.isEmpty()) {
                    optionName = field.getName();
                }

                if ("enabled".equals(optionName)) {
                    throw new RuntimeException("Cannot have a config option called 'enabled' (in Feature '" + featureClass.getCanonicalName() + "')");
                }

                field.setAccessible(true);

                try {
                    Object defaultValue = field.get(featureInstance);
                    Object newValue = configHandler.get(
                        featureName,
                        optionName,
                        fieldConfig.description(),
                        fieldConfig.requiresRestart(),
                        field.getType(),
                        defaultValue
                    );

                    field.set(featureInstance, newValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void handleImc(FMLInterModComms.IMCMessage message) {

        Matcher matcher = IMC_PATTERN.matcher(message.key);

        if (!matcher.find()) {
            Utilitas.getLogger().warn("Received IMC with key '" + message.key + "' from '" + message.getSender() + "' but its format is invalid. Should be '<feature>:<key>'");
        }

        String featureName = matcher.group(1);
        String key = matcher.group(2);

        for (Feature f : features) {
            if (!f.metadata().name().equals(featureName)) {
                continue;
            }

            f.handleImc(message, key);
            return;
        }

        Utilitas.getLogger().warn("Received IMC with key '" + message.key + "' from '" + message.getSender() + "' but no feature matches the key");
    }

    public void serverStarting(FMLServerStartingEvent event) {

        for (Feature f : features) {
            f.serverStarting(event);
        }
    }
}

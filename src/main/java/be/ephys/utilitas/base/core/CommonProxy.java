package be.ephys.utilitas.base.core;

import be.ephys.utilitas.Utilitas;
import be.ephys.utilitas.base.feature.Config;
import be.ephys.utilitas.base.feature.Feature;
import be.ephys.utilitas.base.feature.FeatureLoader;
import be.ephys.utilitas.base.feature.FeatureMeta;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonProxy {

    public static final String CLASS_NAME = "be.ephys.utilitas.base.core.CommonProxy";
    private static final Pattern IMC_PATTERN = Pattern.compile("^([^:]+):(.+)");

    private ConfigHandler config;
    private FeatureLoader featureLoader;
    protected final List<Feature> features = new ArrayList<>();
//    private List<Class<? extends Feature>> featureClasses;

    public void preInit(FMLPreInitializationEvent event) {

        this.featureLoader = new FeatureLoader(event);
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
        MinecraftForge.EVENT_BUS.register(configHandler);

        // TODO force enable disabled features if something depends on them.

        for (Class<? extends Feature> featureClass : featureLoader.getFeatureClasses()) {
            FeatureMeta annotation = featureClass.getAnnotation(FeatureMeta.class);
            if (annotation == null) {
                throw new RuntimeException("Feature '" + featureClass.getCanonicalName() + "' is missing annotation @FeatureMeta");
            }

            String featureName = annotation.name();

            StringBuilder categoryDescription = new StringBuilder(annotation.description());
            Set<Class<? extends Feature>> dependants = featureLoader.getDependants(featureClass);

            if (!dependants.isEmpty()) {
                categoryDescription.append("\n\nDepended on by:");

                for (Class<? extends Feature> dependant : dependants) {
                    categoryDescription.append("\n- ").append(dependant.getAnnotation(FeatureMeta.class).name());
                }
            }

            configHandler.defineCategory(featureName, categoryDescription.toString());

            boolean enabled = configHandler.get(featureName, "enabled", "Enable the feature (features are force-enabled if another active feature depends on them)", true, Boolean.class, annotation.defaultEnabled());

            if (!enabled) {
                Utilitas.getLogger().info("x " + featureName);
                continue;
            }

            Utilitas.getLogger().info("✔ " + featureName);

            Feature featureInstance;
            try {
                featureInstance = featureClass.newInstance();
                this.features.add(featureInstance);

                Field instanceField = FeatureLoader.getInstanceField(featureClass);
                if (instanceField != null) {
                    instanceField.setAccessible(true);
                    instanceField.set(null, featureInstance);
                }
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

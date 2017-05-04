package be.ephys.utilitas.base.core;

import be.ephys.utilitas.Utilitas;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.EnumSet;

public class ConfigHandler {
    private final Configuration config;

    public ConfigHandler(File location) {
        config = new Configuration(location);
        config.load();
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (!event.getModID().equals(Utilitas.MODID)) {
            return;
        }

        // TODO tell listeners the config changed

        persist();
    }

    public void persist() {
        if (config.hasChanged()) {
            config.save();
        }
    }

    public <T> T get(String category, String optionName, String description, boolean requiresRestart, Class<T> valueType, Object defaultValue) {

        Property property = getPropertyByType(category, optionName, defaultValue, valueType);

        if (isEnum(valueType)) {
            description += "\nPossible values (must match exactly): " + StringUtils.join(getEnums(valueType), " ");
        }

        property.setComment(description);
        property.setRequiresMcRestart(requiresRestart);

        // noinspection unchecked
        return (T) getValue(property, valueType);
    }

    public void defineCategory(String featureName, String description) {
        ConfigCategory category = config.getCategory(featureName);

        category.setRequiresMcRestart(true);
        category.setComment(description);
    }

    private Property getPropertyByType(String category, String key, Object defaultValue, Class<?> valueType) {

        // TODO generify
        if (valueType == Boolean.class || valueType == boolean.class) {
            return config.get(category, key, (boolean) defaultValue);
        }

        if (valueType == Integer.class || valueType == int.class) {
            return config.get(category, key, (int) defaultValue);
        }

        if (valueType == Double.class || valueType == double.class) {
            return config.get(category, key, (double) defaultValue);
        }

        if (valueType == String.class) {
            return config.get(category, key, (String) defaultValue);
        }

        if (isEnum(valueType)) {
            return config.get(category, key, ((Enum) defaultValue).name());
        }

        throw new IllegalArgumentException("Unsupported type " + valueType.getCanonicalName());
    }

    private Object getValue(Property property, Class<?> valueType) {

        // TODO generify
        if (valueType == Boolean.class || valueType == boolean.class) {
            return property.getBoolean();
        }

        if (valueType == Integer.class || valueType == int.class) {
            return property.getInt();
        }

        if (valueType == Double.class || valueType == double.class) {
            return property.getDouble();
        }

        if (valueType == String.class) {
            return property.getString();
        }

        if (isEnum(valueType)) {
            return getEnum(valueType, property.getString());
        }

        throw new IllegalArgumentException("Unsupported type " + valueType.getCanonicalName());
    }

    public static boolean isEnum(Class<?> aClass) {
        return Enum.class.isAssignableFrom(aClass);
    }

    private static EnumSet getEnums(Class aClass) {
        return EnumSet.allOf(aClass);
    }

    private static Enum getEnum(Class aClass, String enumName) {
        return Enum.valueOf(aClass, enumName);
    }
}

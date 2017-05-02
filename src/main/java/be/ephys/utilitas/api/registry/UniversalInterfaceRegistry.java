package be.ephys.utilitas.api.registry;

import be.ephys.utilitas.Utilitas;
import be.ephys.utilitas.feature.universal_interface.TileEntityInterface;
import net.minecraft.entity.player.EntityPlayer;

import java.lang.reflect.Constructor;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class UniversalInterfaceRegistry {
    private static TreeMap<Class<?>, Class<? extends UniversalInterfaceAdapter>> adapterMap = new TreeMap<>((o1, o2) -> {
        if (!o1.isInterface() && o2.isInterface()) // place interfaces first
            return 1;

        if (o1.isAssignableFrom(o2)) // o1 is superclass
            return 1;

        return -1;
    });

    public static boolean hasHandler(Class<? extends UniversalInterfaceAdapter> clazz) {
        return adapterMap.containsValue(clazz);
    }

    public static <T> UniversalInterfaceAdapter<T> getHandler(T obj, TileEntityInterface te, EntityPlayer linker) {
        Set<Entry<Class<?>, Class<? extends UniversalInterfaceAdapter>>> set = adapterMap.entrySet();

        for (Entry<Class<?>, Class<? extends UniversalInterfaceAdapter>> entry : set) {
            if (entry.getKey().isInstance(obj)) {
                Class<? extends UniversalInterfaceAdapter> clazz = entry.getValue();

                Constructor construct;
                try {
                    construct = clazz.getConstructor(TileEntityInterface.class);
                    UniversalInterfaceAdapter<T> handler = (UniversalInterfaceAdapter<T>) construct.newInstance(te);

                    if (handler.setLink(obj, linker)) {
                        return handler;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static <T> void addInterface(Class<? extends UniversalInterfaceAdapter<T>> clazzInterface, Class<T> clazzTarget) {
        if (adapterMap.containsKey(clazzTarget)) {
            Utilitas.getLogger().error(clazzTarget + " already has a class handler. Ignoring " + clazzInterface);
            return;
        }

        adapterMap.put(clazzTarget, clazzInterface);
        Utilitas.getLogger().info("[Interface Registry] Added handler (" + clazzInterface + ") for " + clazzTarget.getName());
    }
}

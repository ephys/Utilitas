package nf.fr.ephys.playerproxies.common.block.uniterface;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.entity.player.EntityPlayer;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;

public class UniversalInterfaceRegistry {
	private static TreeMap<Class<?>, Class<? extends UniversalInterface>> map = new TreeMap<Class<?>, Class<? extends UniversalInterface>>(
		new Comparator<Class<?>>() {
			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				if (o1.isAssignableFrom(o2)) // o1 is superclass
					return 1;
				
				return -1;
			}
		}
	);

	public static boolean hasHandler(Class<? extends UniversalInterface> clazz) {
		return map.containsValue(clazz);
	}

	public static UniversalInterface getHandler(Object obj, TileEntityInterface te, EntityPlayer linker) {
		Set<Entry<Class<?>, Class<? extends UniversalInterface>>> set = map.entrySet();

		for (Entry<Class<?>, Class<? extends UniversalInterface>> entry : set) {
			if (entry.getKey().isInstance(obj)) {
				Class<? extends UniversalInterface> clazz = entry.getValue();
				
				Constructor construct;
				try {
					construct = clazz.getConstructor(TileEntityInterface.class);
					UniversalInterface handler = (UniversalInterface) construct.newInstance(te);
					
					if (handler.setLink(obj, linker))
						return handler;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	public static void addInterface(Class<? extends UniversalInterface> clazzInterface, Class<?> clazzTarget) {
		if (map.containsKey(clazzTarget))
			throw new IllegalArgumentException(clazzTarget + " already has a class handler");
		
		map.put(clazzTarget, clazzInterface);
		PlayerProxies.getLogger().info("[Interface Registry] Added handler for "+clazzTarget.getName());
	}
}
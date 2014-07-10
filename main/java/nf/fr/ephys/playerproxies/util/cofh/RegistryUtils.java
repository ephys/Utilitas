package nf.fr.ephys.playerproxies.util.cofh;

import net.minecraft.util.RegistryNamespaced;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

/**
 * https://github.com/CoFH/CoFHLib/blob/master/src/main/java/cofh/util/RegistryUtils.java
 */
public class RegistryUtils {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean overwriteEntry(RegistryNamespaced registry, String name, Object object) {
		PlayerProxies.getLogger().info("Trying to overwrite '"+name+"', impossible operation. Unimplemented method RegistryUtils::overwriteEntry");

		/*Object oldThing = registry.getObject(name);
		int id = registry.getIDForObject(oldThing);

		for (Field field : registry.getClass().getFields()) {
			if (field.getClass().equals(java.util.Map.class)) {
				field.setAccessible(true);

				try {
					BiMap map = (BiMap) field.get(registry);

					registry.underlyingIntegerMap.func_148746_a(object, id);

					map.remove(name);
					map.focePut(name, object);
				} catch(Throwable err) {
					err.printStackTrace();

					return false;
				}

				return true;
			}
		}

		return false;*/

		return false;
	}
}
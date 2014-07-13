package nf.fr.ephys.playerproxies.util.cofh;

import com.google.common.collect.BiMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.RegistrySimple;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * https://github.com/CoFH/CoFHLib/blob/master/src/main/java/cofh/util/RegistryUtils.java
 */
public class RegistryUtils {
	private static Field underlyingIntergerMap;
	private static Field field_148758_b;

	private static Field registryObjects;

	private static Field itemblock_block;
	private static boolean mayOverwrite = false;

	static {
		Field[] rnFields = RegistryNamespaced.class.getDeclaredFields();
		Field[] roFields = RegistrySimple.class.getDeclaredFields();
		Field[] ibFields = ItemBlock.class.getDeclaredFields();

		if (rnFields.length > 1 && roFields.length > 1 && ibFields.length > 0 &&
				rnFields[0].getType().equals(ObjectIntIdentityMap.class) &&
				rnFields[1].getType().equals(Map.class) &&
				roFields[1].getType().equals(Map.class) &&
				ibFields[0].getType().equals(Block.class)) {
			underlyingIntergerMap = rnFields[0];
			field_148758_b = rnFields[1];

			registryObjects = rnFields[1];
			itemblock_block = ibFields[0];

			underlyingIntergerMap.setAccessible(true);
			field_148758_b.setAccessible(true);
			registryObjects.setAccessible(true);
			itemblock_block.setAccessible(true);

			mayOverwrite = true;
		} else {
			PlayerProxies.getLogger().error("Reflection faillure: overwriting will be disabled (this is a bug !)");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object overwriteEntry(RegistryNamespaced registry, String name, Object newEntry) {
		if (!mayOverwrite) return null;

		try {
			Object oldEntry = registry.getObject(name);
			int id = registry.getIDForObject(oldEntry);

			//BiMap map = ((BiMap) registry.registryObjects);
			BiMap map = (BiMap) registryObjects.get(registry);

			//registry.underlyingIntegerMap.func_148746_a(newEntry, id);
			((ObjectIntIdentityMap) underlyingIntergerMap.get(registry)).func_148746_a(newEntry, id);

			map.remove(name);
			map.forcePut(name, newEntry);

			BiMap reverse = (BiMap) field_148758_b.get(registry);
			reverse.remove(oldEntry);
			reverse.forcePut(newEntry, name);

			PlayerProxies.getLogger().info("RegistryUtils::overwriteEntry: overwriting '" + name + "' with " + newEntry.toString() + ": Operation success");

			return oldEntry;
		} catch (Exception e) {
			PlayerProxies.getLogger().error("RegistryUtils::overwriteEntry: overwriting '" + name + ": Operation failure (this is a bug !)");
			e.printStackTrace();

			return null;
		}
	}

	public static Block overwriteBlock(String name, Block newBlock) {
		if (!mayOverwrite) return null;

		Block oldBlock = (Block) overwriteEntry(Block.blockRegistry, name, newBlock);

		if (name.startsWith("minecraft:")) {
			Field[] blocks = Blocks.class.getDeclaredFields();

			try {
				for (Field blockField : blocks) {
					if (blockField.get(null) == oldBlock) {
						blockField.setAccessible(true);

						Field modifiersField = Field.class.getDeclaredField("modifiers");
						modifiersField.setAccessible(true);
						modifiersField.setInt(blockField, blockField.getModifiers() & ~Modifier.FINAL);

						blockField.set(null, newBlock);

						PlayerProxies.getLogger().warn("RegistryUtils::overwriteBlock: overwrote net.minecraft.init.Blocks entry");

						break;
					}
				}
			} catch (Exception e) {
				PlayerProxies.getLogger().warn("RegistryUtils::overwriteBlock: failed to overwrite net.minecraft.init.Blocks entry");

				e.printStackTrace();
			}
		}

		if (oldBlock == null) return null;

		Object oldItem = Item.itemRegistry.getObject(name);

		if (oldItem instanceof ItemBlock) {
			try {
				itemblock_block.set(oldItem, newBlock);
			} catch (IllegalAccessException e) {
				PlayerProxies.getLogger().info("RegistryUtils::overwriteBlock: Overwriting " + name + "'s itemblock failed. Your game is going to crash :( Report this.");

				e.printStackTrace();
			}
		}

		return oldBlock;
	}
}
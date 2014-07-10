package nf.fr.ephys.playerproxies.common.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBeaconTierII;

import java.util.ArrayList;
import java.util.List;

public class BeaconEffectsRegistry {
	private static List<Effect> effects = new ArrayList<>();

	/**
	 * adds "beacon effects" to an item
	 * @param item          the item adding effects to the beacon
	 * @param potionEffect  the potion effect id
	 * @param minLevel      the minimum beacon level for this effect to be available
	 * @param maxLevel      the maximum beacon level for this effect to be available
	 */
	public static void addEffect(ItemStack item, int potionEffect, int minLevel, int maxLevel) {
		addEffect(new ItemStack[] { item }, potionEffect, minLevel, maxLevel);
	}

	public static void addEffect(Item item, int potionEffect, int minLevel, int maxLevel) {
		addEffect(new ItemStack[] { new ItemStack(item) }, potionEffect, minLevel, maxLevel);
	}

	/**
	 * adds "beacon effects" to combined items
	 * @param items          the items which combined adds effects to the beacon
	 * @param potionEffect  the potion effect id
	 * @param minLevel      the minimum beacon level for this effect to be available
	 * @param maxLevel      the maximum beacon level for this effect to be available
	 */
	public static void addEffect(ItemStack[] items, int potionEffect, int minLevel, int maxLevel) {
		if (items.length > TileEntityBeaconTierII.MAX_ITEMS) {
			PlayerProxies.getLogger().warn("Adding to many combined items (" + items + ") to the beacon registry, a beacon cannot support more than "+TileEntityBeaconTierII.MAX_ITEMS+" items");
			return;
		}

		if (maxLevel < 1) {
			PlayerProxies.getLogger().warn("Attempted to add a potion effect with an incorrect maximum beacon level (" + maxLevel + ") to " + items + ". Skipping effect (as it would never be available).");
			return;
		}

		if (minLevel > TileEntityBeaconTierII.MAX_LEVELS) {
			PlayerProxies.getLogger().warn("Attempted to add a potion effect with an incorrect minimum beacon level (" + minLevel + ") to " + items + ". Clipping it to " + TileEntityBeaconTierII.MAX_LEVELS);
			minLevel = TileEntityBeaconTierII.MAX_LEVELS;
		}

		effects.add(new Effect(minLevel, maxLevel, potionEffect, items));
	}

	public static List<Integer> getEffects(ItemStack[] itemList, int level) {
		List<Integer> list = new ArrayList<>();

		for (Effect effect : effects) {
			if (level < effect.min || level > effect.max) continue;

			ItemStack[] requiredItems = effect.items;

			boolean valid = true;
			for (ItemStack needle : requiredItems) {
				if (!contains(itemList, needle)) {
					valid = false;
					break;
				}
			}

			if (valid) list.add(effect.potionEffect);
		}

		return list;
	}

	private static boolean contains(ItemStack[] list, ItemStack needle) {
		if (needle == null) return false;
		for (ItemStack elem : list) {
			if (elem == null) return false;

			if (needle.isItemEqual(elem)) return true;
		}

		return false;
	}

	public static boolean hasItem(ItemStack needle) {
		for (Effect effect : effects) {
			for (ItemStack item : effect.items) {
				if (item.isItemEqual(needle)) return true;
			}
		}

		return false;
	}

	private static class Effect {
		private int min;
		private int max;
		private int potionEffect;
		private ItemStack[] items;

		private Effect(int min, int max, int potionEffect, ItemStack[] items) {
			this.min = min;
			this.max = max;
			this.potionEffect = potionEffect;
			this.items = items;
		}
	}
}

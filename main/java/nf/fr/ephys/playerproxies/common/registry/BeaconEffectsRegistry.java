package nf.fr.ephys.playerproxies.common.registry;

import net.minecraft.item.Item;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBeaconTierII;
import nf.fr.ephys.playerproxies.util.SimpleMap;

public class BeaconEffectsRegistry {
	private static SimpleMap<Item[], Effect> effects = new SimpleMap<>();

	/**
	 * adds "beacon effects" to an item
	 * @param item          the item adding effects to the beacon
	 * @param potionEffect  the potion effect id
	 * @param minLevel      the minimum beacon level for this effect to be available
	 * @param maxLevel      the maximum beacon level for this effect to be available
	 */
	public static void addEffect(Item item, int potionEffect, int minLevel, int maxLevel) {
		if (maxLevel < 1) {
			PlayerProxies.getLogger().warn("Attempted to add a potion effect with an incorrect maximum beacon level (" + maxLevel + ") to " + item.getUnlocalizedName() + ". Skipping effect (as it would never be available).");
			return;
		}

		if (minLevel > TileEntityBeaconTierII.MAX_LEVELS) {
			PlayerProxies.getLogger().warn("Attempted to add a potion effect with an incorrect minimum beacon level (" + minLevel + ") to " + item.getUnlocalizedName() + ". Clipping it to " + TileEntityBeaconTierII.MAX_LEVELS);
			minLevel = TileEntityBeaconTierII.MAX_LEVELS;
		}

		effects.put(new Item[] { item }, new Effect(minLevel, maxLevel, potionEffect));
	}

	/**
	 * adds "beacon effects" to an item
	 * @param items          the items which combined adds effects to the beacon
	 * @param potionEffect  the potion effect id
	 * @param minLevel      the minimum beacon level for this effect to be available
	 * @param maxLevel      the maximum beacon level for this effect to be available
	 */
	public static void addEffect(Item[] items, int potionEffect, int minLevel, int maxLevel) {
		if (items.length > TileEntityBeaconTierII.MAX_ITEMS) {
			PlayerProxies.getLogger().warn("Adding to many combined items ("+items+") to the beacon registry, a beacon cannot support more than "+TileEntityBeaconTierII.MAX_ITEMS+" items");
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

		effects.put(new Item[] { item }, new Effect(minLevel, maxLevel, potionEffect));
	}

	public static int[] getEffects(Item[] itemList, int level) {
		return null;
	}

	public static boolean hasItem(Item item) { return false; }

	private static class Effect {
		private int min;
		private int max;
		private int potionEffect;

		private Effect(int min, int max, int potionEffect) {
			this.min = min;
			this.max = max;
			this.potionEffect = potionEffect;
		}
	}
}

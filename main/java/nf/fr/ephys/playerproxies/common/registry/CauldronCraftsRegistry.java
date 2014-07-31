package nf.fr.ephys.playerproxies.common.registry;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;

import java.util.HashMap;
import java.util.List;

public class CauldronCraftsRegistry {
	private static HashMap<ItemStack[], ItemStack[]> crafts = new HashMap<>();

	public static void addRecipe(ItemStack[] items, ItemStack[] results) {
		crafts.put(items, results);
	}

	public static boolean attemptCrafting(World world, int x, int y, int z) {
		//if (!world.getBlock(x, y, z).equals(Blocks.cauldron))
		//	return false;

		//int waterLevel = world.getBlockMetadata(x, y, z);

		//if (waterLevel == 0)
		// 	return false;

		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
		ItemStack[] recipe = getItemsInAABB(world, aabb);
		ItemStack[] results = crafts.get(recipe);

		System.out.println("items: " + recipe.length);
		if (results == null) {
			System.out.println("no craft available");
			return false;
		}

		destroyItemsInAABB(world, aabb);

		for (ItemStack result : results) {
			InventoryHelper.dropItem(result.copy(), world, x, y + 1, z);
		}

		//world.setBlockMetadataWithNotify(x, y, z, --waterLevel, 3);

		return true;
	}

	@SuppressWarnings("unchecked")
	public static ItemStack[] getItemsInAABB(World world, AxisAlignedBB aabb) {
		List<EntityItem> itemEntities = world.getEntitiesWithinAABB(EntityItem.class, aabb);

		ItemStack[] stacks = new ItemStack[itemEntities.size()];

		for (int i = 0; i < itemEntities.size(); i++) {
			stacks[i] = itemEntities.get(i).getEntityItem();
		}

		return stacks;
	}

	@SuppressWarnings("unchecked")
	public static void destroyItemsInAABB(World world, AxisAlignedBB aabb) {
		List<EntityItem> itemEntities = world.getEntitiesWithinAABB(EntityItem.class, aabb);

		for (EntityItem item : itemEntities) {
			item.setDead();
		}
	}
}
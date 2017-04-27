package be.ephys.utilitas.common.item;

import be.ephys.utilitas.common.tileentity.TileEntityInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;

public interface IInterfaceUpgrade {
	/**
	 * Called when inserted, the itemstack passed is still in the player's inventory, not yet in the tile entity.
	 *
	 * @param tile      the tile the upgrade will be inserted into
	 * @param player    the player inserting the upgrade
	 * @param stack     the upgrade stack
	 * @return the upgrade is valid
	 */
	boolean onInsert(TileEntityInterface tile, EntityPlayer player, ItemStack stack);

	/**
	 * Called when removed, the itemstack passed is still in the tile entity, not yet in the player's inventory.
	 *
	 * @param tile      the tile the upgrade has been removed from
	 * @param player    the player inserting the upgrade
	 * @param stack     the upgrade stack
	 */
	void onRemove(TileEntityInterface tile, EntityPlayer player, ItemStack stack);
}

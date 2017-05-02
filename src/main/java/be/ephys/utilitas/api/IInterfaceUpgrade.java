package be.ephys.utilitas.api;

import be.ephys.utilitas.feature.universal_interface.TileEntityInterface;
import net.minecraft.item.ItemStack;

public interface IInterfaceUpgrade {

    /**
     * Called when inserted, the itemstack passed is still in the player's inventory, not yet in the tile entity.
     *
     * @param tile   the tile the upgrade will be inserted into
     * @param stack  the upgrade stack
     * @return the upgrade is valid
     */
    boolean onInsert(TileEntityInterface tile, ItemStack stack);

    /**
     * Called when removed, the itemstack passed is still in the tile entity, not yet in the player's inventory.
     *
     * @param tile   the tile the upgrade has been removed from
     * @param stack  the upgrade stack
     */
    void onRemove(TileEntityInterface tile, ItemStack stack);
}

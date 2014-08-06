package nf.fr.ephys.playerproxies.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;

public class InterfaceUpgrade extends Item implements IInterfaceUpgrade {
	@Override
	public boolean onInsert(TileEntityInterface tile, EntityPlayer player, ItemStack stack) {
		return false;
	}

	@Override
	public void onRemove(TileEntityInterface tile, EntityPlayer player, ItemStack stack) {
		return;
	}
}

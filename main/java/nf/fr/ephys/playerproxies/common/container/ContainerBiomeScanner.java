package nf.fr.ephys.playerproxies.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;
import nf.fr.ephys.playerproxies.util.FilteredSlot;

public class ContainerBiomeScanner extends Container {
	private TileEntityBiomeScanner te;
	private FilteredSlot cardSlot;

	public ContainerBiomeScanner(EntityPlayer player, TileEntityBiomeScanner te) {
		this.te = te;

		this.cardSlot = new FilteredSlot(te, 0, 226, 25, new Item[] { PlayerProxies.Items.biomeStorage });

		addSlotToContainer(this.cardSlot);

		bindPlayerInventory(player.inventory);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotID);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotID < 1) {
				if (!this.mergeItemStack(itemstack1, 1, this.inventorySlots.size(), true))
					return null;
			} else {
				if(this.cardSlot.getHasStack() || !this.cardSlot.isItemValid(itemstack1))
					return null;

				if (!this.mergeItemStack(itemstack1, 0, 1, false))
					return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
						41 + j * 19, 114 + i * 19));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 41 + i * 19, 175));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	public TileEntityBiomeScanner getTileEntity() {
		return this.te;
	}
}

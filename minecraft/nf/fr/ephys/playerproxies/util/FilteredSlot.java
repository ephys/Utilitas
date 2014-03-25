package nf.fr.ephys.playerproxies.util;

import java.util.Vector;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FilteredSlot extends Slot {
	private Vector<Integer> filter;

	public FilteredSlot(IInventory inventory, int id, int x, int y) {
		super(inventory, id, x, y);

		this.filter = new Vector<Integer>();
	}

	public FilteredSlot(IInventory inventory, int id, int x, int y, int[] filter) {
		this(inventory, id, x, y);

		addFilteredIds(filter);
	}

	public FilteredSlot(IInventory inventory, int id, int x, int y, Vector<Integer> filter) {
		super(inventory, id, x, y);

		this.filter = filter;
	}

	public FilteredSlot addFilteredIds(int[] filter) {
		for (int i = 0; i < filter.length; i++)
			this.filter.add(filter[i]);

		return this;
	}

	public FilteredSlot addFilteredIds(int filter) {
		this.filter.add(filter);

		return this;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return this.filter.contains(stack.itemID);
	}
}

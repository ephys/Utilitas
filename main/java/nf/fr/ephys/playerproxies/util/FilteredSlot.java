package nf.fr.ephys.playerproxies.util;

import java.util.Vector;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FilteredSlot extends Slot {
	private Vector<Item> filter;

	public FilteredSlot(IInventory inventory, int id, int x, int y) {
		super(inventory, id, x, y);

		this.filter = new Vector<Item>();
	}

	public FilteredSlot(IInventory inventory, int id, int x, int y, Item[] filter) {
		this(inventory, id, x, y);

		addFilteredIds(filter);
	}

	public FilteredSlot(IInventory inventory, int id, int x, int y, Vector<Item> filter) {
		super(inventory, id, x, y);

		this.filter = filter;
	}

	public FilteredSlot addFilteredIds(Item[] filter) {
		for (int i = 0; i < filter.length; i++)
			this.filter.add(filter[i]);

		return this;
	}

	public FilteredSlot addFilteredIds(Item filter) {
		this.filter.add(filter);

		return this;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return this.filter.contains(stack.getItem());
	}
}

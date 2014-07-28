package nf.fr.ephys.playerproxies.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityFluidHopper;
import nf.fr.ephys.playerproxies.util.SlotFiltered;
import nf.fr.ephys.playerproxies.util.SlotIInventory;

import java.util.Vector;

public class ContainerFluidHopper extends Container {
	private EntityPlayer player;
	private TileEntityFluidHopper te;
	private int tileSlotCount;

	public ContainerFluidHopper(EntityPlayer player, TileEntityFluidHopper te) {
		super();

		this.player = player;
		this.te = te;

		tileSlotCount = te.getSizeInventory();

		int i;
		for (i = 0; i < tileSlotCount - 1; i++) {
			addSlotToContainer(new SlotIInventory(te, i, 10, 20 + 30 * i));
		}

		addSlotToContainer(new SlotFiltered(te, i, 250 - 16 - 10, 20, new Vector<Item>(0)));
		bindPlayerInventory(player.inventory, te.getSizeInventory());
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int originSlotID) {
		Slot slot = (Slot) this.inventorySlots.get(originSlotID);

		if (slot == null || !slot.getHasStack()) return null;

		ItemStack originStack = slot.getStack();

		// we're in the tile, send to player
		if (originSlotID < tileSlotCount) {
			if (!this.mergeItemStack(originStack, tileSlotCount, tileSlotCount + 9, false))
				return null;
		} else {
			if (!this.mergeItemStack(originStack, 0, tileSlotCount - 1, false))
				return null;
		}

		if (originStack.stackSize == 0) {
			slot.putStack(null);
		}

		slot.onSlotChanged();

		return originStack.copy();
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer, int startOffset) {
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 41 + i * 19, 175));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public FluidTankInfo[] getFluidStacks() {
		return te.getTankInfo(ForgeDirection.DOWN);
	}
	public int getTileSlotCount() { return tileSlotCount; }
}
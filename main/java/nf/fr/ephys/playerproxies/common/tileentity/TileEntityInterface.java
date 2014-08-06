package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.cookiecore.helpers.ChatHelper;
import nf.fr.ephys.cookiecore.helpers.EntityHelper;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.item.IInterfaceUpgrade;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import nf.fr.ephys.playerproxies.common.registry.UniversalInterfaceRegistry;
import nf.fr.ephys.playerproxies.common.registry.uniterface.UniversalInterface;

public class TileEntityInterface extends TileEntity implements ISidedInventory, IFluidHandler {
	private UniversalInterface uniterface = null;
	public ItemStack[] upgrades = new ItemStack[5];

	private boolean isFluidHandler = false;
	private boolean isWireless = false;
	private boolean worksCrossDim = false;

	public int tick = 0;

	public UniversalInterface getInterface() {
		return uniterface;
	}

	public void onBlockUpdate() {
		if (this.uniterface != null)
			this.uniterface.onBlockUpdate();
	}

	private IInventory getInventory() {
		if (!isInRange()) {
			return null;
		}

		return uniterface.getInventory();
	}

	private IFluidHandler getFluidHandler() {
		if (!isFluidHandler() || !isInRange())
			return null;

		return uniterface.getFluidHandler();
	}

	public boolean isInRange() {
		if (uniterface == null) return false;

		if (!worksCrossDim() && uniterface.getDim() != worldObj.provider.dimensionId) {
			return false;
		}

		if (!isWireless() && !uniterface.isNextTo(xCoord, yCoord, zCoord)) {
			return false;
		}

		return true;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);

		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("tick", tick);

		nbt.setBoolean("isFluidHandler", isFluidHandler);
		nbt.setBoolean("isWireless", isWireless);
		nbt.setBoolean("worksCrossDim", worksCrossDim);

		for (int i = 0; i < upgrades.length; i++) {
			if (upgrades[i] == null) continue;

			NBTTagCompound upgradeNBT = new NBTTagCompound();
			upgrades[i].writeToNBT(upgradeNBT);

			nbt.setTag("upgrade_" + i, upgradeNBT);
		}

		if (this.uniterface != null) {
			NBTHelper.setClass(nbt, "handler", this.uniterface.getClass());
			this.uniterface.writeToNBT(nbt);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		tick = NBTHelper.getInt(nbt, "tick", 0);

		isFluidHandler = NBTHelper.getBoolean(nbt, "isFluidHandler", isFluidHandler);
		isWireless = NBTHelper.getBoolean(nbt, "isWireless", isWireless);
		worksCrossDim = NBTHelper.getBoolean(nbt, "worksCrossDim", worksCrossDim);

		for (int i = 0; i < upgrades.length; i++) {
			if (nbt.hasKey("upgrade_" + i))
				upgrades[i] = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("upgrade_" + i));
			else
				upgrades[i] = null;
		}

		Class<? extends UniversalInterface> clazz = (Class<? extends UniversalInterface>) NBTHelper.getClass(nbt, "handler");

		if (clazz != null && UniversalInterfaceRegistry.hasHandler(clazz)) {
			try {
				this.uniterface = clazz.getConstructor(TileEntityInterface.class).newInstance(this);
				this.uniterface.readFromNBT(nbt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.uniterface = null;
		}
	}

	@Override
	public void validate() {
		super.validate();

		if (this.uniterface != null) this.uniterface.validate();
	}

	public void unlink() {
		this.uniterface = null;

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (this.uniterface != null)
			this.uniterface.onTick(tick++);
	}

	public boolean addUpgrade(ItemStack heldItem, EntityPlayer player) {
		if (heldItem == null || !(heldItem.getItem() instanceof IInterfaceUpgrade)) return false;

		IInterfaceUpgrade upgrade = (IInterfaceUpgrade) heldItem.getItem();

		int upgradeSlot = hasUpgrade(heldItem);
		if (upgradeSlot != -1) {
			ItemStack stack = upgrades[upgradeSlot];
			upgrades[upgradeSlot] = null;

			upgrade.onRemove(this, player, stack);

			InventoryHelper.dropItem(stack, player);
		} else {
			upgradeSlot = getEmptySlot();

			if (upgradeSlot == -1) return false;

			if (!upgrade.onInsert(this, player, heldItem)) return false;

			ItemStack stack = heldItem.copy();
			stack.stackSize = 1;
			upgrades[upgradeSlot] = stack;

			heldItem.stackSize--;

			if (heldItem.stackSize <= 0)
				player.setCurrentItemOrArmor(0, null);
		}

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		return true;
	}

	public int hasUpgrade(ItemStack upgrade) {
		for (int i = 0; i < upgrades.length; i++) {
			if (upgrades[i] != null && upgrades[i].isItemEqual(upgrade))
				return i;
		}

		return -1;
	}

	private int getEmptySlot() {
		for (int i = 0; i < upgrades.length; i++) {
			if (upgrades[i] == null) return i;
		}

		return -1;
	}

	public boolean link(EntityPlayer player) {
		if (uniterface != null) {
			unlink();
			ChatHelper.sendChatMessage(player, "Interface unlinked");

			return true;
		}

		Object toLink;
		if (player.getHeldItem() == null) {
			if (EntityHelper.isFakePlayer(player))
				return false;

			toLink = player;
		} else if (player.getHeldItem().getItem().equals(PlayerProxies.Items.linkDevice)) {
			toLink = ItemLinker.getLinkedObject(player.getHeldItem(), worldObj);

			if (toLink == null) {
				ChatHelper.sendChatMessage(player, "Link wand not bound");

				return false;
			}

			if (toLink instanceof TileEntityInterface) {
				ChatHelper.sendChatMessage(player, "You're not a good person. You know that, right ?");

				return false;
			}

			if (toLink instanceof EntityPlayer && EntityHelper.isFakePlayer((EntityPlayer) toLink))
				return false;
		} else {
			return false;
		}

		UniversalInterface handler = UniversalInterfaceRegistry.getHandler(toLink, this, player);

		if (handler != null) {
			this.uniterface = handler;

			ChatHelper.sendChatMessage(player, "Universal interface linked to " + handler.getName());

			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		} else {
			ChatHelper.sendChatMessage(player, "Could not link the universal interface to this link wand");
		}

		return true;
	}

	// ================================================================================
	// IInventory interface
	// ================================================================================
	@Override
	public int getSizeInventory() {
		IInventory linkedInventory = this.getInventory();
		return (linkedInventory != null) ? linkedInventory.getSizeInventory()
				: 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		IInventory linkedInventory = this.getInventory();

		if (linkedInventory == null)
			return null;

		return linkedInventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		IInventory linkedInventory = this.getInventory();

		if (linkedInventory == null)
			return null;

		return linkedInventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		IInventory linkedInventory = this.getInventory();
		if (linkedInventory != null)
			linkedInventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		IInventory linkedInventory = this.getInventory();
		return (linkedInventory != null) ? linkedInventory
				.getInventoryStackLimit() : 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		IInventory linkedInventory = this.getInventory();

		return linkedInventory != null && linkedInventory.isItemValidForSlot(i, itemstack);
	}

	// ================================================================================
	// ISidedInventory interface
	// ================================================================================

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		IInventory linkedInventory = this.getInventory();

		return (linkedInventory instanceof ISidedInventory) ? ((ISidedInventory) linkedInventory).getAccessibleSlotsFromSide(var1) :
				linkedInventory != null ? InventoryHelper.getUnSidedInventorySlots(linkedInventory) : new int[0];
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		IInventory linkedInventory = this.getInventory();

		return (linkedInventory instanceof ISidedInventory) ? ((ISidedInventory) linkedInventory)
				.canInsertItem(i, itemstack, j) : linkedInventory != null;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		IInventory linkedInventory = this.getInventory();

		return (linkedInventory instanceof ISidedInventory) ? ((ISidedInventory) linkedInventory)
				.canExtractItem(i, itemstack, j) : linkedInventory != null;
	}

	// ================================================================================
	// IFluidHandler interface
	// ================================================================================

	@Override
	public int fill(ForgeDirection forgeDirection, FluidStack fluidStack, boolean b) {
		IFluidHandler fluidHandler = this.getFluidHandler();

		return fluidHandler == null ? 0 : fluidHandler.fill(forgeDirection, fluidStack, b);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		IFluidHandler fluidHandler = this.getFluidHandler();

		return fluidHandler == null ? null : fluidHandler.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		IFluidHandler fluidHandler = this.getFluidHandler();

		return fluidHandler == null ? null : fluidHandler.drain(from, maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		IFluidHandler fluidHandler = this.getFluidHandler();

		return fluidHandler != null && fluidHandler.canFill(from, fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		IFluidHandler fluidHandler = this.getFluidHandler();

		return fluidHandler != null && fluidHandler.canDrain(from, fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		IFluidHandler fluidHandler = this.getFluidHandler();

		return fluidHandler == null ? null : fluidHandler.getTankInfo(from);
	}

	public void setIsFluidHandler(boolean b) {
		isFluidHandler = b;
	}

	public void setWorksCrossDim(boolean b) {
		worksCrossDim = b;
	}

	public void setWireless(boolean b) {
		isWireless = b;
	}

	public boolean worksCrossDim() {
		return worksCrossDim;
	}

	public boolean isWireless() {
		return isWireless;
	}

	public boolean isFluidHandler() {
		return isFluidHandler;
	}
}

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
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import nf.fr.ephys.playerproxies.common.registry.UniversalInterfaceRegistry;
import nf.fr.ephys.playerproxies.common.registry.uniterface.UniversalInterface;
import nf.fr.ephys.playerproxies.helpers.CommandHelper;
import nf.fr.ephys.playerproxies.helpers.EntityHelper;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class TileEntityInterface extends TileEntity implements ISidedInventory, IFluidHandler {
	private UniversalInterface uniterface = null;

	public int tick = 0;

	public UniversalInterface getInterface() {
		return uniterface;
	}

	public void onBlockUpdate() {
		if (this.uniterface != null)
			this.uniterface.onBlockUpdate();
	}

	private IInventory getInventory() {
		return uniterface == null ? null : uniterface.getInventory();
	}

	private IFluidHandler getFluidHandler() {
		return uniterface == null ? null : uniterface.getFluidHandler();
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

		if (this.uniterface != null) {
			NBTHelper.setClass(nbt, "handler", this.uniterface.getClass());
			this.uniterface.writeToNBT(nbt);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

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
			this.uniterface.onTick();
	}

	public boolean link(EntityPlayer player) {
		if (uniterface != null) {
			unlink();
			CommandHelper.sendChatMessage(player, "Interface unlinked");

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
				CommandHelper.sendChatMessage(player, "Link wand not bound");

				return false;
			}

			if (toLink instanceof TileEntityInterface) {
				CommandHelper.sendChatMessage(player, "You're not a good person. You know that, right ?");

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

			CommandHelper.sendChatMessage(player, "Universal interface linked to " + handler.getName());

			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		} else {
			CommandHelper.sendChatMessage(player, "Could not link the universal interface to this link wand");
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
				linkedInventory != null ? getUnSidedInventorySlots(linkedInventory) : new int[0];
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

	public static int[] getUnSidedInventorySlots(IInventory inventory) {
		int[] slots = new int[inventory.getSizeInventory()];

		for (int i = 0; i < slots.length; i++) {
			slots[i] = i;
		}

		return slots;
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
}

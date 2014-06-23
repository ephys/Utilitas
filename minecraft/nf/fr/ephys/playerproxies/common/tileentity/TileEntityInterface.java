package nf.fr.ephys.playerproxies.common.tileentity;

import java.util.ArrayList;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.uniterface.UniversalInterface;
import nf.fr.ephys.playerproxies.common.block.uniterface.UniversalInterfaceRegistry;
import nf.fr.ephys.playerproxies.common.core.PacketHandler;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;

public class TileEntityInterface extends TileEntity implements ISidedInventory, IFluidHandler {
	private UniversalInterface uniterface = null;

	public UniversalInterface getInterface() {
		return uniterface;
	}

	public void onBlockUpdate(int side) {
		this.uniterface.onBlockUpdate(side);
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

		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		readFromNBT(packet.data);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		if (this.uniterface != null)
			this.uniterface.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if (this.uniterface != null)
			this.uniterface.readFromNBT(nbt);
	}
	
	public void unlink() {
		this.uniterface = null;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if (this.uniterface != null)
			this.uniterface.onTick();
	}
	
	public boolean link(EntityPlayer player) {
		Object toLink;
		if (player.getHeldItem() == null) {
			toLink = player;
		} else if (player.getHeldItem().itemID == PlayerProxies.Items.linkDevice.itemID) {
			toLink = ItemLinker.getLinkedObject(player.getHeldItem(), worldObj);
			
			if (toLink == null) {
				player.addChatMessage("Link wand not bound");
				return false;
			}
		} else {
			return false;
		}

		UniversalInterface handler = UniversalInterfaceRegistry.getHandler(toLink, this);

		if (handler != null) {
			this.uniterface = handler;
		} else {
			player.addChatMessage("Could not link the universal interface to this link wand");
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

		ItemStack stack = linkedInventory.getStackInSlot(i);

		return stack;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		IInventory linkedInventory = this.getInventory();

		if (linkedInventory == null)
			return null;

		ItemStack stack = linkedInventory.decrStackSize(i, j);

		return stack;
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
	public String getInvName() {
		return "ephys_pp.blockinterface";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		IInventory linkedInventory = this.getInventory();
		return (linkedInventory != null) ? linkedInventory
				.getInventoryStackLimit() : 0;
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();

		IInventory linkedInventory = this.getInventory();
		if (linkedInventory != null)
			linkedInventory.onInventoryChanged();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		IInventory linkedInventory = this.getInventory();

		return (linkedInventory != null) ? linkedInventory.isItemValidForSlot(
				i, itemstack) : false;
	}

	// ================================================================================
	// ISidedInventory interface
	// ================================================================================

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		IInventory linkedInventory = this.getInventory();

		return (linkedInventory instanceof ISidedInventory) ? ((ISidedInventory) linkedInventory)
				.getAccessibleSlotsFromSide(var1)
				: linkedInventory instanceof IInventory ? getUnSidedInventorySlots((IInventory) linkedInventory)
						: new int[0];
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
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		IFluidHandler fluidHandler = this.getFluidHandler();

		return fluidHandler == null ? 0 : fluidHandler.fill(from, resource, doFill);
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

		return fluidHandler == null ?  false : fluidHandler.canFill(from, fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		IFluidHandler fluidHandler = this.getFluidHandler();

		return fluidHandler == null ? false : fluidHandler.canDrain(from, fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		IFluidHandler fluidHandler = this.getFluidHandler();

		return fluidHandler == null ? null : fluidHandler.getTankInfo(from);
	}
}

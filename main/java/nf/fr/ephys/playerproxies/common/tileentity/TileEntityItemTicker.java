package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import nf.fr.ephys.cookiecore.helpers.EntityHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;

public class TileEntityItemTicker extends TileEntity implements IInventory {
	private ItemStack item;
	private FakePlayer player;

	@Override
	public void validate() {
		super.validate();

		if (!worldObj.isRemote) {
			player = EntityHelper.getFakePlayer((WorldServer) worldObj);

			player.setPosition(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public void invalidate() {
		if (player != null)
			player.setDead();

		super.invalidate();
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
		NBTHelper.setWritable(nbt, "item", item);

		super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		item = NBTHelper.getItemStack(nbt, "item");

		super.readFromNBT(nbt);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return i == 0 ? item : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);

		if (stack != null && amount > 0) {
			setInventorySlotContents(slot, null);
		} else {
			onInventoryChanged();
		}

		return stack;
	}

	public boolean hasStackInSlot(int slot) {
		return slot == 0 && item != null && item.stackSize > 0;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack stack = getStackInSlot(i);

		if (stack != null) {
			setInventorySlotContents(i, null);
		} else {
			onInventoryChanged();
		}

		return stack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		this.item = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}

		onInventoryChanged();
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
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return i == 0 && itemstack != null && !(itemstack.getItem() instanceof ItemBlock);
	}

	public void onInventoryChanged() {
		if (worldObj != null)
			worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public void updateEntity() {
		if (this.player != null && this.item != null) {
			this.item.getItem().onUpdate(this.item, player.worldObj, player, 0, true);
		}

		super.updateEntity();
	}
}
package nf.fr.ephys.playerproxies.common.tileentity;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.FakePlayer;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.core.NetServerHandlerFake;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class TileEntityItemTicker extends TileEntity implements IInventory {
	private ItemStack item;
	private FakePlayer player;

	@Override
	public void validate() {
		player = new FakePlayer(worldObj, "[PlayerProxies::ItemTicker]");
		player.setPosition(xCoord, yCoord, zCoord);
		player.playerNetServerHandler =	new NetServerHandlerFake(FMLCommonHandler.instance().getMinecraftServerInstance(), player);

		super.validate();
	}

	@Override
	public void invalidate() {
		player.setDead();

		super.invalidate();
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
	public String getInvName() {
		return "ephys.pp.tileEntityItemTicker";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5, yCoord + 0.5,
						zCoord + 0.5) < 64;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return i == 0 && itemstack != null && !(itemstack.getItem() instanceof ItemBlock);
	}
	
	@Override
	public void onInventoryChanged() {
		if (worldObj != null)
			worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		
		super.onInventoryChanged();
	}
	
	@Override
	public void updateEntity() {
		if (this.item != null) {
			this.item.getItem().onUpdate(this.item, player.worldObj, player, 0, true);
		}

		super.updateEntity();
	}
}
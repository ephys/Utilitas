package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class TileEntityBiomeScanner extends TileEntity implements IInventory {
	public static final int NO_STORED_VALUE = 256; // max biome length = 256 (0 - 255)

	private int storedBiome = NO_STORED_VALUE;

	private int progress = -1;

	public ItemStack card;

	public BiomeGenBase getBiome() {
		if (storedBiome == NO_STORED_VALUE) {
			return this.worldObj.getBiomeGenForCoords(xCoord, zCoord);
		}

		return BiomeGenBase.getBiome(storedBiome);
	}

	public float getFloatTemperature() {
		return getBiome().getFloatTemperature(xCoord, yCoord, zCoord);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);

		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("progress", progress);
		NBTHelper.setWritable(nbt, "card", this.card);

		super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.progress = NBTHelper.getInt(nbt, "progress", -1);
		this.card = NBTHelper.getItemStack(nbt, "card", null);

		if (this.card != null)
			this.storedBiome = card.getItemDamage();
		else
			this.storedBiome = NO_STORED_VALUE;

		super.readFromNBT(nbt);
	}

	@Override
	public void updateEntity() {
		if (progress != -1 && this.worldObj.getTotalWorldTime() % 20 == 0) {
			if (progress++ == 100) {
				this.onReckoningEnd();
			}
		}

		super.updateEntity();
	}

	/**
	 * Initiate countdown, at the end of the countdown, set the biome id to the
	 * card
	 */
	public void startReckoning() {
		if (this.progress == -1)
			this.progress = 0;
	}

	/**
	 * Called at the end of the countdown
	 */
	private void onReckoningEnd() {
		this.progress = -1;

		this.card.setItemDamage(this.worldObj.getBiomeGenForCoords(xCoord, zCoord).biomeID);
	}

	/**
	 * @return the current reckoning status, integer from 0 to 100 or -1 if it
	 *         isn't active
	 */
	public int getProgress() {
		return this.progress;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return i == 0 ? card : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);

		if (stack != null && amount > 0) {
			setInventorySlotContents(slot, null);
		}

		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack stack = getStackInSlot(i);

		if (stack != null) {
			setInventorySlotContents(i, null);
		}

		return stack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();

		this.card = stack;

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
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return i == 0 && itemstack.getItem().equals(PlayerProxies.Items.biomeStorage);
	}

	public void onInventoryChanged() {
		if (this.card == null) {
			this.storedBiome = NO_STORED_VALUE;
			this.progress = -1;
		} else {
			this.storedBiome = this.card.getItemDamage();

			if (this.storedBiome == NO_STORED_VALUE)
				this.startReckoning();
			else
				this.progress = -1;
		}
	}
}
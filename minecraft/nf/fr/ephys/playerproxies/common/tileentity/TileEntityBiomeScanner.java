package nf.fr.ephys.playerproxies.common.tileentity;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class TileEntityBiomeScanner extends TileEntity implements IInventory {
	private byte biome;
	private byte storedBiome;

	private int tick = 0;
	private int progress = -1;

	public ItemStack card;

	public BiomeGenBase getBiome() {
		if (storedBiome != -1)
			return BiomeGenBase.biomeList[storedBiome];

		//BiomeGenBase.biomeList
		// chunk.getBiomeGenForWorldCoords(k3 & 15, l4 & 15, this.mc.theWorld.getWorldChunkManager()) <-- use that
		
		return BiomeGenBase.biomeList[biome];
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}
	
	public void setBiome(BiomeGenBase biome) {
		this.biome = (byte) biome.biomeID;
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
			this.storedBiome = (byte) NBTHelper.getInt(this.card, "biome", -1);
		
		super.readFromNBT(nbt);
	}

	@Override
	public void updateEntity() {
		if (this.tick++ % 20 == 0) {
			if (progress != -1) {
				if (progress++ == 100) {
					this.onReckoningEnd();
				}
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
		
		NBTHelper.setInt(this.card, "biome", this.biome);
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
		this.card = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInvName() {
		return "ephys.pp.tileEntityBiomeScanner";
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
		return itemstack.itemID == PlayerProxies.Items.biomeStorage.itemID;
	}

	@Override
	public void onInventoryChanged() {
		byte biome = (byte) NBTHelper.getInt(this.card, "biome", -1);

		if (this.card == null || biome != -1) {
			this.progress = -1;
			this.storedBiome = biome;
		} else
			this.startReckoning();

		super.onInventoryChanged();
	}
}
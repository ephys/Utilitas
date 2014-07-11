package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.core.ConfigHandler;
import nf.fr.ephys.playerproxies.common.network.PacketSetBiomeHandler;
import nf.fr.ephys.playerproxies.common.network.PacketSpawnParticleHandler;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;
import nf.fr.ephys.playerproxies.helpers.ParticleHelper;
import nf.fr.ephys.playerproxies.util.cofh.TileEnergyHandler;

public class TileEntityBiomeReplicator extends TileEnergyHandler implements IInventory {
	private static final int MAX_SIZE = 100;

	private int[][] bounds = null;

	private int biome = TileEntityBiomeScanner.NO_STORED_VALUE;
	private ItemStack biomeHandler;

	private int cursorX;
	private int cursorZ;

	public static final int REQUIRED_RF = 10000;

	public TileEntityBiomeReplicator() {
		super();
		bounds = generateTerrainBounds(MAX_SIZE);
		storage.setMaxReceive(200);
		storage.setCapacity(REQUIRED_RF << 1);
	}

	public int getBiome() {
		return biome;
	}

	public void setBiome(int biome) {
		this.biome = biome;
	}

	public void resetCursor() {
		cursorX = 0;
		cursorZ = 0;
	}

	public boolean hasBiome() {
		return biome != TileEntityBiomeScanner.NO_STORED_VALUE;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		cursorX = NBTHelper.getInt(nbt, "cursorX", 0);
		cursorZ = NBTHelper.getInt(nbt, "cursorZ", 0);
		biomeHandler = NBTHelper.getItemStack(nbt, "biomeHandler", null);

		biome = (biomeHandler == null ? TileEntityBiomeScanner.NO_STORED_VALUE : biomeHandler.getItemDamage());

		super.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("cursorX", cursorX);
		nbt.setInteger("cursorZ", cursorZ);
		nbt.setInteger("energyLevel", 0);

		NBTHelper.setWritable(nbt, "biomeHandler", biomeHandler);

		super.writeToNBT(nbt);
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

	private static int portalParticleID = ParticleHelper.getParticleIDFromName("portal");

	@Override
	public void updateEntity() {
		if (worldObj.isRemote) return;

		if (!hasBiome()) return;

		int halfBounds = bounds[0].length >> 1;
		if(cursorX >= halfBounds)
			return;

		int xRight = this.xCoord - cursorX;
		int xLeft = this.xCoord + cursorX;

		int zTop = this.zCoord + cursorZ;
		int zBottom = this.zCoord - cursorZ;

		if (worldObj.getTotalWorldTime() % 30 == 0) {
			for(int i = 0; i < 4; i++) {
				PacketSpawnParticleHandler.sendPacketSpawnParticle(portalParticleID, xRight + Math.random() / 2 + 0.25, this.yCoord + i + Math.random(), zBottom + Math.random() / 2 + 0.25, 0, 0, 0, worldObj.provider.dimensionId);
				PacketSpawnParticleHandler.sendPacketSpawnParticle(portalParticleID, xRight+Math.random() / 2 + 0.25, this.yCoord+i+Math.random(), zTop+Math.random() / 2 + 0.25, 0, 0, 0, worldObj.provider.dimensionId);
				PacketSpawnParticleHandler.sendPacketSpawnParticle(portalParticleID, xLeft+Math.random() / 2 + 0.25, this.yCoord+i+Math.random(), zBottom+Math.random() / 2 + 0.25, 0, 0, 0, worldObj.provider.dimensionId);
				PacketSpawnParticleHandler.sendPacketSpawnParticle(portalParticleID, xLeft+Math.random() / 2 + 0.25, this.yCoord+i+Math.random(), zTop+Math.random() / 2 + 0.25, 0, 0, 0, worldObj.provider.dimensionId);
			}
		}

		if (!PlayerProxies.getConfig().requiresPower()) {
			storage.setEnergyStored(storage.getEnergyStored() + storage.getMaxReceive());
		}

		if (storage.getEnergyStored() < REQUIRED_RF) return;

		this.storage.extractEnergy(REQUIRED_RF, false);

		boolean hasNextStep = false;
		if(cursorZ < bounds[0][halfBounds+cursorX]) {
			changeBiome(xLeft, zTop);

			hasNextStep = true;
		}

		if(cursorZ < bounds[1][halfBounds+cursorX]) {
			changeBiome(xLeft, zBottom);

			hasNextStep = true;
		}

		if(cursorZ < bounds[0][halfBounds-cursorX]) {
			changeBiome(xRight, zTop);

			hasNextStep = true;
		}

		if(cursorZ < bounds[1][halfBounds-cursorX]) {
			changeBiome(xRight, zBottom);

			hasNextStep = true;
		}

		cursorZ++;
		if(!hasNextStep) {
			cursorX++;
			cursorZ = 0;
		}

		super.updateEntity();
	}

	private void changeBiome(int x, int z) {
		BlockHelper.setBiome(worldObj, x, z, biome);
		PacketSetBiomeHandler.setClientBiome(x, z, biome, worldObj.provider.dimensionId);
	}

	private static int[][] generateTerrainBounds(int maxSize) {
		int halfSize = maxSize/2;
		int firstJump = (int)Math.pow(-halfSize, 2);

		int[] boundsPositive = new int[maxSize];
		int[] boundsNegative = new int[maxSize];
		for(int i = -halfSize; i < halfSize; i++) {
			double jump = -(Math.pow(i, 2)-firstJump) / maxSize;

			boundsPositive[i+halfSize] = (int)(jump*(Math.random()/4+1));
			boundsNegative[i+halfSize] = (int)(jump*(Math.random()/4+1));
		}

		return new int[][]{boundsPositive, boundsNegative};
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return i == 0 ? this.biomeHandler : null;
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
		this.biomeHandler = stack;

		int biome = biomeHandler == null ? TileEntityBiomeScanner.NO_STORED_VALUE : biomeHandler.getItemDamage();

		this.setBiome(biome);

		if (stack != null) {
			if (stack.stackSize > getInventoryStackLimit())
				stack.stackSize = getInventoryStackLimit();
		} else {
			this.resetCursor();
		}

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
		return i == 0 &&
				itemstack.getItem().equals(PlayerProxies.Items.biomeStorage) &&
				itemstack.getItemDamage() != TileEntityBiomeScanner.NO_STORED_VALUE;
	}
}
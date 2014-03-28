package nf.fr.ephys.playerproxies.common.tileentity;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeDirection;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class TileEntityBiomeReplicator extends TileEntity implements IInventory, IEnergyHandler {
	private static final int MAX_SIZE = 100;

	private int[][] bounds = null;

	private byte biome = -1;
	private ItemStack biomeHandler;
	
	private int cursorX;
	private int cursorZ;

	public static final int REQUIRED_RF = 10000;
	private EnergyStorage energyStorage = new EnergyStorage(REQUIRED_RF << 1);

	public TileEntityBiomeReplicator() {
		bounds = generateTerrainBounds(MAX_SIZE);
		energyStorage.setMaxTransfer(100);
	}

	public byte getBiome() {
		return biome;
	}
	
	public void setBiome(byte biome) {
		this.biome = biome;
	}
	
	public void resetCursor() {
		cursorX = 0;
		cursorZ = 0;
	}
	
	public boolean hasBiome() {
		return biome > -1;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		cursorX = NBTHelper.getInt(nbt, "cursorX", 0);
		cursorZ = NBTHelper.getInt(nbt, "cursorZ", 0);
		biomeHandler = NBTHelper.getItemStack(nbt, "biomeHandler", null);

		biome = (biomeHandler == null ? -1 : (byte) NBTHelper.getInt(biomeHandler, "biome", -1));
		
		if (nbt.hasKey("energyStorage"))
			this.energyStorage.readFromNBT(nbt.getCompoundTag("energyStorage"));

		super.readFromNBT(nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("cursorX", cursorX);
		nbt.setInteger("cursorZ", cursorZ);
		nbt.setInteger("energyLevel", 0);

		NBTHelper.setWritable(nbt, "biomeHandler", biomeHandler);
		
		NBTTagCompound energyStorageNBT = new NBTTagCompound();
		
		energyStorage.readFromNBT(energyStorageNBT);

		nbt.setCompoundTag("energyStorage", energyStorageNBT);

		super.writeToNBT(nbt);
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
	public void updateEntity() {
		if(!hasBiome()) return;

		int halfBounds = bounds[0].length >> 1;
		if(cursorX >= halfBounds)
			return;

		int xRight = this.xCoord - cursorX;
		int xLeft = this.xCoord + cursorX;

		int zTop = this.zCoord + cursorZ;
		int zBottom = this.zCoord - cursorZ;

		for(int i = 0; i < 4; i++) {
			worldObj.spawnParticle("portal", xRight+Math.random() / 2 + 0.25, this.yCoord+i+Math.random(), zBottom+Math.random() / 2 + 0.25, 0, 0, 0);
			worldObj.spawnParticle("portal", xRight+Math.random() / 2 + 0.25, this.yCoord+i+Math.random(), zTop+Math.random() / 2 + 0.25, 0, 0, 0);
			worldObj.spawnParticle("portal", xLeft+Math.random() / 2 + 0.25, this.yCoord+i+Math.random(), zBottom+Math.random() / 2 + 0.25, 0, 0, 0);
			worldObj.spawnParticle("portal", xLeft+Math.random() / 2 + 0.25, this.yCoord+i+Math.random(), zTop+Math.random() / 2 + 0.25, 0, 0, 0);
		}

		if (PlayerProxies.requiresPower())
			energyStorage.setEnergyStored(energyStorage.getEnergyStored() + energyStorage.getMaxReceive());

		if (energyStorage.getEnergyStored() < REQUIRED_RF) return;

		this.energyStorage.extractEnergy(REQUIRED_RF, false);

		boolean hasNextStep = false;
		if(cursorZ < bounds[0][halfBounds+cursorX]) {
			Chunk chunk = worldObj.getChunkFromBlockCoords(xLeft, zTop);
			byte[] biomes = chunk.getBiomeArray();
			biomes[(zTop & 15) << 4 | (xLeft & 15)] = biome;
			
			chunk.setBiomeArray(biomes);
			chunk.setChunkModified();
			
			hasNextStep = true;
		}

		if(cursorZ < bounds[1][halfBounds+cursorX]) {
			Chunk chunk = worldObj.getChunkFromBlockCoords(xLeft, zBottom);
			byte[] biomes = chunk.getBiomeArray();
			biomes[(zBottom & 15) << 4 | (xLeft & 15)] = biome;
			
			chunk.setBiomeArray(biomes);
			chunk.setChunkModified();
			
			hasNextStep = true;
		}

		if(cursorZ < bounds[0][halfBounds-cursorX]) {
			Chunk chunk = worldObj.getChunkFromBlockCoords(xRight, zTop);
			byte[] biomes = chunk.getBiomeArray();
			biomes[(zTop & 15) << 4 | (xRight & 15)] = biome;
			
			chunk.setBiomeArray(biomes);
			chunk.setChunkModified();
			
			hasNextStep = true;
		}

		if(cursorZ < bounds[1][halfBounds-cursorX]) {
			Chunk chunk = worldObj.getChunkFromBlockCoords(xRight, zBottom);
			byte[] biomes = chunk.getBiomeArray();
			biomes[(zBottom & 15) << 4 | (xRight & 15)] = biome;
			
			chunk.setBiomeArray(biomes);
			chunk.setChunkModified();
			
			hasNextStep = true;
		}
		
		cursorZ++;
		if(!hasNextStep) {
			cursorX++;
			cursorZ = 0;
		}

		super.updateEntity();
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

		if (stack != null) {
			if (stack.stackSize > getInventoryStackLimit())
				stack.stackSize = getInventoryStackLimit();
			
			byte biome = (byte) NBTHelper.getInt(biomeHandler, "biome", -1);
			this.setBiome(biome);
		} else {
			this.resetCursor();
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
		return i == 0 && itemstack.itemID == PlayerProxies.itemBiomeStorage.itemID && NBTHelper.getInt(itemstack, "biome", -1) != -1;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		return this.energyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		return this.energyStorage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public boolean canInterface(ForgeDirection from) {
		return true;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return this.energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return this.energyStorage.getMaxEnergyStored();
	}	
}
package nf.fr.ephys.playerproxies.common.tileentity;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class TileEntityBiomeScanner extends TileEntity {
	private byte biome;

	private int tick = 0;
	private int progress = -1;
	
	private Random random;
	
	public TileEntityBiomeScanner() {
		random = new Random();
	}
	
	public BiomeGenBase getBiome() {
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
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("progress", progress);
		super.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.progress = NBTHelper.getInt(nbt, "progress", -1);
		super.readFromNBT(nbt);
	}

	@Override
	public void updateEntity() {
		if (random.nextDouble() > 0.9D) {
			if (progress != -1) {
				if (progress++ == 100) {
					this.onReckoningEnd();
				}
			}
		}

		super.updateEntity();
	}

	/**
	 * Initiate countdown, at the end of the countdown, set the biome id to the card
	 */
	public void startReckoning() {
		this.progress = 0;
	}

	/**
	 * Called at the end of the countdown
	 */
	private void onReckoningEnd() {
		this.progress = -1;
	}

	/**
	 * @return the current reckoning status, integer from 0 to 100 or -1 if it isn't active
	 */
	public int getProgress() {
		// TODO Auto-generated method stub
		return this.progress;
	}

}
package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class TileEntityBiomeReplicator extends TileEntity {
	private static final int MAX_SIZE = 100;

	private int[][] bounds = null;
	private byte biome = (byte) (BiomeGenBase.taiga.biomeID & 255);

	private int cursorX;
	private int cursorZ;

	private int tick = 0;
	
	public TileEntityBiomeReplicator() {
		bounds = generateTerrainBounds(MAX_SIZE);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		cursorX = NBTHelper.getInt(nbt, "cursorX", 0);
		cursorZ = NBTHelper.getInt(nbt, "cursorZ", 0);

		super.readFromNBT(nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("cursorX", cursorX);
		nbt.setInteger("cursorZ", cursorZ);

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
		tick++;

		int halfBounds = bounds[0].length >> 1;
		if(cursorX >= halfBounds)
			return;

		int xRight = this.xCoord - cursorX;
		int xLeft = this.xCoord + cursorX;
		
		int zTop = this.zCoord + cursorZ;
		int zBottom = this.zCoord - cursorZ;

		for(int i = -5; i < 5; i++) {
			worldObj.spawnParticle("portal", xRight+Math.random(), this.yCoord+i+Math.random(), zBottom+Math.random(), 0, 0, 0);
			worldObj.spawnParticle("portal", xRight+Math.random(), this.yCoord+i+Math.random(), zTop+Math.random(), 0, 0, 0);
			worldObj.spawnParticle("portal", xLeft+Math.random(), this.yCoord+i+Math.random(), zBottom+Math.random(), 0, 0, 0);
			worldObj.spawnParticle("portal", xLeft+Math.random(), this.yCoord+i+Math.random(), zTop+Math.random(), 0, 0, 0);
		}
		
		if(tick % 10 != 0)
			return;

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
}
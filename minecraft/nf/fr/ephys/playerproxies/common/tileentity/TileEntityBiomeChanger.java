package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class TileEntityBiomeChanger extends TileEntity {
	private static final int MAX_SIZE = 100;

	private double seed = Math.random()+1;
	private int[][] bounds = null;
	private byte biome = (byte) (BiomeGenBase.taiga.biomeID & 255);
	private int cursor = 0;

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		cursor = NBTHelper.getInt(nbt, "cursor", 0);

		bounds = new int[2][];
		bounds[0] = NBTHelper.getIntArray(nbt, "boundsPositive", new int[MAX_SIZE]);
		bounds[1] = NBTHelper.getIntArray(nbt, "boundsNegative", new int[MAX_SIZE]);
		
		super.readFromNBT(nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("cursor", cursor);

		NBTHelper.setIntArray(nbt, "boundsPositive", bounds[0]);
		NBTHelper.setIntArray(nbt, "boundsNegative", bounds[1]);

		super.writeToNBT(nbt);
	}
	
	@Override
	public void updateEntity() {
		if(bounds == null) {
			bounds = generateTerrainBounds(MAX_SIZE);
		}

		int start = this.xCoord - (bounds[0].length/2);
		
		for(int i = 0; i < bounds[0].length; i++) {
			int x = start + i;
			
			for(int j = 0; j < bounds[0][i]; j++) {
				int z = this.zCoord + j;

				Chunk chunk = worldObj.getChunkFromBlockCoords(x, z);
				byte[] biomes = chunk.getBiomeArray();
				biomes[(z & 15) << 4 | (x & 15)] = biome;
			}
		}
		
		for(int i = 0; i < bounds[1].length; i++) {
			int x = start + i;
			
			for(int j = 0; j < bounds[1][i]; j++) {
				int z = this.zCoord - j;

				Chunk chunk = worldObj.getChunkFromBlockCoords(x, z);
				byte[] biomes = chunk.getBiomeArray();
				biomes[(z & 15) << 4 | (x & 15)] = biome;
			}
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
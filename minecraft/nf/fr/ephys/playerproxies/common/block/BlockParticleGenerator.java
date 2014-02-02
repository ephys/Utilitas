package nf.fr.ephys.playerproxies.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockParticleGenerator extends Block {
	public static int blockID = 803;

	private String[] particleList = new String[] { "depthsuspend", "smoke",
			"mobSpell", "spell", "instantSpell", "note", "portal",
			"enchantmenttable", "flame", "lava", "splash", "reddust", "heart" };
	
//private Block block;

	public BlockParticleGenerator() {
		super(blockID, Material.iron);
	}
	
	@Override
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		int[] offset = new int[3];

		int offsetType = 0;
		Block block;

		do {
			offset[0] = 0;
			offset[1] = 0;
			offset[2] = 0;
			
			offset[offsetType] = -1;
			offsetType++;

			block = Block.blocksList[par1iBlockAccess.getBlockId(par2+offset[0], par3+offset[1], par4+offset[2])];
		} while ((block == null || block == this || !block.isOpaqueCube()) && offsetType != 3);
	
		offsetType = 0;
		
		if (block == null || !block.isOpaqueCube()) {
			do {
				offset[0] = 0;
				offset[1] = 0;
				offset[2] = 0;
				
				offset[offsetType] = 1;
				offsetType++;
				
				block = Block.blocksList[par1iBlockAccess.getBlockId(par2+offset[0], par3+offset[1], par4+offset[2])];
			} while ((block == null || block == this || !block.isOpaqueCube()) && offsetType != 3);
		}
		
		if (block == null || block == this || !block.isOpaqueCube())
			return super.getBlockTexture(par1iBlockAccess, par2, par3, par4, par5);
		
		return block.getBlockTexture(par1iBlockAccess, par2+offset[0], par3+offset[1], par4+offset[2], par5);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z,
			Random random) {
		int powerHeight = world.getBlockPowerInput(x, y, z);
		byte particleID = (byte)world.getBlockMetadata(x, y, z);
		
		int speed = (particleID == 1 || particleID == 8) ? 0 : 1;

		for (int i = 0; i < powerHeight + 1; i++) {
			world.spawnParticle(this.particleList[particleID], x + random.nextFloat(),
					y + random.nextFloat() + (i >> 1), z + random.nextFloat(),
					0, speed, 0);
		}

		super.randomDisplayTick(world, x, y, z, random);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {

		int metadata = world.getBlockMetadata(x, y, z);

		if(player.isSneaking()) {
			if (++metadata == this.particleList.length)
				metadata = 0;
		} else {
			if (--metadata == -1)
				metadata = this.particleList.length-1;
		}

		world.setBlockMetadataWithNotify(x, y, z, metadata, 2);

		return super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
	}
}

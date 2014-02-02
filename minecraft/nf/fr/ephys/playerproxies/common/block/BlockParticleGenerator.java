package nf.fr.ephys.playerproxies.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockParticleGenerator extends Block {
	public static int blockID = 803;
	
	public BlockParticleGenerator() {
		super(blockID, Material.iron);
	}
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		int powerHeight = world.getBlockPowerInput(x, y, z);

		for (int i = 0; i < powerHeight+1; i++) {
			world.spawnParticle("portal", x+random.nextFloat(), y+random.nextFloat()+(i>>1), z+random.nextFloat(), 0, 1, 0);
		}

		super.randomDisplayTick(world, x, y, z, random);
	}
}

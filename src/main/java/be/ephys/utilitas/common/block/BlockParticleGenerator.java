package be.ephys.utilitas.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

import java.util.Random;

public class BlockParticleGenerator extends Block {
	public static int BLOCK_ID = 803;
	public static boolean enabled = true;

	private IIcon[] textures;

	private static String[] particleList = new String[] { "depthsuspend", "smoke",
			"mobSpell", "spell", "instantSpell", "note", "portal",
			"enchantmenttable", "flame", "lava", "splash", "reddust", "heart" };

	public static void register() {
		if (!enabled) return;

		PlayerProxies.Blocks.particleGenerator = new BlockParticleGenerator(Material.iron);
		PlayerProxies.Blocks.particleGenerator.setBlockName("PP_ParticleGenerator")
			.setHardness(2F)
			.setResistance(500.0F)
			.setCreativeTab(PlayerProxies.creativeTab);

		GameRegistry.registerBlock(PlayerProxies.Blocks.particleGenerator, PlayerProxies.Blocks.particleGenerator.getUnlocalizedName());
	}

	public static void registerCraft() {
		if (!enabled) return;

		GameRegistry.addShapelessRecipe(new ItemStack(PlayerProxies.Blocks.particleGenerator), Blocks.iron_bars, PlayerProxies.Blocks.hardenedStone);
	}

	public BlockParticleGenerator(Material material) {
		super(material);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		return textures[side];
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		textures = new IIcon[6];

		textures[0] = register.registerIcon("ephys.pp:particle_generator_bottom");
		textures[1] = register.registerIcon("ephys.pp:particle_generator_top");
		textures[2] = register.registerIcon("ephys.pp:particle_generator_north");
		textures[3] = register.registerIcon("ephys.pp:particle_generator_south");
		textures[4] = register.registerIcon("ephys.pp:particle_generator_west");
		textures[5] = register.registerIcon("ephys.pp:particle_generator_east");
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		int powerHeight = Math.max(world.getBlockPowerInput(x, y, z), world.getStrongestIndirectPower(x, y, z));
		byte particleID = (byte)world.getBlockMetadata(x, y, z);

		int speed = (particleID == 1 || particleID == 8) ? 0 : 1;

		for (int i = 0; i < powerHeight + 1; i++) {
			world.spawnParticle(particleList[particleID], x + random.nextFloat(),
					y + random.nextFloat() + (i >> 1), z + random.nextFloat(),
					0, speed, 0);
		}

		super.randomDisplayTick(world, x, y, z, random);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {

		if(player.getHeldItem() != null)
			return false;

		int metadata = world.getBlockMetadata(x, y, z);

		if(player.isSneaking()) {
			if (++metadata == particleList.length)
				metadata = 0;
		} else {
			if (--metadata == -1)
				metadata = particleList.length - 1;
		}

		world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
		world.playSoundEffect((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, "random.orb", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);

		return true;
	}
}

package nf.fr.ephys.playerproxies.common.block;

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

	private IIcon iconSide;
	private IIcon iconTop;

	private static String[] particleList = new String[] { "depthsuspend", "smoke",
			"mobSpell", "spell", "instantSpell", "note", "portal",
			"enchantmenttable", "flame", "lava", "splash", "reddust", "heart" };

	public static void register() {
		PlayerProxies.Blocks.particleGenerator = new BlockParticleGenerator(Material.iron);
		PlayerProxies.Blocks.particleGenerator.setBlockName("PP_ParticleGenerator")
			.setHardness(2F)
			.setResistance(500.0F)
			.setCreativeTab(PlayerProxies.creativeTab);

		GameRegistry.registerBlock(PlayerProxies.Blocks.particleGenerator, PlayerProxies.Blocks.particleGenerator.getUnlocalizedName());
	}

	public static void registerCraft() {
		GameRegistry.addShapelessRecipe(new ItemStack(PlayerProxies.Blocks.particleGenerator), Blocks.iron_bars, PlayerProxies.Blocks.hardenedStone);
	}

	public BlockParticleGenerator(Material material) {
		super(material);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		return side == 1 ? iconTop : iconSide;
	}

	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		this.iconTop = par1IconRegister.registerIcon("ephys.pp:particleGenerator");
		this.iconSide = par1IconRegister.registerIcon("ephys.pp:hardenedStone");
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}

	public static int getRedstoneLevel(World world, int x, int y, int z) {
		int maxInput = 0;

		for (int i = 0; i < 6; i++) {
			int input = world.getIndirectPowerLevelTo(x, y, z, i);

			if (input > maxInput)
				maxInput = input;
		}

		return maxInput;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		int powerHeight = getRedstoneLevel(world, x, y, z);
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

package nf.fr.ephys.playerproxies.common.block;

import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

public class BlockParticleGenerator extends Block {
	public static int BLOCK_ID = 803;
	
	private Icon iconSide;
	private Icon iconTop;

	private static String[] particleList = new String[] { "depthsuspend", "smoke",
			"mobSpell", "spell", "instantSpell", "note", "portal",
			"enchantmenttable", "flame", "lava", "splash", "reddust", "heart" };
	
	public static void register() {
		PlayerProxies.blockParticleGenerator = new BlockParticleGenerator();
		PlayerProxies.blockParticleGenerator.setUnlocalizedName("PP_ParticleGenerator");
		GameRegistry.registerBlock(PlayerProxies.blockParticleGenerator, "PP_ParticleGenerator");
		LanguageRegistry.instance().addName(PlayerProxies.blockParticleGenerator, "Particle Generator");
		
		GameRegistry.addShapelessRecipe(new ItemStack(PlayerProxies.blockParticleGenerator), Block.fenceIron, PlayerProxies.blockHardenedStone);
	}
	
	public static void registerCraft() {
		GameRegistry.addShapelessRecipe(new ItemStack(PlayerProxies.blockParticleGenerator), Block.fenceIron, PlayerProxies.blockHardenedStone);
	}

	public BlockParticleGenerator() {
		super(BLOCK_ID, Material.iron);
		
		setHardness(2F);
		setResistance(500.0F);
		
		setCreativeTab(PlayerProxies.creativeTab);
	}

	@Override
	public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int par5) {
		int[] offset = new int[] { 0, 0, 0 };

		int offsetType = 0;
		Block block;

		do {
			offset[offsetType] = 1;
			offsetType++;

			block = Block.blocksList[blockAccess.getBlockId(x+offset[0], y+offset[1], z+offset[2])];

			if (block != null) {
				if (block.blockID == PlayerProxies.blockHardenedStone.blockID) {
					block = null;
					break;
				}
				
				if(block != this && block.isOpaqueCube())
					break;
			}

			block = Block.blocksList[blockAccess.getBlockId(x-offset[0], y-offset[1], z-offset[2])];
			
			if (block != null) {
				if (block.blockID == PlayerProxies.blockHardenedStone.blockID) {
					block = null;
					break;
				}
				
				if(block != this && block.isOpaqueCube())
					break;
			}
		} while (offsetType != 3);

		if (block == null || block == this || !block.isOpaqueCube())
			return par5 == 1 ? iconTop : iconSide;

		return block.getBlockTexture(blockAccess, x+offset[0], y+offset[1], z+offset[2], par5);
	}
	
	@Override
	public Icon getIcon(int par1, int par2) {
		return par1 == 1 ? iconTop : iconSide;
	}
	
	@Override
	public void registerIcons(IconRegister par1IconRegister) {
		this.iconTop = par1IconRegister.registerIcon("ephys.pp:particleGenerator");
		this.iconSide = par1IconRegister.registerIcon("ephys.pp:hardenedStone");
	}
	
	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return true;
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
		
		if(player.getHeldItem() != null)
			return false;

		int metadata = world.getBlockMetadata(x, y, z);

		if(player.isSneaking()) {
			if (++metadata == this.particleList.length)
				metadata = 0;
		} else {
			if (--metadata == -1)
				metadata = this.particleList.length-1;
		}

		world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
		world.playSoundEffect((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, "random.orb", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);

		return true;
	}
}

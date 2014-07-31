package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.client.renderer.BlockFluidDiffuserRenderer;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityPotionDiffuser;

import java.util.Random;

public class BlockFluidDiffuser extends BlockContainer {
	public static boolean enabled = true;

	private IIcon[] sides;
	private IIcon dynamicTextureTop;
	private IIcon dynamicTextureSide;

	protected BlockFluidDiffuser(Material material) {
		super(material);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		world.spawnParticle("portal",
				x + random.nextFloat(),
				y + random.nextFloat() + 0.5,
				z + random.nextFloat(),
				0, 0, 0);

		super.randomDisplayTick(world, x, y, z, random);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		return side > 2 ? sides[3] : sides[side];
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return BlockFluidDiffuserRenderer.RENDER_ID;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		sides = new IIcon[5];

		sides[0] = register.registerIcon("ephys.pp:fluid_diffuser_bottom");
		sides[1] = register.registerIcon("ephys.pp:fluid_diffuser_top");
		sides[2] = register.registerIcon("ephys.pp:fluid_diffuser_south");
		sides[3] = register.registerIcon("ephys.pp:fluid_diffuser_west");

		dynamicTextureTop = register.registerIcon("ephys.pp:fluid_diffuser_top_dynamic");
		dynamicTextureSide = register.registerIcon("ephys.pp:fluid_diffuser_side_dynamic");
	}

	public static void register() {
		if (!enabled) return;

		PlayerProxies.Blocks.fluidDiffuser = new BlockFluidDiffuser(Material.iron);
		PlayerProxies.Blocks.fluidDiffuser.setBlockName("PP_FluidDiffuser")
				.setHardness(2.5F)
				.setResistance(5000.0F)
				.setCreativeTab(PlayerProxies.creativeTab);

		GameRegistry.registerBlock(PlayerProxies.Blocks.fluidDiffuser, PlayerProxies.Blocks.fluidDiffuser.getUnlocalizedName());

		GameRegistry.registerTileEntity(TileEntityPotionDiffuser.class, "PP_PotionDiffuser");
	}

	public static void registerCraft() {
		if (!enabled) return;

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.fluidDiffuser),
				"p", "g", "d",
				'p', Items.glass_bottle,
				'g', PlayerProxies.Blocks.particleGenerator != PlayerProxies.Blocks.particleGenerator ? Blocks.obsidian : PlayerProxies.Blocks.hardenedStone,
				'd', Items.diamond);
	}

	public IIcon getDynamicTextureSide() {
		return dynamicTextureSide;
	}

	public IIcon getDynamicTextureTop() {
		return dynamicTextureTop;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityPotionDiffuser();
	}
}

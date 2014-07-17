package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.item.MultitemBlock;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityPotionDiffuser;

import java.util.List;
import java.util.Random;

public class BlockHardenedStone extends BlockContainer {
	public static final int METADATA_HARDENED_STONE = 0;
	public static final int METADATA_POTION_DIFFUSER = 1;

	private IIcon iconSide;
	private IIcon iconTop;

	public static void register() {
		PlayerProxies.Blocks.hardenedStone = new BlockHardenedStone(Material.iron);
		PlayerProxies.Blocks.hardenedStone.setBlockName("PP_HardenedStone")
				.setHardness(2.5F)
				.setResistance(5000.0F)
				.setCreativeTab(PlayerProxies.creativeTab);

		GameRegistry.registerBlock(PlayerProxies.Blocks.hardenedStone, MultitemBlock.class, PlayerProxies.Blocks.hardenedStone.getUnlocalizedName());

		GameRegistry.registerTileEntity(TileEntityPotionDiffuser.class, "PP_PotionDiffuser");
	}

	public static void registerCraft() {
		if(Loader.isModLoaded("IC2")) {
			GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.hardenedStone, 8, METADATA_HARDENED_STONE),
					"ioi", "oso", "ioi",
					'i', ic2.api.item.Items.getItem("advancedAlloy"),
					's', ic2.api.item.Items.getItem("reinforcedStone"),
					'o', new ItemStack(Blocks.obsidian));
		} else {
			GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.hardenedStone, 6, METADATA_HARDENED_STONE),
					"ioi", "oso", "ioi",
					'i', Items.iron_ingot,
					's', Blocks.stone,
					'o', Blocks.obsidian);
		}

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.hardenedStone, 1, METADATA_POTION_DIFFUSER),
				"p", "g", "d",
				'p', Items.glass_bottle,
				'g', PlayerProxies.Blocks.particleGenerator,
				'd', Items.diamond);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item unknown, CreativeTabs tab, List subItems) {
		subItems.add(new ItemStack(this, 1, METADATA_HARDENED_STONE));
		subItems.add(new ItemStack(this, 1, METADATA_POTION_DIFFUSER));
	}

	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}

	public BlockHardenedStone(Material material) {
		super(material);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		return side == 1 && metadata == METADATA_POTION_DIFFUSER ? iconTop : iconSide;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.iconTop = register.registerIcon("ephys.pp:particleGenerator");
		this.iconSide = register.registerIcon("ephys.pp:hardenedStone");
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		if (world.getBlockMetadata(x, y, z) == METADATA_POTION_DIFFUSER) {
			world.spawnParticle("portal",
					x + random.nextFloat(),
					y + random.nextFloat() + 0.5,
					z + random.nextFloat(),
					0, 0, 0);
		}

		super.randomDisplayTick(world, x, y, z, random);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		if (metadata == METADATA_HARDENED_STONE) return null;
		return new TileEntityPotionDiffuser();
	}

/*	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int metadata) {
		if (metadata == METADATA_POTION_DIFFUSER) {
			TileEntity te = world.getTileEntity(x, y, z);

			if (te != null)
				BlockHelper.dropContents((TileEntityPotionDiffuser) te, world, x, y, z);
		}

		super.onBlockPreDestroy(world, x, y, z, metadata);
	}*/
}
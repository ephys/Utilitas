package nf.fr.ephys.playerproxies.common.block;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.item.MultitemBlock;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityPotionDiffuser;

public class BlockHardenedStone extends BlockContainer {
	public static int BLOCK_ID = 802;
	
	public static final int METADATA_HARDENED_STONE = 0;
	public static final int METADATA_POTION_DIFFUSER = 1;
	
	private Icon iconSide;
	private Icon iconTop;
	
	public static final String[] blockNames = {
		"Hardened Stone", "Potion diffuser"
	};

	public static void register() {
		PlayerProxies.Blocks.hardenedStone = new BlockHardenedStone(BlockHardenedStone.BLOCK_ID, Material.iron);
		PlayerProxies.Blocks.hardenedStone.setUnlocalizedName("PP_HardenedStone");
		GameRegistry.registerBlock(PlayerProxies.Blocks.hardenedStone, MultitemBlock.class, "PP_HardenedStone");

		GameRegistry.registerTileEntity(TileEntityPotionDiffuser.class, "PP_PotionDiffuser");

		for (int metadata = 0; metadata < blockNames.length; metadata++) {
			ItemStack stackMultiBlock = new ItemStack(PlayerProxies.Blocks.hardenedStone, 1, metadata);

			LanguageRegistry.addName(stackMultiBlock, blockNames[metadata]);
			LanguageRegistry.instance().addStringLocalization("PP_HardenedStone." + metadata, "EN_US", blockNames[metadata]);
		}
	}

	public static void registerCraft() {
		if(Loader.isModLoaded("IC2")) {
			GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.hardenedStone, 8, METADATA_HARDENED_STONE),
					"ioi", "oso", "ioi", 
					'i', ic2.api.item.Items.getItem("advancedAlloy"), 
					's', ic2.api.item.Items.getItem("reinforcedStone"), 
					'o', new ItemStack(Block.obsidian));
		} else {
			GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.hardenedStone, 6, METADATA_HARDENED_STONE),
					"ioi", "oso", "ioi", 
					'i', Item.ingotIron, 
					's', Block.stone, 
					'o', Block.obsidian);
		}

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.hardenedStone, 1, METADATA_POTION_DIFFUSER),
				"p", "g", "d", 
				'p', Item.glassBottle, 
				'g', PlayerProxies.Blocks.particleGenerator, 
				'd', Item.diamond);
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int unknown, CreativeTabs tab, List subItems) {
		for (int i = 0; i < blockNames.length; i++) {
			subItems.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}
	
	public BlockHardenedStone(int id, Material material) {
		super(id, material);

		setHardness(2.5F);
		setResistance(5000.0F);
		setCreativeTab(PlayerProxies.creativeTab);
	}
	
	@Override
	public Icon getIcon(int side, int metadata) {
		return side == 1 && metadata == METADATA_POTION_DIFFUSER ? iconTop : iconSide;
	}

	@Override
	public void registerIcons(IconRegister register) {
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
	public TileEntity createNewTileEntity(World world) {
		return null;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		if (metadata == METADATA_HARDENED_STONE) return null;
		return new TileEntityPotionDiffuser();
	}
}
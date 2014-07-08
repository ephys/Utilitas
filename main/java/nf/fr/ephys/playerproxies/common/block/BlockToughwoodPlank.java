package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.item.MultitemBlock;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeReplicator;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;

import java.util.List;

public class BlockToughwoodPlank extends BlockContainer {
	public static final int METADATA_TOUGHWOOD = 0;
	public static final int METADATA_BIOME_REPLICATOR = 1;

	private IIcon iconSideClean;
	private IIcon iconSideReplicator;
	private IIcon iconBottom;
	private IIcon iconTop;

	public static void register() {
		PlayerProxies.Blocks.toughwoodPlank = new BlockToughwoodPlank(Material.wood);
		PlayerProxies.Blocks.toughwoodPlank.setBlockName("PP_ToughwoodPlank")
			.setHardness(1.0F)
			.setCreativeTab(PlayerProxies.creativeTab)
			.setStepSound(soundTypeWood);

		GameRegistry.registerBlock(PlayerProxies.Blocks.toughwoodPlank, MultitemBlock.class, PlayerProxies.Blocks.toughwoodPlank.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityBiomeReplicator.class, "PP_BiomeChanger");
	}

	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.toughwoodPlank, 4, METADATA_TOUGHWOOD),
				" b ", "bwb", " b ",
				'b', new ItemStack(Blocks.planks, 1, 2),
				'w', new ItemStack(Blocks.log)
		);

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.toughwoodPlank, 1, METADATA_BIOME_REPLICATOR),
				" c ", "pdp", " p ",
				'c', new ItemStack(PlayerProxies.Items.linkFocus),
				'p', new ItemStack(PlayerProxies.Blocks.toughwoodPlank),
				'd', new ItemStack(Blocks.diamond_block)
		);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item unknown, CreativeTabs tab, List subItems) {
		subItems.add(new ItemStack(this, 1, METADATA_BIOME_REPLICATOR));
		subItems.add(new ItemStack(this, 1, METADATA_TOUGHWOOD));
	}

	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return metadata == METADATA_BIOME_REPLICATOR;
	}

	public BlockToughwoodPlank(Material material) {
		super(material);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		switch(side) {
			case 0:
				return iconBottom;
			case 1:
				return (metadata == METADATA_BIOME_REPLICATOR) ? iconTop : iconBottom;
			default:
				return metadata == METADATA_BIOME_REPLICATOR ? iconSideReplicator : iconSideClean;
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		iconTop    			= register.registerIcon("ephys.pp:biomeChangerTop");
		iconBottom 			= register.registerIcon("ephys.pp:biomeChangerBottom");
		iconSideClean		= register.registerIcon("ephys.pp:biomeScannerSide");
		iconSideReplicator	= register.registerIcon("ephys.pp:biomeChangerSide");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return metadata == METADATA_BIOME_REPLICATOR ? new TileEntityBiomeReplicator() : null;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float par7, float par8, float par9) {
		if (world.getBlockMetadata(x, y, z) != METADATA_BIOME_REPLICATOR) return false;

		if (world.isRemote) return true;

		TileEntityBiomeReplicator te = (TileEntityBiomeReplicator) world.getTileEntity(x, y, z);

		if (te.hasBiome()) {
			BlockHelper.dropContents(te, world, x, y, z);
		}

		if (player.getHeldItem() != null && te.isItemValidForSlot(0, player.getHeldItem())) {
			te.setInventorySlotContents(0, player.getHeldItem().copy());
			player.getHeldItem().stackSize--;
		}

		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int metadata) {
		if (metadata == METADATA_BIOME_REPLICATOR) {
			TileEntityBiomeReplicator te = (TileEntityBiomeReplicator) world.getTileEntity(x, y, z);

			if (te != null)
				BlockHelper.dropContents(te, world, x, y, z);
		}

		super.onBlockPreDestroy(world, x, y, z, metadata);
	}
}

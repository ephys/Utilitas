package nf.fr.ephys.playerproxies.common.block;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.item.MultitemBlock;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeReplicator;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;

public class BlockToughwoodPlank extends BlockContainer {
	public static int BLOCK_ID = 807;

	public static final int METADATA_TOUGHWOOD = 0;
	public static final int METADATA_BIOME_REPLICATOR = 1;
	
	private Icon iconSideClean;
	private Icon iconSideReplicator;
	private Icon iconBottom;
	private Icon iconTop;
	
	public static final String[] blockNames = {
		"Thoughwood plank", "Biome transmuter"
	};

	public static void register() {
		PlayerProxies.blockToughwoodPlank = new BlockToughwoodPlank(BLOCK_ID, Material.wood);
		PlayerProxies.blockToughwoodPlank.setUnlocalizedName("PP_ToughwoodPlank");
		
		GameRegistry.registerBlock(PlayerProxies.blockToughwoodPlank, MultitemBlock.class, "PP_ToughwoodPlank", PlayerProxies.modid);
		GameRegistry.registerTileEntity(TileEntityBiomeReplicator.class, "PP_BiomeChanger");

		for (int metadata = 0; metadata < blockNames.length; metadata++) {
			ItemStack stackMultiBlock = new ItemStack(PlayerProxies.blockToughwoodPlank, 1, metadata);

			LanguageRegistry.addName(stackMultiBlock, blockNames[metadata]);
		}
	}
	
	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockToughwoodPlank, 4, METADATA_TOUGHWOOD), 
				" b ", "bwb", " b ",
				'b', new ItemStack(Block.planks, 1, 2),
				'w', new ItemStack(Block.wood)
		);

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockToughwoodPlank, 1, METADATA_BIOME_REPLICATOR), 
				" c ", "pdp", " p ",
				'c', new ItemStack(PlayerProxies.itemLinkFocus),
				'p', new ItemStack(PlayerProxies.blockToughwoodPlank),
				'd', new ItemStack(Block.blockDiamond)
		);
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
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return metadata == METADATA_BIOME_REPLICATOR;
	}
	
	public BlockToughwoodPlank(int id, Material material) {
		super(id, material);

		setHardness(1.0F);
		setCreativeTab(PlayerProxies.creativeTab);

		setStepSound(soundWoodFootstep);
	}

	@Override
	public Icon getIcon(int side, int metadata) {
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
	public void registerIcons(IconRegister register) {
		iconTop    			= register.registerIcon("ephys.pp:biomeChangerTop");
		iconBottom 			= register.registerIcon("ephys.pp:biomeChangerBottom");
		iconSideClean		= register.registerIcon("ephys.pp:biomeScannerSide");
		iconSideReplicator	= register.registerIcon("ephys.pp:biomeChangerSide");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return metadata == METADATA_BIOME_REPLICATOR ? new TileEntityBiomeReplicator() : null;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float par7, float par8, float par9) {
		if (metadata != METADATA_BIOME_REPLICATOR) return false;
		
		if (world.isRemote) return true;

		TileEntityBiomeReplicator te = (TileEntityBiomeReplicator) world.getBlockTileEntity(x, y, z);

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
			TileEntityBiomeReplicator te = (TileEntityBiomeReplicator) world.getBlockTileEntity(x, y, z);
	
			if (te != null)
				BlockHelper.dropContents(te, world, x, y, z);
		}

		super.onBlockPreDestroy(world, x, y, z, metadata);
	}
}

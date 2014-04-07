package nf.fr.ephys.playerproxies.common.block;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.turtle.api.TurtleAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import nf.fr.ephys.playerproxies.common.item.MultitemBlock;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeReplicator;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;

public class BlockBaseShineyGlass extends BlockContainer {
	public static int BLOCK_ID = 804;

	public static final int METADATA_ETHEREAL_GLASS = 0;
	public static final int METADATA_INTERFACE = 1;

	public static final String[] blockNames = {
		"Shiney Glass", "Universal Interface"
	};

	public static void register() {
		PlayerProxies.blockBaseShineyGlass = new BlockBaseShineyGlass(BlockBaseShineyGlass.BLOCK_ID, Material.glass);
		PlayerProxies.blockBaseShineyGlass.setUnlocalizedName("PP_ShineyGlass");
		
		GameRegistry.registerBlock(PlayerProxies.blockBaseShineyGlass, MultitemBlock.class, "PP_ShineyGlass", PlayerProxies.modid);
		GameRegistry.registerTileEntity(TileEntityInterface.class, "PP_Interface");

		for (int metadata = 0; metadata < blockNames.length; metadata++) {
			ItemStack stackMultiBlock = new ItemStack(PlayerProxies.blockBaseShineyGlass, 1, metadata);

			LanguageRegistry.addName(stackMultiBlock, blockNames[metadata]);
			LanguageRegistry.instance().addStringLocalization("PP_ShineyGlass." + metadata, "EN_US", blockNames[metadata]);
		}
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int unknown, CreativeTabs tab, List subItems) {
		for (int i = 0; i < blockNames.length; i++) {
			subItems.add(new ItemStack(this, 1, i));
		}
	}
	
	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockBaseShineyGlass, 12, METADATA_ETHEREAL_GLASS),
				"ggg", "gdg", "ggg", 
				'd', new ItemStack(Item.diamond), 
				'g', new ItemStack(Block.glass));
		
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockBaseShineyGlass, 1, METADATA_INTERFACE),
				"dld", "geg", "dgd", 
				'd', new ItemStack(Item.diamond), 
				'l', new ItemStack(PlayerProxies.itemLinkFocus), 
				'g', new ItemStack(PlayerProxies.blockBaseShineyGlass, METADATA_ETHEREAL_GLASS), 
				'e', new ItemStack(Block.enderChest));
	}
	
	public BlockBaseShineyGlass(int id, Material material) {
		super(id, material);
		this.setStepSound(Block.soundGlassFootstep);
		setLightValue(1.0F);
		setHardness(1.0F);
		
		setCreativeTab(PlayerProxies.creativeTab);
		setTextureName("ephys.pp:pureGlass");
	}
	
	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
    public int getRenderBlockPass() {
        return 1;
    }
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitVectX, float hitVectY, float hitVectZ) {
		int metadata = world.getBlockMetadata(x, y, z);
		
		switch (metadata) {
			case METADATA_INTERFACE:		
				if (player.getHeldItem() == null) {
					if (!world.isRemote)
						((TileEntityInterface) world.getBlockTileEntity(x, y, z)).toggleLinked(player);
		
					world.markBlockForUpdate(x, y, z);
		
					return true;
				}
			default: return false;
		}
	}
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return metadata == METADATA_INTERFACE;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		switch (metadata) {
			case METADATA_INTERFACE:	return new TileEntityInterface();
			default: 					return null;
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}
}

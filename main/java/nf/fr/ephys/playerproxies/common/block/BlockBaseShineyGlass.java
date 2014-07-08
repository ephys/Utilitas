package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.item.MultitemBlock;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;

import java.util.List;

public class BlockBaseShineyGlass extends BlockBreakable implements ITileEntityProvider {
	public static final int METADATA_ETHEREAL_GLASS = 0;
	public static final int METADATA_INTERFACE = 1;

	protected BlockBaseShineyGlass(String texture, Material material, boolean transparent) {
		super(texture, material, transparent);

		this.isBlockContainer = true;
	}

	public static void register() {
		PlayerProxies.Blocks.baseShineyGlass = (BlockBaseShineyGlass) (new BlockBaseShineyGlass("ephys.pp:pureGlass", Material.glass, false)).setBlockName("PP_ShineyGlass").setStepSound(Block.soundTypeGlass).setLightLevel(1.0F).setHardness(1.0F).setCreativeTab(PlayerProxies.creativeTab).setBlockTextureName("ephys.pp:pureGlass");

		GameRegistry.registerBlock(PlayerProxies.Blocks.baseShineyGlass, MultitemBlock.class, PlayerProxies.Blocks.baseShineyGlass.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityInterface.class, "PP_Interface");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item unknown, CreativeTabs tab, List subItems) {
		subItems.add(new ItemStack(this, 1, METADATA_ETHEREAL_GLASS));
		subItems.add(new ItemStack(this, 1, METADATA_INTERFACE));
	}

	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.baseShineyGlass, 12, METADATA_ETHEREAL_GLASS), "ggg", "gdg", "ggg", 'd', new ItemStack(Items.diamond), 'g', new ItemStack(Blocks.glass));

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.baseShineyGlass, 1, METADATA_INTERFACE), "dld", "geg", "dgd", 'd', new ItemStack(Items.diamond), 'l', new ItemStack(PlayerProxies.Items.linkFocus), 'g', new ItemStack(PlayerProxies.Blocks.baseShineyGlass, METADATA_ETHEREAL_GLASS), 'e', new ItemStack(Blocks.ender_chest));
	}

	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitVectX, float hitVectY, float hitVectZ) {
		int metadata = world.getBlockMetadata(x, y, z);

		switch (metadata) {
			case METADATA_INTERFACE:
				if (!world.isRemote) {
					((TileEntityInterface) world.getTileEntity(x, y, z)).link(player);
				}

				return true;
			default:
				return false;
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileEntity te = world.getTileEntity(x, y, z);

		if (te instanceof TileEntityInterface) {
			((TileEntityInterface) te).onBlockUpdate();
		}

		super.onNeighborBlockChange(world, x, y, z, block);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return metadata == METADATA_INTERFACE;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		switch (metadata) {
			case METADATA_INTERFACE:
				return new TileEntityInterface();
			default:
				return null;
		}
	}

	@Override
	public void breakBlock(World paramWorld, int paramInt1, int paramInt2, int paramInt3, Block paramBlock, int paramInt4) {
		super.breakBlock(paramWorld, paramInt1, paramInt2, paramInt3, paramBlock, paramInt4);
		paramWorld.removeTileEntity(paramInt1, paramInt2, paramInt3);
	}

	@Override
	public boolean onBlockEventReceived(World paramWorld, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
		super.onBlockEventReceived(paramWorld, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
		TileEntity localTileEntity = paramWorld.getTileEntity(paramInt1, paramInt2, paramInt3);

		return localTileEntity != null && localTileEntity.receiveClientEvent(paramInt4, paramInt5);
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}
}
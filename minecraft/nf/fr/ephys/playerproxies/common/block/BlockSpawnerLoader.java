package nf.fr.ephys.playerproxies.common.block;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.turtle.api.TurtleAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBlockInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntitySpawnerLoader;

public class BlockSpawnerLoader extends BlockContainer {
	public static int BLOCK_ID = 801;
	private Icon topIcon;
	private Icon sideIcon;

	public BlockSpawnerLoader() {
		super(BlockSpawnerLoader.BLOCK_ID, Material.iron);

		setHardness(2.5F);
		setResistance(1000.0F);
		setCreativeTab(CreativeTabs.tabDecorations);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntitySpawnerLoader();
	}

	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase entity, ItemStack par6ItemStack) {
		if (entity instanceof EntityPlayer) {
			TileEntitySpawnerLoader spawnerLoader = (TileEntitySpawnerLoader) world
					.getBlockTileEntity(x, y, z);
		}
	}

	@Override
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int x, int y,
			int z, int side) {
		return side == 1 ? this.topIcon : this.sideIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister register) {
		topIcon = register.registerIcon("ephys.pp:ghostStabilizer");
		sideIcon = register.registerIcon("ephys.pp:hardenedStone");
	}

	@Override
	public Icon getIcon(int side, int metadata) {
		return side == 1 ? this.topIcon : this.sideIcon;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
	}

	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
	}

	/**
	 * Adds all intersecting collision boxes to a list. (Be sure to only add
	 * boxes to the list if they intersect the mask.) Parameters: World, X, Y,
	 * Z, mask, list, colliding entity
	 */
	public void addCollisionBoxesToList(World par1World, int par2, int par3,
			int par4, AxisAlignedBB par5AxisAlignedBB, List par6List,
			Entity par7Entity) {
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		super.addCollisionBoxesToList(par1World, par2, par3, par4,
				par5AxisAlignedBB, par6List, par7Entity);
	}
}

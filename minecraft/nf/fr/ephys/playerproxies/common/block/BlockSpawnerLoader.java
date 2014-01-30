package nf.fr.ephys.playerproxies.common.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.turtle.api.TurtleAPI;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TESpawnerLoader;

public class BlockSpawnerLoader extends BlockContainer {
	public static int blockID = 801;
	private Icon topIcon;
	private Icon sideIcon;

	public BlockSpawnerLoader() {
		super(BlockSpawnerLoader.blockID, Material.iron);

		setHardness(2.5F);
		setResistance(1000.0F);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TESpawnerLoader();
	}

	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase entity, ItemStack par6ItemStack) {
		if (entity instanceof EntityPlayer) {
			TESpawnerLoader spawnerLoader = (TESpawnerLoader) world.getBlockTileEntity(x, y, z);
		}
	}

	@Override
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int x, int y, int z, int side) {
		return side == 1 ? this.topIcon : this.sideIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister register) {
		topIcon = register.registerIcon("ephys.pp:ghost_stabilizer_top");
		sideIcon = register.registerIcon("ephys.pp:ghost_stabilizer_side");
	}
	
	@Override
	public Icon getIcon(int side, int metadata) {
		return side == 1 ? this.topIcon : this.sideIcon;
	}
}

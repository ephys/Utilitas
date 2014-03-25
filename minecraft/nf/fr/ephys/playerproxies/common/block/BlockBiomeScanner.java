package nf.fr.ephys.playerproxies.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeReplicator;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;
import nf.fr.ephys.playerproxies.helpers.PlacementHelpers;

public class BlockBiomeScanner extends BlockContainer {
	public static int blockID = 807;
	
	private Icon iconTop;
	private Icon iconSide;
	private Icon iconBottom;
	private Icon iconScreen;
	
	public BlockBiomeScanner() {
		super(blockID, Material.wood);
	}
	
	@Override
	public Icon getIcon(int side, int metadata) {
		switch(side) {
			case 0: return iconBottom;
			case 1: return iconTop;
			default:
				if (metadata == 0) return side == 2 || side == 4 ? iconScreen : iconSide;
				
				return side == metadata ? iconScreen : iconSide;
		}
	}

	@Override
	public void registerIcons(IconRegister register) {
		iconTop    = register.registerIcon("ephys.pp:biomeScannerTop");
		iconBottom = register.registerIcon("ephys.pp:biomeChangerBottom");
		iconSide   = register.registerIcon("ephys.pp:biomeScannerSide");
		iconScreen = register.registerIcon("ephys.pp:biomeScannerScreen");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityBiomeScanner();
	}
	
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
        par1World.setBlockMetadataWithNotify(par2, par3, par4, PlacementHelpers.orientationToMetadataXZ(par5EntityLivingBase.rotationYaw), 2);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (player.isSneaking()) return false;

		player.openGui(PlayerProxies.instance, PlayerProxies.GUI_BIOME_SCANNER, world, x, y, z);

		return true;
	}
}

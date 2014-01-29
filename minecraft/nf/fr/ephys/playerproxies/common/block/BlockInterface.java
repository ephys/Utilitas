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

public class BlockInterface extends BlockContainer {
	public static int blockID = 800;
	
	public BlockInterface() {
		super(BlockInterface.blockID, Material.glass);
		
		setLightValue(1.0F);
		setHardness(1.0F);
		setCreativeTab(CreativeTabs.tabDecorations);
		setTextureName("ephys.pp:universal_interface");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TEBlockInterface();
	}
	
	
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {}
    
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitVectX, float hitVectY, float hitVectZ) {
    	if(player.getHeldItem() == null) {
        	if(!world.isRemote)
        		((TEBlockInterface)world.getBlockTileEntity(x, y, z)).toggleLinked(player);
    		
    		world.markBlockForUpdate(x, y, z);
    		 
    		return true;
    	}
    	
    	return false;
    }
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
    public int getRenderBlockPass() {
        return 1;
    }
}

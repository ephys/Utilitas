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
	
	public BlockSpawnerLoader() {
		super(BlockSpawnerLoader.blockID, Material.glass);
		
		setLightValue(1.0F);
		setHardness(1.0F);
		setCreativeTab(CreativeTabs.tabDecorations);
		setTextureName("ephys.pp:universal_interface");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TESpawnerLoader();
	}
	
	
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack par6ItemStack) {
    	if(entity instanceof EntityPlayer) {
    		TESpawnerLoader spawnerLoader = (TESpawnerLoader) world.getBlockTileEntity(x, y, z);
    		
    		spawnerLoader.setOwner(((EntityPlayer) entity).username);
    	}
    }
    
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitVectX, float hitVectY, float hitVectZ) {
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

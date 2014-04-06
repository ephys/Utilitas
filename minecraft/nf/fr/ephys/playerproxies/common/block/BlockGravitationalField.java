package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;

public class BlockGravitationalField extends BlockContainer {
	public static int BLOCK_ID = 809;
	
	public static void register() {
		PlayerProxies.blockGravitationalField = new BlockGravitationalField();
		PlayerProxies.blockGravitationalField.setUnlocalizedName("PP_GravitationalField");
		GameRegistry.registerBlock(PlayerProxies.blockGravitationalField, "PP_GravitationalField");
		GameRegistry.registerTileEntity(TileEntityGravitationalField.class, "PP_GravitationalField");
		LanguageRegistry.instance().addName(PlayerProxies.blockGravitationalField, "Gravitational Field Handler");
	}
	
	public static void registerCraft() {
		System.err.println("WARNING GRAVITATIONAL FIELD CRAFT UNIMPLEMENTED");
	}
	
	public BlockGravitationalField() {
		super(BLOCK_ID, Material.iron);
		
		this.setCreativeTab(PlayerProxies.creativeTab);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityGravitationalField();
	}
	
    public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {
        if (world.isRemote) return;
        
        ((TileEntityGravitationalField)world.getBlockTileEntity(x, y, z)).checkPowered();
    }
    
    
}

package nf.fr.ephys.playerproxies.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;

public class BlockEnderminator extends BlockContainer {
	public static int BLOCK_ID = 810;
	
	public BlockEnderminator() {
		super(BLOCK_ID, Material.iron);
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

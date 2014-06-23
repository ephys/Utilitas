package nf.fr.ephys.playerproxies.common.block.uniterface;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import dan200.computercraft.api.turtle.ITurtleAccess;

public class InterfaceTurtle extends InterfaceTileEntity {
	private ITurtleAccess turtleAccess = null;
	
	public InterfaceTurtle(TileEntityInterface tileEntity) {
		super(tileEntity);
	}

	@Override
	public IFluidHandler getFluidHandler() {
		return null;
	}

	@Override
	public IInventory getInventory() {
		return blockEntity == null ? null : (IInventory) blockEntity;
	}

	@Override
	public void onTick() {
		if (turtleAccess == null) {
			TileEntity turtle = this.getTileEntity().getWorldObj().getBlockTileEntity(entityLocation[0], entityLocation[1], entityLocation[2]);
			
			if (turtle instanceof ITurtleAccess) {
				this.turtleAccess = (ITurtleAccess) turtle;
				
				ChunkCoordinates pos = turtleAccess.getPosition();
				blockEntity = turtleAccess.getWorld().getBlockTileEntity((int) pos.posX, (int) pos.posY, (int) pos.posZ);
			} else {
				this.getTileEntity().unlink();
				return;
			}
		}

		super.onTick();
	}
}

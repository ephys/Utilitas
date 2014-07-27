package nf.fr.ephys.playerproxies.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityFluidHopper;

public class ContainerFluidHopper extends Container {
	private EntityPlayer player;
	private TileEntityFluidHopper te;

	public ContainerFluidHopper(EntityPlayer player, TileEntityFluidHopper te) {
		super();

		this.player = player;
		this.te = te;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public FluidTankInfo[] getFluidStacks() {
		return te.getTankInfo(ForgeDirection.DOWN);
	}
}
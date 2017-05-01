package be.ephys.utilitas.feature.fluid_hopper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class ContainerFluidHopper extends Container {
    private TileEntityFluidHopper te;

    public ContainerFluidHopper(TileEntityFluidHopper te) {
        super();

        this.te = te;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    public IFluidTankProperties[] getFluidStacks() {
        IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);

        return fluidHandler.getTankProperties();
    }
}

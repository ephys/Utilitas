package be.ephys.utilitas.common.core;

import be.ephys.utilitas.common.Utilitas;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);

        if (ID == Utilitas.GUI_BIOME_SCANNER && tile instanceof TileEntityBiomeScanner) {
            return new ContainerBiomeScanner(player, (TileEntityBiomeScanner) tile);
        } else if (ID == PlayerProxies.GUI_FLUID_HOPPER && tile instanceof TileEntityFluidHopper) {
            return new ContainerFluidHopper(player, (TileEntityFluidHopper) tile);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (ID == PlayerProxies.GUI_BIOME_SCANNER && te instanceof TileEntityBiomeScanner)
            return new GuiBiomeScanner(new ContainerBiomeScanner(player, (TileEntityBiomeScanner) te));
        else if (ID == PlayerProxies.GUI_FLUID_HOPPER && te instanceof TileEntityFluidHopper)
            return new GuiFluidHopper(new ContainerFluidHopper(player, (TileEntityFluidHopper) te));

        return null;
    }
}

package nf.fr.ephys.playerproxies.common.core;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.client.gui.GuiBiomeScanner;
import nf.fr.ephys.playerproxies.client.gui.GuiFluidHopper;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.container.ContainerBiomeScanner;
import nf.fr.ephys.playerproxies.common.container.ContainerFluidHopper;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityFluidHopper;

public class GuiHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);

		if (ID == PlayerProxies.GUI_BIOME_SCANNER && te instanceof TileEntityBiomeScanner)
			return new ContainerBiomeScanner(player, (TileEntityBiomeScanner) te);
		else if (ID == PlayerProxies.GUI_FLUID_HOPPER && te instanceof TileEntityFluidHopper)
			return new ContainerFluidHopper(player, (TileEntityFluidHopper) te);

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

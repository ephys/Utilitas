package nf.fr.ephys.playerproxies.common.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.client.gui.GuiBiomeScanner;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.container.ContainerBiomeScanner;
import nf.fr.ephys.playerproxies.common.container.ContainerUniversalInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (ID == PlayerProxies.GUI_BIOME_SCANNER && te instanceof TileEntityBiomeScanner)
			return new ContainerBiomeScanner(player, (TileEntityBiomeScanner) te);

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (ID == PlayerProxies.GUI_BIOME_SCANNER && te instanceof TileEntityBiomeScanner)
			return new GuiBiomeScanner(new ContainerBiomeScanner(player, (TileEntityBiomeScanner) te));

		return null;
	}
}

package nf.fr.ephys.playerproxies.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import nf.fr.ephys.playerproxies.common.container.ContainerUniversalInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;

public class GuiUniversalInterface extends GuiContainer {
	public static int ID = 0;
	private ContainerUniversalInterface container;
	
	public GuiUniversalInterface(ContainerUniversalInterface container) {
		super(container);

		this.container = container;
	}


	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
	
	}
}

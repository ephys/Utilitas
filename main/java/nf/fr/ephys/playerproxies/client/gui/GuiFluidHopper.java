package nf.fr.ephys.playerproxies.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fluids.FluidTankInfo;
import nf.fr.ephys.playerproxies.common.container.ContainerFluidHopper;
import nf.fr.ephys.playerproxies.helpers.ChatHelper;

public class GuiFluidHopper extends GuiContainer {
	public GuiFluidHopper(ContainerFluidHopper containerBiomeScanner) {
		super(containerBiomeScanner);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		FluidTankInfo[] fluids = ((ContainerFluidHopper) inventorySlots).getFluidStacks();

		for (int i = 0; i < fluids.length; i++) {
			FluidTankInfo fluid = fluids[i];
			fontRendererObj.drawString(fluid == null ? "empty" : ChatHelper.getDisplayName(fluid.fluid) + " " + fluid.fluid.amount + " / " + fluid.capacity, 20, 20 + 20 * i, 0xFFFFFF);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {

	}
}

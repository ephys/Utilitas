package nf.fr.ephys.playerproxies.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fluids.FluidTankInfo;
import nf.fr.ephys.playerproxies.client.gui.util.GuiFluidTankInfo;
import nf.fr.ephys.playerproxies.common.container.ContainerFluidHopper;
import nf.fr.ephys.playerproxies.helpers.ChatHelper;

public class GuiFluidHopper extends GuiContainer {
	private GuiFluidTankInfo[] tanks;

	public GuiFluidHopper(ContainerFluidHopper containerBiomeScanner) {
		super(containerBiomeScanner);
	}

	@Override
	public void initGui() {
		super.initGui();

		int nbFluids = ((ContainerFluidHopper) inventorySlots).getFluidStacks().length;

		tanks = new GuiFluidTankInfo[nbFluids];

		for (int i = 0; i < nbFluids; i++) {
			tanks[i] = new GuiFluidTankInfo(5, 10 + 30 * i, 20, 120, GuiFluidTankInfo.RIGHT);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		FluidTankInfo[] fluids = ((ContainerFluidHopper) inventorySlots).getFluidStacks();

		for (int i = 0; i < fluids.length; i++) {
			FluidTankInfo fluid = fluids[i];
			tanks[i].draw(fluid);

			fontRendererObj.drawString(fluid == null ? "empty" : ChatHelper.getDisplayName(fluid.fluid), 30, 10 + 30 * i, 0xFFFFFF);

			if (fluid != null)
				fontRendererObj.drawString(fluid.fluid.amount + "mb / " + fluid.capacity + "mb", 30, 21 + 30 * i, 0xFFFFFF);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {

	}
}

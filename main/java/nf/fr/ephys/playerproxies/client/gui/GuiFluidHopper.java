package nf.fr.ephys.playerproxies.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidTankInfo;
import nf.fr.ephys.playerproxies.client.gui.util.GuiFluidTankInfo;
import nf.fr.ephys.playerproxies.common.container.ContainerFluidHopper;
import nf.fr.ephys.playerproxies.helpers.ChatHelper;
import org.lwjgl.opengl.GL11;

public class GuiFluidHopper extends GuiContainer {
	private GuiFluidTankInfo[] tanks;

	public GuiFluidHopper(ContainerFluidHopper containerBiomeScanner) {
		super(containerBiomeScanner);

		xSize = 250;
		ySize = 200;
	}

	@Override
	public void initGui() {
		super.initGui();

		int nbFluids = ((ContainerFluidHopper) inventorySlots).getFluidStacks().length;

		tanks = new GuiFluidTankInfo[nbFluids];

		for (int i = 0; i < nbFluids; i++) {
			tanks[i] = new GuiFluidTankInfo(10, 20 + 30 * i, 20, 120, GuiFluidTankInfo.RIGHT);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glPushMatrix();
			GL11.glTranslatef(guiLeft, guiTop, 0.0F);

			drawRect(-2, -2, xSize + 2, ySize + 2, 0x55212121);
			drawRect(0, 0, xSize, ySize, 0x55FFFFFF);

			drawRect(0, 0, 20, ySize, 0xFF212121);

			fontRendererObj.drawString(StatCollector.translateToLocal("tile.PP_FluidHopper.name"), 30, 5, 0xFFFFFF);

			FluidTankInfo[] fluids = ((ContainerFluidHopper) inventorySlots).getFluidStacks();

			for (int i = 0; i < fluids.length; i++) {
				FluidTankInfo fluid = fluids[i];
				tanks[i].draw(fluid, 0xFF000000);

				fontRendererObj.drawString(fluid == null ? StatCollector.translateToLocal("pp_messages.empty") : ChatHelper.getDisplayName(fluid.fluid), 35, 20 + 30 * i, 0xFFFFFF);

				if (fluid != null)
					fontRendererObj.drawString(fluid.fluid.amount + "mb / " + fluid.capacity + "mb", 35, 31 + 30 * i, 0xFFFFFF);
			}

			for (int i = ((ContainerFluidHopper) inventorySlots).getTileSlotCount() - 1; i < this.inventorySlots.inventorySlots.size(); ++i) {
				Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i);

				int xPos = slot.xDisplayPosition;
				int yPos = slot.yDisplayPosition;
				drawRect(xPos - 1, yPos - 1, xPos + 16 + 1, yPos + 16 + 1, 0xFF000000);
				drawRect(xPos, yPos, xPos + 16, yPos + 16, 0xFF999999);

				GL11.glColor4f(1, 1, 1, 1);
			}
		GL11.glPopMatrix();
	}
}

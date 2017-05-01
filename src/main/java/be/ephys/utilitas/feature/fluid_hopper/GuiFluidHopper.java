package be.ephys.utilitas.feature.fluid_hopper;

import be.ephys.utilitas.base.gui.GuiFluidTankInfo;
import be.ephys.utilitas.base.helpers.ChatHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.lwjgl.opengl.GL11;

public class GuiFluidHopper extends GuiContainer {

    private GuiFluidTankInfo[] tanks;

    public GuiFluidHopper(ContainerFluidHopper container) {
        super(container);

        xSize = 250;
        ySize = 200;
    }

    private ContainerFluidHopper container() {
        return (ContainerFluidHopper) this.inventorySlots;
    }

    @Override
    public void initGui() {
        super.initGui();

        int nbFluids = this.container().getFluidStacks().length;

        tanks = new GuiFluidTankInfo[nbFluids];

        for (int i = 0; i < nbFluids; i++) {
            tanks[i] = new GuiFluidTankInfo(10, 20 + 30 * i, 20, 120);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft, guiTop, 0.0F);

        drawRect(-2, -2, xSize + 2, ySize + 2, 0x55212121);
        drawRect(0, 0, xSize, ySize, 0x55FFFFFF);

        drawRect(0, 0, 20, ySize, 0xFF212121);

        fontRendererObj.drawString(I18n.format("fluid_hopper"), 30, 5, 0xFFFFFF);

        IFluidTankProperties[] tanks = this.container().getFluidStacks();

        for (int i = 0; i < tanks.length; i++) {
            IFluidTankProperties tank = tanks[i];
            FluidStack fluidStack = tank.getContents();
            this.tanks[i].draw(tank, 0xFF000000);

            fontRendererObj.drawString(tank == null ? I18n.format("fluid_hopper.state.empty") : ChatHelper.getDisplayName(fluidStack), 35, 20 + 30 * i, 0xFFFFFF);

            if (tank != null)
                fontRendererObj.drawString(fluidStack.amount + "mb / " + tank.getCapacity() + "mb", 35, 31 + 30 * i, 0xFFFFFF);
        }

        GL11.glPopMatrix();
    }
}

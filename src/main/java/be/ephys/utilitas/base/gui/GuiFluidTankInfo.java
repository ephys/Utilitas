package be.ephys.utilitas.base.gui;

import be.ephys.utilitas.base.helpers.RenderHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.lwjgl.opengl.GL11;

public class GuiFluidTankInfo extends Gui {
    private int xLeft;
    private int yTop;

    private int height;
    private int width;

    public GuiFluidTankInfo(int xLeft, int yTop, int height, int width) {
        this.xLeft = xLeft;
        this.yTop = yTop;

        this.height = height;
        this.width = width;
    }

    public GuiFluidTankInfo(int xLeft, int yTop) {
        this(xLeft, yTop, 16, 16);
    }

    public void draw(IFluidTankProperties tank, int backgroundColor) {
        drawRect(xLeft - 1, yTop - 1, xLeft + 16 + 1, yTop + 16 + 1, backgroundColor);
        GL11.glColor4f(1, 1, 1, 1);

        if (tank == null) {
            renderEmpty();
            return;
        }

        FluidStack contents = tank.getContents();
        if (contents == null) {
            renderEmpty();
            return;
        }

        // RenderHelper.loadBlockMap();

        TextureAtlasSprite fluidIcon = RenderHelper.getFluidTexture(contents);
        if (fluidIcon == null) {
            renderEmpty();
            return;
        }

        drawTexturedModalRect(xLeft, yTop, fluidIcon, width, height);
    }

    private void renderEmpty() {
        drawRect(xLeft, yTop, xLeft + width, yTop + height, 0xFF999999);
    }
}

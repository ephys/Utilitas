package nf.fr.ephys.playerproxies.client.gui.util;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidTankInfo;
import nf.fr.ephys.playerproxies.helpers.RenderHelper;
import org.lwjgl.opengl.GL11;

public class GuiFluidTankInfo extends Gui {
	private int xLeft;
	private int yTop;

	private int height;
	private int width;

	private int direction;

	public static final int RIGHT = 0;
	public static final int LEFT = 0;
	public static final int TOP = 0;
	public static final int BOTTOM = 0;

	public GuiFluidTankInfo(int xLeft, int yTop, int height, int width, int direction) {
		this.xLeft = xLeft;
		this.yTop = yTop;

		this.height = height;
		this.width = width;

		this.direction = direction;
	}

	public void draw(FluidTankInfo tank) {
		drawRect(xLeft - 1, yTop - 1, xLeft + 16 + 1, yTop + 16 + 1, 0xFF212121);

		GL11.glColor4f(1, 1, 1, 1);

		RenderHelper.loadBlockMap();

		if (tank != null && tank.fluid != null) {
			IIcon fluidIcon = RenderHelper.getFluidTexture(tank.fluid);

			drawTexturedModelRectFromIcon(xLeft, yTop, fluidIcon, 16, 16);
		}
	}
}

package nf.fr.ephys.playerproxies.client.registry;

import net.minecraft.client.Minecraft;
import nf.fr.ephys.cookiecore.helpers.MathHelper;

public class DragonColorRegistry {
	private static final float PRECISION = 50F;
	public static final int[] RED = new int[] { 180, 0, 0 };
	public static final int[] PURPLE = new int[] { 60, 0, 180 };

	private static int[] colors = new int[(int) Math.floor(Math.PI * PRECISION)];

	static {
		for (int i = 0; i < colors.length; i++) {
			float sin = (float) Math.sin(i / PRECISION);

			int r = MathHelper.gradientRGB(RED[0], PURPLE[0], sin) << 16;
			int g = MathHelper.gradientRGB(RED[1], PURPLE[1], sin) << 8;
			int b = MathHelper.gradientRGB(RED[2], PURPLE[2], sin);

			colors[i] = r | g | b;
		}
	}

	public static int getColor() {
		int index = (int) (Minecraft.getMinecraft().theWorld.getTotalWorldTime() % colors.length);
		return colors[index];
	}
}
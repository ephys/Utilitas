package nf.fr.ephys.playerproxies.helpers;

import java.awt.*;

public class MathHelper {
	public static Color gradient(Color colorA, Color colorB, float percent) {
		return new Color(
			Math.round((colorB.getRed() - colorA.getRed()) * percent + colorA.getRed()),
			Math.round((colorB.getGreen() - colorA.getGreen()) * percent + colorA.getGreen()),
			Math.round((colorB.getBlue() - colorA.getBlue()) * percent + colorA.getBlue())
		);
	}

	public static int[] toRGB(int hexColor) {
		// 0xRRGGBB

		return new int[] {
			hexColor >> 16,         // RR [>> GGBB]
			(hexColor >> 8) & 0xFF, // RRGG [>> BB], 00GG
			hexColor & 0xFF         // 0000BB
		};
	}
}

package nf.fr.ephys.playerproxies.helpers;

import java.awt.*;
import java.util.List;

public class MathHelper {
	public static Color gradient(Color colorA, Color colorB, float percent) {
		return new Color(
			Math.round((colorB.getRed() - colorA.getRed()) * percent + colorA.getRed()),
			Math.round((colorB.getGreen() - colorA.getGreen()) * percent + colorA.getGreen()),
			Math.round((colorB.getBlue() - colorA.getBlue()) * percent + colorA.getBlue())
		);
	}

	public static int gradientRGB(int colorA, int colorB, float percent) {
		return Math.round((colorB - colorA) * percent + colorA);
	}

	public static int[] toRGB(int hexColor) {
		// 0xRRGGBB

		return new int[] {
			hexColor >> 16,         // RR [>> GGBB]
			(hexColor >> 8) & 0xFF, // RRGG [>> BB], 00GG
			hexColor & 0xFF         // 0000BB
		};
	}

	public static Object getRandom(List list) {
		if (list.size() == 0) return null;

		Object o = null;
		do {
			o = list.get(net.minecraft.util.MathHelper.getRandomIntegerInRange(BlockHelper.random, 0, list.size() - 1));
		} while(o == null);

		return o;
	}
}

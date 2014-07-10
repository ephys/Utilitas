package nf.fr.ephys.playerproxies.helpers;

import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

public class DebugHelper {
	public static void sidedDebug(World world, String message) {
		PlayerProxies.getLogger().info("[" + (world.isRemote ? "CLIENT" : "SERVER") + "] " + message);
	}

	public static String arrayToString(Object[] array) {
		StringBuilder str = new StringBuilder();

		str.append('[');

		for (int i = 0; i < array.length; i++) {
			if (array[i] == null)
				str.append("null");
			else
				str.append(array[i].toString());

			if (i != array.length - 1)
				str.append(", ");
		}

		str.append(']');

		return str.toString();
	}
}

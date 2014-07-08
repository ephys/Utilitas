package nf.fr.ephys.playerproxies.helpers;

import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

public class DebugHelper {
	public static void sidedDebug(World world, String message) {
		PlayerProxies.getLogger().debug("[" + (world.isRemote ? "CLIENT" : "SERVER") + "] " + message);
	}
}

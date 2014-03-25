package nf.fr.ephys.playerproxies.helpers;

import net.minecraft.util.MathHelper;

public class PlacementHelpers {
	public static int orientationToMetadataXZ(double rotationYaw) {
	    int l = MathHelper.floor_double((double)(rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	    return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
	}
}
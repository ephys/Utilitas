package nf.fr.ephys.playerproxies.common.registry;

import net.minecraft.entity.Entity;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;

import java.util.Vector;

public class GravitationalFieldRegistry  {
	private static Vector<TileEntityGravitationalField> vector = new Vector<>();

	public static TileEntityGravitationalField getClosestGravitationalField(Entity player) {
		for (TileEntityGravitationalField field : vector) {
			if (player.worldObj.equals(field.getWorldObj())) {
				if (!field.inRange(player)) continue;

				if (field.isActive())
					return field;
			}
		}

		return null;
	}

	public static void remove(TileEntityGravitationalField te) {
		vector.remove(te);
	}

	public static void add(TileEntityGravitationalField te) {
		vector.add(te);
	}
}
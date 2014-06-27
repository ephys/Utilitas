package nf.fr.ephys.playerproxies.common.registry;

import java.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;

public class GravitationalFieldRegistry  {
	//private static Vector<int[]> vector = new Vector<int[]>();
	private static Vector<TileEntityGravitationalField> vector = new Vector<TileEntityGravitationalField>();

	public static TileEntityGravitationalField getClosestGravitationalField(Entity player) {
		//for (int[] pos : vector) {
		for (TileEntityGravitationalField field : vector) {
			//if (player.worldObj.blockHasTileEntity(pos[0], pos[1], pos[2])) {
			if (player.worldObj == field.worldObj) {
				if (!field.inRange(player)) continue;

				//TileEntity te = player.worldObj.getBlockTileEntity(pos[0], pos[1], pos[2]);

				//if (te instanceof TileEntityGravitationalField && ((TileEntityGravitationalField) te).isActive())
					//return (TileEntityGravitationalField) te;
				
				if (field.isActive())
					return field;
			}
		}

		return null;
	}

	public static void remove(TileEntityGravitationalField te) {
		//vector.remove(BlockHelper.getCoords(te));
		vector.remove(te);
	}

	public static void add(TileEntityGravitationalField te) {
		//vector.add(BlockHelper.getCoords(te));
		vector.add(te);
	}
	
	public static void clear() {
		
	}
}
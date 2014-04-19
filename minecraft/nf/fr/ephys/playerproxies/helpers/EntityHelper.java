package nf.fr.ephys.playerproxies.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.FakePlayer;

public class EntityHelper {
	public static final int ARMORSLOT_HELMET = 3;
	public static final int ARMORSLOT_CHEST = 2;
	public static final int ARMORSLOT_PANTS = 1;
	public static final int ARMORSLOT_BOOT = 0;
	
	public static boolean isFakePlayer(EntityPlayer player) {
		if (player == null) return false;
		
		if (player instanceof FakePlayer) return true;

		if (player.username == null || player.username.contains("[")) return true;

		if (player.getClass().toString().toLowerCase().contains("fake")) return true;

		return false;
	}
	
	public static MovingObjectPosition getPlayerMOP(EntityPlayer player, double range) {
		Vec3 pos = player.worldObj.getWorldVec3Pool().getVecFromPool(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3 look = player.getLookVec();
		Vec3 ray = pos.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);

		return player.worldObj.clip(pos, ray);
	}
}

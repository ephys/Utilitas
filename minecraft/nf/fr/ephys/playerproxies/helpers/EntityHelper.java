package nf.fr.ephys.playerproxies.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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

	/**
	 * This is an eavy method, don't call it too often. Really.
	 * Finds an entity by it's UUID (returns null if the entity does not exists or is not loaded)
	 * 
	 * @param entityUUID the entity UUID
	 * @return Entity 	 the entity
	 */
	public static Entity getEntityByUUID(UUID entityUUID) {
		for (WorldServer world : MinecraftServer.getServer().worldServers) {
			Entity entity = getEntityByUUIDInWorld(entityUUID, world);

			if (entity != null)
				return entity;
		}

		return null;
	}

	/**
	 * This is an eavy method, don't call it too often. Really.
	 * Finds an entity by it's UUID in a specific world (returns null if the entity does not exists or is not loaded)
	 * 
	 * @param entityUUID the entity UUID
	 * @param world		 the world to search in
	 * @return Entity 	 the entity
	 */
	public static Entity getEntityByUUIDInWorld(UUID uuid, World world) {
		List<Entity> entities = world.getLoadedEntityList();
		
		for (Entity entity : entities) {
			if (entity.getUniqueID().equals(uuid))
				return entity;
		}
		
		return null;
	}
}

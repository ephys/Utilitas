package nf.fr.ephys.playerproxies.client.core;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NicknamesRegistry {
	private static HashMap<UUID, String> map = new HashMap<>();

	public static void set(UUID userid, String nickname) {
		map.put(userid, nickname);

		EntityPlayer player = getPlayerClient(userid);

		if (player != null) {
			player.refreshDisplayName();
		}
	}

	public static String get(UUID name) {
		return map.get(name);
	}

	@SuppressWarnings("unchecked")
	public static EntityPlayer getPlayerClient(UUID userid) {
		List<EntityPlayer> players = Minecraft.getMinecraft().theWorld.playerEntities;

		for (EntityPlayer player : players) {
			if (player.getUniqueID().equals(userid)) {
				return player;
			}
		}

		return null;
	}
}
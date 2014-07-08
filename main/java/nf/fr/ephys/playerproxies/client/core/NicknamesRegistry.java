package nf.fr.ephys.playerproxies.client.core;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

public class NicknamesRegistry {
	private static HashMap<String, String> map = new HashMap<>();

	public static void set(String username, String nickname) {
		map.put(username, nickname);

		EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(username);

		if (player != null)
			player.refreshDisplayName();
	}

	public static String get(String name) {
		return map.get(name);
	}
}
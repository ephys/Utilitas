package nf.fr.ephys.playerproxies.client.core;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class NicknamesRegistry {
	private static HashMap<String, String> map = new HashMap<String, String>();
	
	public static void set(String realname, String nickname) {
		map.put(realname, nickname);
		
		// we're client side, we're allowed to use that
		EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(realname);
		
		// player is already logged in, let's update his nickname
		// otherwise, his nickname will be updated when he joins our client side world
		if (player != null) {
			player.refreshDisplayName();
		}
	}
	
	public static String get(String realname) {
		return map.get(realname);
	}
}

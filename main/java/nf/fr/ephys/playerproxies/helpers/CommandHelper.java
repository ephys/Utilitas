package nf.fr.ephys.playerproxies.helpers;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

public class CommandHelper {
	public static void sendChatMessage(ICommandSender user, String message) {
		if (user.getEntityWorld() == null || !user.getEntityWorld().isRemote)
			user.addChatMessage(new ChatComponentText(message));
	}

	private static final String[] sides = {"down", "up", "north", "south", "west", "east"};
	public static String blockSideName(int side) {
		return StatCollector.translateToLocal("side." + sides[side]);
	}
}
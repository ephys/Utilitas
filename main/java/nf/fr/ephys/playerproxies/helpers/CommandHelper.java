package nf.fr.ephys.playerproxies.helpers;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandHelper {
	public static void sendChatMessage(ICommandSender user, String message) {
		if (user.getEntityWorld() == null || !user.getEntityWorld().isRemote)
			user.addChatMessage(new ChatComponentText(message));
	}
}
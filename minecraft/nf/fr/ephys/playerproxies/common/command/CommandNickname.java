package nf.fr.ephys.playerproxies.common.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import nf.fr.ephys.playerproxies.common.core.PacketHandler;

public class CommandNickname extends CommandBase {
	@Override
	public String getCommandName() {
		return "nickname";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "nickname [playername] <nickname>";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		EntityPlayer target;
		String nickname;
		
		if (args.length == 1) {
			if (!(sender instanceof EntityPlayer)) {
				sender.sendChatToPlayer(ChatMessageComponent.createFromText("Specify a player."));
				return;
			}
			
			target = (EntityPlayer) sender;
			nickname = args[0];
		} else if (args.length == 2) {
			try {
				target = getPlayer(sender, args[0]);
			} catch(PlayerNotFoundException e) {
				sender.sendChatToPlayer(ChatMessageComponent.createFromText(args[0]+" is not connected."));
				
				return;
			}

			nickname = args[1];
		} else {
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("Usage: /"+getCommandUsage(sender)));
			return;
		}
		
		if (!nickname.matches("^[a-zA-Z0-9_]*$")) {
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid nickname: Alphanumerical characters and underscores only"));
		}

		target.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setString("nickname", nickname);
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromText(target.getEntityName()+" is now known as "+nickname));

		target.refreshDisplayName();
		PacketHandler.sendPacketSetNickname(target);
	}

	// TODO remove
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return false;
	}
}

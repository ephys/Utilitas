package nf.fr.ephys.playerproxies.common.command;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;

public class NicknameCommand implements ICommand {
	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "nickname";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "nickname <username>";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length != 1) {
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("Usage: /"+getCommandUsage(sender)));
			return;
		}
		
		if (!(sender instanceof EntityPlayer)) {
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("You must be a player to use this command"));
			return;
		}
		
		((EntityPlayer) sender).getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setString("nickname", args[0]);
		((EntityPlayer) sender).addChatMessage("New nickname : "+args[0]);
	}

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

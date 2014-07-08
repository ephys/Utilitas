package nf.fr.ephys.playerproxies.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import nf.fr.ephys.playerproxies.common.network.PacketSetNicknameHandler;
import nf.fr.ephys.playerproxies.helpers.CommandHelper;

import java.util.List;

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
				CommandHelper.sendChatMessage(sender, "Specify a player.");
				return;
			}

			target = (EntityPlayer) sender;
			nickname = args[0];
		} else if (args.length == 2) {
			try {
				target = getPlayer(sender, args[0]);
			} catch(PlayerNotFoundException e) {
				CommandHelper.sendChatMessage(sender, args[0]+" is not connected.");

				return;
			}

			nickname = args[1];
		} else {
			CommandHelper.sendChatMessage(sender, "Usage: /"+getCommandUsage(sender));

			return;
		}

		if (!nickname.matches("^[a-zA-Z0-9_]*$")) {
			CommandHelper.sendChatMessage(sender, "Invalid nickname: Alphanumerical characters and underscores only");

			return;
		}

		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(target.getDisplayName()+" is now known as "+nickname));

		NBTTagCompound playerNBT = target.getEntityData();

		if (!playerNBT.hasKey(EntityPlayer.PERSISTED_NBT_TAG))
			playerNBT.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());

		playerNBT.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setString("nickname", nickname);

		target.refreshDisplayName();

		if (!target.worldObj.isRemote)
			PacketSetNicknameHandler.sendNickToAll((EntityPlayerMP) target);
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

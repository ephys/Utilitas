package nf.fr.ephys.playerproxies.common.command;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import nf.fr.ephys.cookiecore.helpers.ChatHelper;
import nf.fr.ephys.playerproxies.client.core.NicknamesRegistry;
import nf.fr.ephys.playerproxies.common.network.PacketSetNicknameHandler;

import java.util.List;

public class CommandNickname extends CommandBase {
	public static boolean enabled = true;

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
				ChatHelper.sendChatMessage(sender, "Specify a player.");
				return;
			}

			target = (EntityPlayer) sender;
			nickname = args[0];
		} else if (args.length == 2) {
			try {
				target = getPlayer(sender, args[0]);
			} catch(PlayerNotFoundException e) {
				ChatHelper.sendChatMessage(sender, args[0] + " is not connected.");

				return;
			}

			nickname = args[1];
		} else {
			ChatHelper.sendChatMessage(sender, "Usage: /" + getCommandUsage(sender));

			return;
		}

		if (!nickname.matches("^[a-zA-Z0-9_]*$")) {
			ChatHelper.sendChatMessage(sender, "Invalid nickname: Alphanumerical characters and underscores only");

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

	public static class Events {
		@SubscribeEvent
		public void changePlayerName(PlayerEvent.NameFormat event) {
			if (!event.entity.worldObj.isRemote) {
				NBTTagCompound nbt = event.entityPlayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

				if (nbt.hasKey("nickname")) {
					event.displayname = nbt.getString("nickname");
				}
			} else {
				// clients have the list stored in a registry, as entities are volatile.
				String name = NicknamesRegistry.get(event.entityPlayer.getUniqueID());

				if (name != null) {
					event.displayname = name;
				}
			}
		}

		@SubscribeEvent
		public void onJoin(EntityJoinWorldEvent event) {
			if (event.entity instanceof EntityPlayerMP) {
				if (!event.world.isRemote) { // server side
					// send this player's nick to every other player
					PacketSetNicknameHandler.sendNickToAll((EntityPlayerMP) event.entity);

					// send this player every other player's nick
					PacketSetNicknameHandler.sendListToPlayer((EntityPlayerMP) event.entity);
				}
			}
		}
	}
}

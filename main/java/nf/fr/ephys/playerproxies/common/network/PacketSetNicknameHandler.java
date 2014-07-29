package nf.fr.ephys.playerproxies.common.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import nf.fr.ephys.cookiecore.util.SimpleMap;
import nf.fr.ephys.playerproxies.client.core.NicknamesRegistry;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

import java.util.Map;

public class PacketSetNicknameHandler implements IMessageHandler<PacketSetNicknameHandler.PacketSetNickname, IMessage> {
	public static void sendListToPlayer(EntityPlayerMP player) {
		PacketSetNickname packet = new PacketSetNickname(null);

		PlayerProxies.getNetHandler().sendTo(packet, player);
	}

	public static void sendNickToAll(EntityPlayerMP player) {
		PacketSetNickname packet = new PacketSetNickname(player);

		PlayerProxies.getNetHandler().sendToAll(packet);
	}

	@Override
	public IMessage onMessage(PacketSetNickname packet, MessageContext messageContext) {
		for (Map.Entry<String, String> nickname : packet.nicknames.entrySet()) {
			NicknamesRegistry.set(nickname.getKey(), nickname.getValue());
		}

		return null;
	}

	public static class PacketSetNickname implements IMessage {
		private SimpleMap<String, String> nicknames = new SimpleMap<>();

		public PacketSetNickname(EntityPlayer player) {
			if (player == null) {
				for (Object entity : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
					player = (EntityPlayer) entity;
					NBTTagCompound nbt = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

					if (nbt.hasKey("nickname")) {
						nicknames.put(player.getGameProfile().getName(), nbt.getString("nickname"));
					}
				}
			} else {
				NBTTagCompound nbt = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

				if (nbt.hasKey("nickname")) {
					nicknames.put(player.getGameProfile().getName(), nbt.getString("nickname"));
				}
			}
		}

		@Override
		public void fromBytes(ByteBuf byteBuf) {
			int size = byteBuf.readInt();

			for (int i = 0; i < size; i++) {
				nicknames.put(ByteBufUtils.readUTF8String(byteBuf), ByteBufUtils.readUTF8String(byteBuf));
			}
		}

		@Override
		public void toBytes(ByteBuf byteBuf) {
			byteBuf.writeInt(nicknames.size());

			for (Map.Entry<String, String> nickname : nicknames.entrySet()) {
				ByteBufUtils.writeUTF8String(byteBuf, nickname.getKey());
				ByteBufUtils.writeUTF8String(byteBuf, nickname.getValue());
			}
		}
	}
}
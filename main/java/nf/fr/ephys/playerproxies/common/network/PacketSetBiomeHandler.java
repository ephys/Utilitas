package nf.fr.ephys.playerproxies.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;

public class PacketSetBiomeHandler implements IMessageHandler<PacketSetBiomeHandler.PacketSetBiome, IMessage> {
	public static void setClientBiome(int x, int z, int biome, int dimention) {
		PacketSetBiome packet = new PacketSetBiome(x, z, biome);

		PlayerProxies.getNetHandler().sendToDimension(packet, dimention);
	}

	@Override
	public IMessage onMessage(PacketSetBiome packet, MessageContext messageContext) {
		BlockHelper.setBiome(Minecraft.getMinecraft().theWorld, packet.x, packet.z, packet.biome);

		return null;
	}

	public static class PacketSetBiome implements IMessage {
		private int x;
		private int z;
		private int biome;

		public PacketSetBiome() {}

		public PacketSetBiome(int x, int z, int biome) {
			this.biome = biome;
			this.x = x;
			this.z = z;
		}

		@Override
		public void fromBytes(ByteBuf byteBuf) {
			x = byteBuf.readInt();
			z = byteBuf.readInt();

			biome = byteBuf.readByte() + 128;
		}

		@Override
		public void toBytes(ByteBuf byteBuf) {
			byteBuf.writeInt(x);
			byteBuf.writeInt(z);

			/* byte = -128 .. 127, biome = 0 - 255 */
			byteBuf.writeByte((byte) (biome - 128));
		}
	}
}

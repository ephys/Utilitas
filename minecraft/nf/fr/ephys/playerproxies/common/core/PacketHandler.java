package nf.fr.ephys.playerproxies.common.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import nf.fr.ephys.playerproxies.client.core.NicknamesRegistry;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.entity.Ghost;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.helpers.ParticleHelper;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketHandler implements IPacketHandler {
	private static enum subChannels {
		ENDER_TOGGLE,
		SPAWN_PARTICLE,
		CHANGE_BIOME,
		SET_NICKNAME
	}

	/*
	 * Type sizes:
	 * 	char : 2 byte 
	 * 	short : 2 bytes 
	 * 	int : 4 bytes 
	 * 	long : 8 bytes
	 * 
	 * 	float : 4 bytes 
	 * 	double : 8 bytes
	 */

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

		try {
			int subChannel = inputStream.readByte();

			if (subChannel == subChannels.ENDER_TOGGLE.ordinal())
				toggleInterfaceEnderMode(inputStream, ((EntityPlayer) player).worldObj);
			else if (subChannel == subChannels.SPAWN_PARTICLE.ordinal())
				spawnParticle(inputStream, ((EntityPlayer) player).worldObj);
			else if (subChannel == subChannels.CHANGE_BIOME.ordinal())
				changeBiome(inputStream, ((EntityPlayer) player).worldObj);
			else if (subChannel == subChannels.SET_NICKNAME.ordinal())
				updateNicknames(inputStream);
			else
				PlayerProxies.getLogger().severe("Packet manager received unknown subpacket id");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public static void sendPacketInterfaceToggle(int xCoord, int yCoord, int zCoord) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(21);
		DataOutputStream outputStream = new DataOutputStream(bos);

		try {
			outputStream.writeByte(subChannels.ENDER_TOGGLE.ordinal());
			outputStream.writeInt(xCoord);
			outputStream.writeInt(yCoord);
			outputStream.writeInt(zCoord);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();

		packet.channel = "PlayerProxies";
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		PacketDispatcher.sendPacketToServer(packet);
	}

	private void toggleInterfaceEnderMode(DataInputStream inputStream, World world) {
		try {
			int xCoord = inputStream.readInt();
			int yCoord = inputStream.readInt();
			int zCoord = inputStream.readInt();

			TileEntity te = world.getBlockTileEntity(xCoord, yCoord, zCoord);

			if (te instanceof TileEntityInterface)
				((TileEntityInterface) te).toggleEnderMode();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public static void sendPacketSpawnParticle(int particleID, double x, double y, double z, double velX, double velY, double velZ, World world) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(50);
		DataOutputStream outputStream = new DataOutputStream(bos);

		try {
			outputStream.writeByte(subChannels.SPAWN_PARTICLE.ordinal());

			outputStream.writeByte(particleID);

			outputStream.writeDouble(x);
			outputStream.writeDouble(y);
			outputStream.writeDouble(z);
			
			outputStream.writeDouble(velX);
			outputStream.writeDouble(velY);
			outputStream.writeDouble(velZ);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();

		packet.channel = "PlayerProxies";
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		PacketDispatcher.sendPacketToAllAround(x, y, z, 32, world.provider.dimensionId, packet);
	}

	private void spawnParticle(DataInputStream stream, World world) {
		try {
			int particleID = stream.readByte();
			
			double x = stream.readDouble();
			double y = stream.readDouble();
			double z = stream.readDouble();
			
			double velX = stream.readDouble();
			double velY = stream.readDouble();
			double velZ = stream.readDouble();
			
			world.spawnParticle(ParticleHelper.getParticleNameFromID(particleID), x, y, z, velX, velY, velZ);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static void sendPacketChangeBiome(int x, int z, byte biome, World world) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
		DataOutputStream outputStream = new DataOutputStream(bos);

		try {
			outputStream.writeByte(subChannels.CHANGE_BIOME.ordinal());

			outputStream.writeByte(biome);

			outputStream.writeInt(x);
			outputStream.writeInt(z);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();

		packet.channel = "PlayerProxies";
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		PacketDispatcher.sendPacketToAllAround(x, 64, z, 128, world.provider.dimensionId, packet);
	}

	private void changeBiome(DataInputStream stream, World world) {
		try {
			byte biome = stream.readByte();

			int x = stream.readInt();
			int z = stream.readInt();

			Chunk chunk = world.getChunkFromBlockCoords(x, z);
			byte[] biomes = chunk.getBiomeArray();
			biomes[(z & 15) << 4 | (x & 15)] = biome;

			chunk.setBiomeArray(biomes);
			chunk.setChunkModified();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Sends a packet to every player containing the player's new nickname 
	 * @param entity
	 */
	public static void sendPacketSetNickname(EntityPlayer player) {
		NBTTagCompound nbt = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

		if (!nbt.hasKey("nickname")) return;

		String nickname = nbt.getString("nickname");
		String realname = player.getEntityName();

		ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
		DataOutputStream outputStream = new DataOutputStream(bos);

		try {
			outputStream.writeByte(subChannels.SET_NICKNAME.ordinal());

			outputStream.writeUTF(realname+":"+nickname);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();

		packet.channel = "PlayerProxies";
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		PacketDispatcher.sendPacketToAllPlayers(packet);
		
		System.out.println("SENDING "+player.username+"'s NICK PACKET TO ALL");
	}

	/**
	 * Sends a packet containing every online player's nickname to this player
	 * 
	 * @param player
	 */
	public static void sendPacketNicknames(EntityPlayer player) {
		Map<String, String> nicknames = new HashMap<String, String>();

		for (Object entity : MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).playerEntityList) {
			NBTTagCompound nbt = ((EntityPlayer) entity).getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		
			if (nbt.hasKey("nickname")) {
				nicknames.put(((EntityPlayer) entity).getEntityName(), nbt.getString("nickname"));
			}
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
		DataOutputStream outputStream = new DataOutputStream(bos);

		try {
			outputStream.writeByte(subChannels.SET_NICKNAME.ordinal());

			int i = 0;
			for (Map.Entry<String, String> set : nicknames.entrySet()) {
				outputStream.writeUTF(set.getKey()+":"+set.getValue());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();

		packet.channel = "PlayerProxies";
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		System.out.println("SENDING NICKS PACKET TO "+player.username);
		PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
	}
	
	private void dumpPacket(Packet250CustomPayload packet) {
		System.out.println("packet size: "+packet.length);
		System.out.println("packet data:");
		for (int i = 0; i < packet.data.length; i++) {
			System.out.print(Integer.toHexString(packet.data[i])+" ");
		}
		
		System.out.println();	
	}
	
	private void updateNicknames(DataInputStream stream) {
		System.out.println("RECEIVED PACKET");

		try {
			String[] names = stream.readUTF().split(":");

			NicknamesRegistry.set(names[0], names[1]);
		} catch (EOFException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

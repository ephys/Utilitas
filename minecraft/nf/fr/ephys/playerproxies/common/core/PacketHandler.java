package nf.fr.ephys.playerproxies.common.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
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
		CHANGE_BIOME
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
			else
				PlayerProxies.getLogger().severe("Packet manager received unknown subpacket id");
		} catch (IOException e) {
			e.printStackTrace();
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
		}
	}

	public static void sendPacketSpawnParticle(int particleID, double x, double y, double z, int velX, int velY, int velZ, World world) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(38);
		DataOutputStream outputStream = new DataOutputStream(bos);

		try {
			outputStream.writeByte(subChannels.SPAWN_PARTICLE.ordinal());

			outputStream.writeByte(particleID);

			outputStream.writeDouble(x);
			outputStream.writeDouble(y);
			outputStream.writeDouble(z);
			
			outputStream.writeInt(velX);
			outputStream.writeInt(velY);
			outputStream.writeInt(velZ);
		} catch (Exception ex) {
			ex.printStackTrace();
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
			
			int velX = stream.readInt();
			int velY = stream.readInt();
			int velZ = stream.readInt();
			
			world.spawnParticle(ParticleHelper.getParticleNameFromID(particleID), x, y, z, velX, velY, velZ);
		} catch (IOException e) {
			e.printStackTrace();
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
		}
	}
}

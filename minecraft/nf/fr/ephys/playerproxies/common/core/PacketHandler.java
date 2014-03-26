package nf.fr.ephys.playerproxies.common.core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.entity.Ghost;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

		if (packet.channel.equals("PP_enderToggle")) {
			toggleInterfaceEnderMode(inputStream, ((EntityPlayer)player).worldObj);
		}
	}

	private void toggleInterfaceEnderMode(DataInputStream inputStream, World world) {
		try {
			int xCoord = inputStream.readInt();
			int yCoord = inputStream.readInt();
			int zCoord = inputStream.readInt();
			
			TileEntity te = world.getBlockTileEntity(xCoord, yCoord, zCoord);
			
			if(te instanceof TileEntityInterface)
				((TileEntityInterface) te).toggleEnderMode();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

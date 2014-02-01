package nf.fr.ephys.playerproxies.common.core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if (packet.channel.equals("PP_enderToggle")) {
			DataInputStream inputStream = new DataInputStream(
					new ByteArrayInputStream(packet.data));

			try {
				int xCoord = inputStream.readInt();
				int yCoord = inputStream.readInt();
				int zCoord = inputStream.readInt();
				
				TileEntity te = ((EntityPlayer) player).worldObj.getBlockTileEntity(xCoord, yCoord, zCoord);
				if(te instanceof TEBlockInterface)
					((TEBlockInterface) te).toggleEnderMode();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

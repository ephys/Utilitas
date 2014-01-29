package nf.fr.ephys.playerproxies.common.core;

import java.net.SocketAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet0KeepAlive;
import net.minecraft.network.packet.Packet101CloseWindow;
import net.minecraft.network.packet.Packet102WindowClick;
import net.minecraft.network.packet.Packet106Transaction;
import net.minecraft.network.packet.Packet107CreativeSetSlot;
import net.minecraft.network.packet.Packet108EnchantItem;
import net.minecraft.network.packet.Packet10Flying;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet14BlockDig;
import net.minecraft.network.packet.Packet15Place;
import net.minecraft.network.packet.Packet16BlockItemSwitch;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet19EntityAction;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet203AutoComplete;
import net.minecraft.network.packet.Packet204ClientInfo;
import net.minecraft.network.packet.Packet205ClientCommand;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet255KickDisconnect;
import net.minecraft.network.packet.Packet27PlayerInput;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet7UseEntity;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.server.MinecraftServer;
 
public class NetServerHandlerFake extends NetServerHandler
{
        public NetServerHandlerFake(MinecraftServer par1MinecraftServer, EntityPlayerMP par3EntityPlayerMP)
        {
                super(par1MinecraftServer, new NetworkManagerFake(), par3EntityPlayerMP);
        }
 
        public void kickPlayerFromServer(String par1Str)
        {
        }
 
        public void func_110774_a(Packet27PlayerInput par1Packet27PlayerInput)
        {
        }
 
        public void handleFlying(Packet10Flying par1Packet10Flying)
        {
        }
 
        public void setPlayerLocation(double par1, double par3, double par5, float par7, float par8)
        {
        }
 
        public void handleBlockDig(Packet14BlockDig par1Packet14BlockDig)
        {
        }
 
        public void handlePlace(Packet15Place par1Packet15Place)
        {
        }
 
        public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj)
        {
        }
 
        public void unexpectedPacket(Packet par1Packet)
        {
        }
 
        public void sendPacketToPlayer(Packet par1Packet)
        {
        }
 
        public void handleBlockItemSwitch(Packet16BlockItemSwitch par1Packet16BlockItemSwitch)
        {
        }
 
        public void handleChat(Packet3Chat par1Packet3Chat)
        {
        }
 
        private void handleSlashCommand(String par1Str)
        {
        }
 
        public void handleAnimation(Packet18Animation par1Packet18Animation)
        {
        }
 
        public void handleEntityAction(Packet19EntityAction par1Packet19EntityAction)
        {
        }
 
        public void handleKickDisconnect(Packet255KickDisconnect par1Packet255KickDisconnect)
        {
        }
 
        public int packetSize()
        {
                return this.netManager.packetSize();
        }
 
        public void handleUseEntity(Packet7UseEntity par1Packet7UseEntity)
        {
        }
 
        public void handleClientCommand(Packet205ClientCommand par1Packet205ClientCommand)
        {
        }
 
        public boolean canProcessPacketsAsync()
        {
                return true;
        }
 
        public void handleRespawn(Packet9Respawn par1Packet9Respawn)
        {
        }
 
        public void handleCloseWindow(Packet101CloseWindow par1Packet101CloseWindow)
        {
        }
 
        public void handleWindowClick(Packet102WindowClick par1Packet102WindowClick)
        {
        }
 
        public void handleEnchantItem(Packet108EnchantItem par1Packet108EnchantItem)
        {
        }
 
        public void handleCreativeSetSlot(Packet107CreativeSetSlot par1Packet107CreativeSetSlot)
        {
        }
 
        public void handleTransaction(Packet106Transaction par1Packet106Transaction)
        {
        }
 
        public void handleUpdateSign(Packet130UpdateSign par1Packet130UpdateSign)
        {
        }
 
        public void handleKeepAlive(Packet0KeepAlive par1Packet0KeepAlive)
        {
        }
 
        public boolean isServerHandler()
        {
                return true;
        }
 
        public void handlePlayerAbilities(Packet202PlayerAbilities par1Packet202PlayerAbilities)
        {
        }
 
        public void handleAutoComplete(Packet203AutoComplete par1Packet203AutoComplete)
        {
        }
 
        public void handleClientInfo(Packet204ClientInfo par1Packet204ClientInfo)
        {
        }
 
        public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload)
        {
        }
 
        public void handleVanilla250Packet(Packet250CustomPayload par1Packet250CustomPayload)
        {
        }
 
        public boolean isConnectionClosed()
        {
                return true;
        }
 
        public void handleMapData(Packet131MapData par1Packet131MapData)
        {
        }
 
        public static class NetworkManagerFake
                implements INetworkManager
        {
                public void setNetHandler(NetHandler nethandler)
                {
                }
 
                public void addToSendQueue(Packet packet)
                {
                }
 
                public void wakeThreads()
                {
                }
 
                public void processReadPackets()
                {
                }
 
                public SocketAddress getSocketAddress()
                {
                        return null;
                }
 
                public void serverShutdown()
                {
                }
 
                public int packetSize()
                {
                        return 0;
                }
 
                public void networkShutdown(String s, Object[] var2)
                {
                }
 
                public void closeConnections()
                {
                }
        }
}
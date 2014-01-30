package nf.fr.ephys.playerproxies.common.entity;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet204ClientInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.FakePlayer;
import nf.fr.ephys.playerproxies.common.core.NetServerHandlerFake;

import net.minecraft.client.entity.AbstractClientPlayer;

public class Ghost extends FakePlayer {
	private EntityPlayer linkedPlayer = null;
	
	public Ghost(World world, String username) {
		super(world, username);

		this.playerNetServerHandler = new NetServerHandlerFake(FMLCommonHandler
				.instance().getMinecraftServerInstance(), this);
		setInvisible(true);
	}
	
	public void setLinkedPlayed(EntityPlayer player) {
		this.linkedPlayer = player;
	}

	public EntityPlayer getLinkedPlayer() {
		return linkedPlayer;
	}
	
	public ResourceLocation getLocationSkin() {
		if(linkedPlayer instanceof AbstractClientPlayer)
			return ((AbstractClientPlayer) linkedPlayer).getLocationSkin();
		
		return null;
	}
	
	@Override
	public boolean isInvisible() {
		return false;
	}

	public void sendChatToPlayer(String s) {
	}

	public boolean canCommandSenderUseCommand(int i, String s) {
		return false;
	}

	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(MathHelper.floor_double(this.posX),
				MathHelper.floor_double(this.posY + 0.5D),
				MathHelper.floor_double(this.posZ));
	}

	@Override
	public boolean isEntityInvulnerable() {
		return true;
	}

	@Override
	public void onDeath(DamageSource source) {
		return;
	}

	@Override
	public void onUpdate() {
		return;
	}
}

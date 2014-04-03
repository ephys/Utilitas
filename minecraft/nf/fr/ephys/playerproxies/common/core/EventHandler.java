package nf.fr.ephys.playerproxies.common.core;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;

public class EventHandler {
	@ForgeSubscribe
	public void onEntityDrop(LivingDropsEvent event) {
		if (event.entity.worldObj.isRemote) return;

		if ((event.entity instanceof EntityWither && event.source.getEntity() instanceof EntityPlayer && Math.random() < 0.25D*(1+event.lootingLevel))
				|| (event.entity instanceof EntityWitch && Math.random() < 0.05D*(1+event.lootingLevel))) {
			event.entity.dropItem(PlayerProxies.itemBiomeStorage.itemID, 1);
		}
	}
	
	@ForgeSubscribe
	public void onEntityDeath(LivingDeathEvent event) {
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;
			
			if (player.username.equals("GC_Darma")) {
				WorldServer worldserver = MinecraftServer.getServer().worldServers[0];
	            WorldInfo worldinfo = worldserver.getWorldInfo();
	            
	            worldinfo.setThundering(true);
			} else if (player.username.equals("Seyhial")) {
				if (event.source.getEntity() instanceof EntityLivingBase && Math.random() < 0.25) {
					EntityZombie zombie = new EntityZombie(player.worldObj);
					
					zombie.setCanPickUpLoot(false);
					zombie.setRotationYawHead(player.rotationYawHead);
					zombie.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
					zombie.setHealth(player.getMaxHealth());
					zombie.setCustomNameTag("Seyhial's carcass");
					zombie.setRevengeTarget((EntityLivingBase) event.source.getEntity());

					player.worldObj.spawnEntityInWorld(zombie);
				}
			}
		}
	}
	
	@ForgeSubscribe
	public void onChatMessage(ServerChatEvent event) {
		if (event.player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).hasKey("nickname")) {
			String username = event.player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getString("nickname");
			
			String message = "<"+username+"> " + event.message;

			event.component = ChatMessageComponent.createFromText(message);
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGH)
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (!(event.entityLiving instanceof EntityPlayer) || !event.entityLiving.worldObj.isRemote) return;
		
		TileEntityGravitationalField field = GravitationalFieldRegistry.getClosestGravitationalField(event.entityLiving);
		
		if (field == null) return;
		
		if (event.entityLiving.motionY < 0)
			event.entityLiving.motionY *= field.getGravityModifier();
		else
			event.entityLiving.motionY *= 2 - field.getGravityModifier();
	}
	
	@ForgeSubscribe(priority = EventPriority.HIGH)
	public void onLivingFall(LivingFallEvent event) {
		if (!(event.entityLiving instanceof EntityPlayer)) return;

		TileEntityGravitationalField field = GravitationalFieldRegistry.getClosestGravitationalField(event.entityLiving);

		if (field == null) return;

		event.distance *= field.getGravityModifier();
	}
}
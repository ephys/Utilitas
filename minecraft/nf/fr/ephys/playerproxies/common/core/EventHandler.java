package nf.fr.ephys.playerproxies.common.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import nf.fr.ephys.playerproxies.client.core.NicknamesRegistry;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.BlockHomeShield;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;
import nf.fr.ephys.playerproxies.helpers.ParticleHelper;

public class EventHandler {
	@ForgeSubscribe
	public void onEntityDrop(LivingDropsEvent event) {
		if (event.entity.worldObj.isRemote)
			return;

		if ((event.entity instanceof EntityWither
				&& event.source.getEntity() instanceof EntityPlayer 
				&& Math.random() < 0.25D * (1 + event.lootingLevel))
				|| (event.entity instanceof EntityWitch && Math.random() < 0.005D)) {
			event.entity.dropItem(PlayerProxies.Items.biomeStorage.itemID, 1);
		}
	}

	private static final int portalParticleID = ParticleHelper
			.getParticleIDFromName("portal");

	@ForgeSubscribe
	public void onAttack(LivingHurtEvent event) {
		if (event.entity instanceof EntityPlayer
				&& ((EntityPlayer) event.entity).username
						.equalsIgnoreCase("M_Bardin")) {
			Entity entity = event.entity;
			World world = entity.worldObj;

			int signe = 1;
			for (int i = 0; i < 5; i++) {
				double velx = world.rand.nextDouble() - 0.5;
				double vely = (world.rand.nextDouble() - 0.5) * signe;
				double velz = world.rand.nextDouble() - 0.5;

				signe *= -1;

				/*
				 * world.spawnParticle("portal", entity.posX + velx, entity.posY
				 * + vely, entity.posZ + velz, -velx, -vely, -velz );
				 */

				PacketHandler.sendPacketSpawnParticle(portalParticleID,
						entity.posX + velx, entity.posY + vely + 1.5,
						entity.posZ + velz, -velx, -vely, -velz, world);
			}
		}
	}

	// ========================== NICKNAME MANAGEMENT ==========================
	@ForgeSubscribe
	public void changePlayerName(PlayerEvent.NameFormat event) {
		NBTTagCompound nbt = event.entityPlayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

		if (nbt.hasKey("nickname")) {
			event.displayname = nbt.getString("nickname");
		} else {
			String name = NicknamesRegistry.get(event.entityPlayer.username);
			
			if (name != null)
				event.displayname = name;
		}
	}

	@ForgeSubscribe
	public void onJoin(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayer) {
			if (!event.world.isRemote) { // server side
				// send this player's nick to every other player
				PacketHandler.sendPacketSetNickname((EntityPlayer) event.entity);
				
				// send this player every other player's nick
				PacketHandler.sendPacketNicknames((EntityPlayer) event.entity);
			} else {
				((EntityPlayer) event.entity).refreshDisplayName();
			}
		}
	}

	/*@ForgeSubscribe
	public void hideUsername(RenderLivingEvent.Specials.Pre event) {
		if (event.entity instanceof EntityPlayer && event.isCancelable()) {
			NBTTagCompound nbt = event.entity.getEntityData().getCompoundTag(
					EntityPlayer.PERSISTED_NBT_TAG);

			if (nbt.hasKey("nickname"))
				event.setCanceled(true);
		}
	}

	@ForgeSubscribe
	public void renderNickname(RenderPlayerEvent.Post event) {
		String username = event.entityPlayer.getDisplayName();

		System.out.println(username);

		double y = event.entityPlayer.isPlayerSleeping() ? event.entity.posY - 1.5D : event.entity.posY;

		PP_RenderPlayerAccessor.renderLivingLabel(event.renderer, event.entityPlayer, username, 
				event.entity.posX, y, event.entity.posZ, 64);
	}*/
	
	// ========================== /NICKNAME MANAGEMENT ==========================

	@ForgeSubscribe(priority = EventPriority.HIGH)
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (!(event.entityLiving instanceof EntityPlayer))
			return;

		TileEntityGravitationalField field = GravitationalFieldRegistry
				.getClosestGravitationalField(event.entityLiving);

		if (field == null)
			return;

		if (event.entityLiving.motionY < 0) {
			if (event.entityLiving.worldObj.isRemote)
				event.entityLiving.motionY *= field.getGravityModifier();

			event.entityLiving.fallDistance /= 2 - field.getGravityModifier();
		} else if (event.entityLiving.worldObj.isRemote)
			event.entityLiving.motionY *= 2 - field.getGravityModifier();
	}

	private static int facingBlocks[] = { 1, 0, 3, 2, 5, 4 };

	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onBlockPlaced(PlayerInteractEvent event) {
		if (event.action != event.action.RIGHT_CLICK_BLOCK)
			return;

		ItemStack item = event.entityPlayer.getHeldItem();
		// ItemStack item = event.entityPlayer.getItemInUse();
		if (item == null)
			return;

		if ((PlayerProxies.Blocks.homeShield == null || item.itemID != PlayerProxies.Blocks.homeShield.blockID) 
				&& item.itemID != BlockHomeShield.twilightForestShieldID)
			return;

		int x = event.x;
		int y = event.y;
		int z = event.z;

		switch (event.face) { // get the position of the future placed block
		case 0:
			y--;
			break;
		case 1:
			y++;
			break;
		case 2:
			z--;
			break;
		case 3:
			z++;
			break;
		case 4:
			x--;
			break;
		case 5:
			x++;
			break;
		}

		int toPlaceMetadata = BlockPistonBase.determineOrientation(
				event.entityPlayer.worldObj, x, y, z, event.entityPlayer);
		if (toPlaceMetadata > 5)
			toPlaceMetadata -= 6;

		int facingBlockX = x;
		int facingBlockY = y;
		int facingBlockZ = z;

		switch (toPlaceMetadata) { // get the position of the block in front of
									// the breakable side
		case 0:
			facingBlockY--;
			break;
		case 1:
			facingBlockY++;
			break;
		case 2:
			facingBlockZ--;
			break;
		case 3:
			facingBlockZ++;
			break;
		case 4:
			facingBlockX--;
			break;
		case 5:
			facingBlockX++;
			break;
		}

		int facingBlockID = event.entityPlayer.worldObj.getBlockId(
				facingBlockX, facingBlockY, facingBlockZ);
		if (facingBlockID == PlayerProxies.Blocks.homeShield.blockID
				|| facingBlockID == BlockHomeShield.twilightForestShieldID) {
			int facingBlockMetadata = event.entityPlayer.worldObj
					.getBlockMetadata(facingBlockX, facingBlockY, facingBlockZ);

			int facingBlockSide = facingBlocks[toPlaceMetadata];

			if (BlockHomeShield.isSideBreakable(facingBlockSide,
					facingBlockMetadata)) {
				event.setResult(Result.DENY);
				event.setCanceled(true);
				event.entityPlayer
						.addChatMessage("Placing that block here would make it impossible to remove.");
			}
		} else if (Block.blocksList[facingBlockID] != null
				&& Block.blocksList[facingBlockID].blockHardness < 0) {
			event.setResult(Result.DENY);
			event.setCanceled(true);
			event.entityPlayer
					.addChatMessage("Placing that block here would make impossible to remove it.");
		}
	}
}
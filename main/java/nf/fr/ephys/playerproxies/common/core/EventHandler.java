package nf.fr.ephys.playerproxies.common.core;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.BlockHomeShield;
import nf.fr.ephys.playerproxies.common.registry.GravitationalFieldRegistry;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;
import nf.fr.ephys.playerproxies.helpers.ChatHelper;

import java.util.Random;

public class EventHandler {
	private Random random = new Random();

	@SubscribeEvent
	public void onEntityDrop(LivingDropsEvent event) {
		if (event.entity.worldObj.isRemote)
			return;

		if ((event.entity instanceof EntityWither
			&& event.source.getEntity() instanceof EntityPlayer
			&& Math.random() < 0.25D * (1 + event.lootingLevel))) {

			event.drops.add(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(PlayerProxies.Items.biomeStorage, 1)));

			//event.entity.dropItem(PlayerProxies.Items.biomeStorage, 1);
		} else if (event.entity instanceof EntityDragon) {
			int nbStacks = MathHelper.getRandomIntegerInRange(random, 1, 8);

			for (int i = 0; i < nbStacks; i++) {
				event.drops.add(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(PlayerProxies.Items.dragonScale, 16)));
			}
		} else if (event.entity.worldObj.provider.dimensionId == 1) {
			if (event.entity instanceof EntityEnderman) {
				if (event.source.getEntity() instanceof EntityCreeper)
					event.drops.add(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(PlayerProxies.Items.dragonScale, MathHelper.getRandomIntegerInRange(random, 5, 19))));
				else if (!(event.source.getEntity() instanceof EntityPlayer) && random.nextFloat() < 0.125F * event.lootingLevel) {
					event.drops.add(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(PlayerProxies.Items.dragonScale, MathHelper.getRandomIntegerInRange(random, 1, 1 + event.lootingLevel))));
				}
			}
		}
	}

	// ========================== /NICKNAME MANAGEMENT ==========================

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderParticles(RenderPlayerEvent.Post event) {
		EntityPlayer player = event.entityPlayer;
		if (!player.onGround && !player.worldObj.isDaytime() && player.getGameProfile().getName().equals("GC_Darma"))
			player.worldObj.spawnParticle("portal",
					player.posX + (random.nextDouble() - 0.5D) * (double) player.width,
					player.posY + random.nextDouble() * (double) player.height - 0.25D,
					player.posZ + (random.nextDouble() - 0.5D) * (double) player.width,
					(random.nextDouble() - 0.5D) * 2.0D,
					- random.nextDouble(),
					(random.nextDouble() - 0.5D) * 2.0D);
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (!(event.entityLiving instanceof EntityPlayer))
			return;

		TileEntityGravitationalField field = GravitationalFieldRegistry.getClosestGravitationalField(event.entityLiving);

		if (field == null)
			return;

		if (event.entityLiving.motionY < 0) {
			if (event.entityLiving.worldObj.isRemote)
				event.entityLiving.motionY *= field.getGravityModifier();

			event.entityLiving.fallDistance /= 2 - field.getGravityModifier();
		} else if (event.entityLiving.worldObj.isRemote)
			event.entityLiving.motionY *= 2 - field.getGravityModifier();
	}

	@SubscribeEvent
	public void enderPearlDamage(EnderTeleportEvent event) {
		if (PlayerProxies.getConfig().areEnderPearlsOP()) {
			event.attackDamage = 0;

			event.entity.worldObj.playSoundEffect(event.entity.posX, event.entity.posY, event.entity.posZ, "mob.endermen.portal", 1F, 1F);

			event.entity.worldObj.playSoundEffect(event.targetX, event.targetY, event.targetZ, "mob.endermen.portal", 1F, 1F);
		}
	}

	@SubscribeEvent
	public void onBlockPlaced(PlayerInteractEvent event) {
		if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
			return;

		ItemStack item = event.entityPlayer.getHeldItem();

		if (item == null)
			return;

		if (!item.getItem().equals(Item.getItemFromBlock(PlayerProxies.Blocks.homeShield)) && !item.getItem().equals(Item.getItemFromBlock(BlockHomeShield.tfShield)))
			return;

		int[] coords = BlockHelper.getAdjacentBlock(event.x, event.y, event.z, event.face);

		int toPlaceSide = BlockPistonBase.determineOrientation(event.entityPlayer.worldObj, coords[0], coords[1], coords[2], event.entityPlayer);

		if (BlockHomeShield.isUnbreakable(toPlaceSide))
			toPlaceSide -= 6;

		coords = BlockHelper.getAdjacentBlock(coords, toPlaceSide);

		Block facingBlock = event.entityPlayer.worldObj.getBlock(coords[0], coords[1], coords[2]);

		if (facingBlock.equals(PlayerProxies.Blocks.homeShield) || facingBlock.equals(BlockHomeShield.tfShield)) {
			int facingBlockMetadata = event.entityPlayer.worldObj.getBlockMetadata(coords[0], coords[1], coords[2]);

			int facingBlockSide = BlockHelper.getOppositeSide(toPlaceSide);

			if (BlockHomeShield.isSideBreakable(facingBlockSide, facingBlockMetadata)) {
				event.setResult(Event.Result.DENY);
				event.setCanceled(true);

				ChatHelper.sendChatMessage(event.entityPlayer, "Placing that block here would make it impossible to remove.");
			}
		} else if (BlockHelper.isUnbreakable(facingBlock, event.world, coords[0], coords[1], coords[2])) {
			event.setResult(Event.Result.DENY);
			event.setCanceled(true);
			ChatHelper.sendChatMessage(event.entityPlayer, "Placing that block here would make it impossible to remove.");
		}
	}
}
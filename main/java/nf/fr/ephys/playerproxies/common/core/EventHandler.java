package nf.fr.ephys.playerproxies.common.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.registry.GravitationalFieldRegistry;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;

import java.util.Random;

public class EventHandler {
	public static final Random random = new Random();

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
}
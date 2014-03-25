package nf.fr.ephys.playerproxies.common.core;

import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

public class EventHandler {
	@ForgeSubscribe
	public void onEntityDrop(LivingDropsEvent event) {
		if (event.entity.worldObj.isRemote) return;

		if ((event.entity instanceof EntityWither && event.source.getEntity() instanceof EntityPlayer && Math.random() < 0.25D*(1+event.lootingLevel))
				|| (event.entity instanceof EntityWitch && Math.random() < 0.05D*(1+event.lootingLevel))) {
			event.entity.dropItem(PlayerProxies.itemBiomeStorage.itemID, 1);
		}
	}
}
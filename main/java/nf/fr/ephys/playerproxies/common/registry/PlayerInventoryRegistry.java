package nf.fr.ephys.playerproxies.common.registry;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerInventoryRegistry {
	private static HashMap<UUID, FakePlayer> inventories = new HashMap<>();

	public static FakePlayer load(UUID uuid) {
		FakePlayer player = FakePlayerFactory.get(MinecraftServer.getServer().worldServerForDimension(0), new GameProfile(uuid, "PP_FakeInventory"));
		player.isDead = false;

		SaveHandler playerSave = (SaveHandler) MinecraftServer.getServer().getEntityWorld().getSaveHandler().getSaveHandler();
		playerSave.readPlayerData(player);

		inventories.put(uuid, player);

		return player;
	}

	public static void syncAndFlush(EntityPlayer player) {
		FakePlayer fake = inventories.get(player.getGameProfile().getId());

		if (fake == null) return;

		fake.setDead();

		NBTTagCompound fakeNBT = new NBTTagCompound();
		fake.writeToNBT(fakeNBT);
		player.readFromNBT(fakeNBT);

		inventories.remove(player.getGameProfile().getId());
	}

	public static EntityPlayer getFakePlayer(UUID uuid) {
		FakePlayer player = inventories.get(uuid);

		System.out.println(player);

		if (player == null)
			return load(uuid);

		return player;
	}

	@SubscribeEvent
	public void unloadPlayerNBT(EntityJoinWorldEvent event) {
		if (!event.world.isRemote && event.entity instanceof EntityPlayer) {
			syncAndFlush((EntityPlayer) event.entity);
		}
	}
}
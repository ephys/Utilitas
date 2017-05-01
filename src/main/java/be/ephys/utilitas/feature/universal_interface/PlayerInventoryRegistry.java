package be.ephys.utilitas.feature.universal_interface;

import be.ephys.utilitas.base.helpers.WorldHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerInventoryRegistry {

    private static HashMap<UUID, FakePlayer> inventories = new HashMap<>();

    public static FakePlayer load(UUID uuid) {

        FakePlayer player = FakePlayerFactory.get(WorldHelper.getServer().worldServerForDimension(0), new GameProfile(uuid, "Fake Inventory"));
        player.isDead = false;

        SaveHandler playerSave = (SaveHandler) WorldHelper.getServer().getEntityWorld().getSaveHandler();
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

        if (player == null)
            return load(uuid);

        return player;
    }

    protected static class EventHandler {

        @SubscribeEvent
        public void unloadPlayerNBT(EntityJoinWorldEvent event) {
            if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
                syncAndFlush((EntityPlayer) event.getEntity());
            }
        }
    }
}

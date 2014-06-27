package nf.fr.ephys.playerproxies.common.registry;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class PlayerInventoryRegistry {
	private static HashMap<String, IInventory> inventories = new HashMap<String, IInventory>();
	private static HashMap<String, IInventory> enderchests = new HashMap<String, IInventory>();
	
	public static void load(String username) {
		SaveHandler playerSave = (SaveHandler) MinecraftServer.getServer().getEntityWorld().getSaveHandler().getSaveHandler();

		playerSave.getPlayerData(username);
	}

	public static void save(String username) {
		//(SaveHandler) MinecraftServer.getServer().getEntityWorld().getSaveHandler().getSaveHandler().writePlayerData(entityplayer);
	}
	
	private static void save(String username, IInventory inventory, IInventory enderchest) {
		
	}
	
	public static void flush() {
		
	}

	public static IInventory getInventory(String username) {
		IInventory inventory = inventories.get(username);

		if (inventory == null) {
			EntityPlayer player = MinecraftServer.getServer().getServerConfigurationManager(MinecraftServer.getServer()).getPlayerForUsername(username);
			
			if (player == null) {
				load(username);
				
				return inventories.get(username);
			}
			
			inventories.put(player.username, player.inventory);
			inventories.put(player.username, player.getInventoryEnderChest());
		}

		return inventory;
	}

	public static IInventory getEnderchest(String username) {
		return null;
	}
}
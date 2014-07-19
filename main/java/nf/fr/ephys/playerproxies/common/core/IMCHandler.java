package nf.fr.ephys.playerproxies.common.core;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.BlockBaseShineyGlass;
import nf.fr.ephys.playerproxies.common.block.BlockHardenedStone;
import nf.fr.ephys.playerproxies.common.block.BlockToughwoodPlank;
import nf.fr.ephys.playerproxies.common.registry.BeaconEffectsRegistry;
import nf.fr.ephys.playerproxies.common.registry.UniversalInterfaceRegistry;
import nf.fr.ephys.playerproxies.common.registry.uniterface.UniversalInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBeaconTierII;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

import java.util.ArrayList;

public class IMCHandler {
	public static void sendMessages() {
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(PlayerProxies.Blocks.baseShineyGlass, 1, BlockBaseShineyGlass.METADATA_ETHEREAL_GLASS));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(PlayerProxies.Blocks.hardenedStone, 1, BlockHardenedStone.METADATA_HARDENED_STONE));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(PlayerProxies.Blocks.toughwoodPlank, 1, BlockToughwoodPlank.METADATA_TOUGHWOOD));
	}

	public static boolean handle(FMLInterModComms.IMCMessage message) {
		if (message.key.equalsIgnoreCase("add-beacon-recipe")) {
			if (!message.isNBTMessage()) {
				PlayerProxies.getLogger().warn("Wrong call to add-beacon-recipe from " + message.getSender() + ". Should be a NBTCompound");
				return false;
			}

			NBTTagCompound nbt = message.getNBTValue();

			if (!nbt.hasKey("effectID")) {
				PlayerProxies.getLogger().warn("Wrong call to add-beacon-recipe from " + message.getSender() + ". NBT is missing a 'effectID' tag (integer) [potion effect id]");
				return false;
			}

			int effect = nbt.getInteger("effectID");
			if (effect < 0 || effect >= Potion.potionTypes.length || Potion.potionTypes[effect] == null) {
				PlayerProxies.getLogger().warn("Wrong call to add-beacon-recipe from " + message.getSender() + ". Invalid effectID tag, effect does not exist in Potion.potionTypes[]");
				return false;
			}

			if (!nbt.hasKey("minLevel")) {
				PlayerProxies.getLogger().warn("Wrong call to add-beacon-recipe from " + message.getSender() + ". NBT is missing a 'minLevel' tag (integer) [minimum beacon level (0-" + TileEntityBeaconTierII.MAX_LEVELS + ")]");
				return false;
			}

			int minLevel = nbt.getInteger("minLevel");

			if (!nbt.hasKey("maxTier")) {
				PlayerProxies.getLogger().warn("Wrong call to add-beacon-recipe from " + message.getSender() + ". NBT is missing a 'maxTier' tag (integer) [maximum potion tier (-1 to remove limit, 0 - " + (TileEntityBeaconTierII.MAX_LEVELS - 1) + ")]");
				return false;
			}

			int maxTier = nbt.getInteger("maxTier");

			if (!nbt.hasKey("items")) {
				PlayerProxies.getLogger().warn("Wrong call to add-beacon-recipe from " + message.getSender() + ". NBT is missing a 'items' tag (NBTCompound) [itemstack list stored as NBT in the compound with a numerical key (0 -> itemCount - 1), max " + TileEntityBeaconTierII.MAX_ITEMS + " items]");
				return false;
			}

			NBTTagCompound itemNBT = nbt.getCompoundTag("items");

			ArrayList<ItemStack> stacks = new ArrayList<>(TileEntityBeaconTierII.MAX_ITEMS);
			for (int i = 0; i < TileEntityBeaconTierII.MAX_ITEMS; i++) {
				ItemStack stack = NBTHelper.getItemStack(itemNBT, Integer.toString(i));

				if (stack != null)
					stacks.add(stack);
			}

			ItemStack[] itemStacks = (ItemStack[]) stacks.toArray();

			BeaconEffectsRegistry.addEffect(itemStacks, effect, minLevel, maxTier); // items, potioneffect, minlevel, maxtier

			return true;
		} else if (message.key.equalsIgnoreCase("add-interface-handler")) {
			if (!message.isStringMessage()) {
				PlayerProxies.getLogger().warn("Wrong call to add-interface-handler from " + message.getSender() + ". Format is (string) 'handlerClass:targetClass'");
				return false;
			}

			String[] classes = message.getStringValue().split(":");
			if (classes.length != 2) {
				PlayerProxies.getLogger().warn("Wrong call to add-interface-handler from " + message.getSender() + ". Format is (string) 'handlerClass:targetClass', got " + message.getStringValue());
				return false;
			}

			try {
				Class handler = Class.forName(classes[0]);
				Class target = Class.forName(classes[1]);

				if (!UniversalInterface.class.isAssignableFrom(handler)) {
					PlayerProxies.getLogger().warn("Wrong call to add-interface-handler from " + message.getSender() + ". Handler should be an instance of " + UniversalInterface.class);
					return false;
				}

				UniversalInterfaceRegistry.addInterface((Class<? extends UniversalInterface>) handler, target);

				return true;
			} catch (ClassNotFoundException e) {
				PlayerProxies.getLogger().warn("Wrong call to add-interface-handler from " + message.getSender() + ". Could not find handler or target class");
				e.printStackTrace();

				return false;
			}
		}

		return false;
	}
}

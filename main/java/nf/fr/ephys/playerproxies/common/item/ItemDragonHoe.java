package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemHoe;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

public class ItemDragonHoe extends ItemHoe {
	public static void register() {
		PlayerProxies.Items.dragonHoe = new ItemDragonHoe();
		PlayerProxies.Items.dragonHoe.setUnlocalizedName("PP_DragonHoe")
				.setMaxStackSize(1)
				.setCreativeTab(PlayerProxies.creativeTab)
				.setHarvestLevel("pickaxe", 4);

		GameRegistry.registerItem(PlayerProxies.Items.dragonPickaxe, PlayerProxies.Items.dragonPickaxe.getUnlocalizedName());
	}

	public static void registerCraft() {

	}
}

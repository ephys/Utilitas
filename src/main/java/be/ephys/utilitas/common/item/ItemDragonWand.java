package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.registry.CauldronCraftsRegistry;

public class ItemDragonWand extends Item {
	public static void register() {
		PlayerProxies.Items.dragonWand = new ItemDragonWand();
		PlayerProxies.Items.dragonWand
				.setUnlocalizedName("PP_DragonWand").setCreativeTab(PlayerProxies.creativeTab);

		GameRegistry.registerItem(PlayerProxies.Items.dragonWand, PlayerProxies.Items.dragonWand.getUnlocalizedName());
	}

	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Items.dragonPickaxe),
				"  d", " s ", "s  ",
				'd', new ItemStack(PlayerProxies.Items.dragonScale),
				's', new ItemStack(Items.stick));
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return CauldronCraftsRegistry.attemptCrafting(world, x, y, z);
	}
}
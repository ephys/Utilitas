package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;

import java.util.List;

public class ItemPotionDiffuser extends Item {
	public static void register() {
		PlayerProxies.Items.potionDiffuser = new ItemPotionDiffuser();
		PlayerProxies.Items.potionDiffuser.setUnlocalizedName("PP_PotionDiffuser")
				.setMaxStackSize(1)
				.setCreativeTab(PlayerProxies.creativeTab)
				.setTextureName("ephys.pp:potionDiffuser");

		GameRegistry.registerItem(PlayerProxies.Items.potionDiffuser, PlayerProxies.Items.potionDiffuser.getUnlocalizedName());
	}

	public static void registerCraft() {
	/*	GameRegistry.addRecipe(new ItemStack(PlayerProxies.Items.linkDevice),
				" il", " si", "s  ",
				'l', new ItemStack(PlayerProxies.Items.linkFocus),
				'i', new ItemStack(Items.iron_ingot),
				's', new ItemStack(Items.stick));*/
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity owner, int slot, boolean isHeld) {
		if (owner.worldObj.getTotalWorldTime() % 10 != 0 || !(owner instanceof EntityPlayer)) return;

		EntityPlayer player = (EntityPlayer) owner;

		IInventory playerInventory = player.inventory;

		for (int i = 0; i < playerInventory.getSizeInventory(); i++) {
			ItemStack is = playerInventory.getStackInSlot(i);

			if (is == null || is.stackSize == 0) continue;

			if (is.getItem().equals(Items.potionitem)) {
				if (ItemPotion.isSplash(is.getItemDamage())) continue;
			} else {
				// todo: support for bloodmagic ?
				continue;
			}

			@SuppressWarnings("unchecked")
			List<PotionEffect> effects = Items.potionitem.getEffects(is);

			boolean shouldDrink = effects.size() > 0;
			for (PotionEffect effect : effects) {
				if (player.getActivePotionEffect(Potion.potionTypes[effect.getPotionID()]) != null) {
					shouldDrink = false;

					break;
				}
			}

			if (shouldDrink) {
				for (PotionEffect effect : effects) {
					player.addPotionEffect(new PotionEffect(effect));
				}

				playerInventory.setInventorySlotContents(i, null);
				BlockHelper.insertItem(playerInventory, new ItemStack(Items.glass_bottle));
			}
		}
	}
}

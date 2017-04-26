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
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

import java.util.List;

public class ItemPotionDiffuser extends Item {
	public static final int DAMAGE_INACTIVE = 0;
	public static final int DAMAGE_ACTIVE = 1;
	public static boolean enabled = true;

	public static void register() {
		if (!enabled) return;

		PlayerProxies.Items.potionDiffuser = new ItemPotionDiffuser();
		PlayerProxies.Items.potionDiffuser.setUnlocalizedName("PP_PotionDiffuser")
				.setMaxStackSize(1)
				.setCreativeTab(PlayerProxies.creativeTab)
				.setTextureName("ephys.pp:potionDiffuser");

		GameRegistry.registerItem(PlayerProxies.Items.potionDiffuser, PlayerProxies.Items.potionDiffuser.getUnlocalizedName());
	}

	public static void registerCraft() {
		if (!enabled) return;

		ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(new ItemStack(PlayerProxies.Items.potionDiffuser), 1, 1, 7));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer player, List data, boolean debug) {
		if (stack.getItemDamage() == DAMAGE_INACTIVE)
			data.add("Sneak and right click to activate");
		else
			data.add("Sneak and right click to deactivate");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (player.isSneaking()) {
			stack.setItemDamage(stack.getItemDamage() == DAMAGE_INACTIVE ? DAMAGE_ACTIVE : DAMAGE_INACTIVE);

			if (!world.isRemote)
				world.playSoundEffect(player.posX, player.posY, player.posZ, "random.orb", 0.8F, 1F);
		}

		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public boolean hasEffect(ItemStack stack, int pass) {
		return stack.getItemDamage() == DAMAGE_ACTIVE;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity owner, int slot, boolean isHeld) {
		if (stack.getItemDamage() == DAMAGE_INACTIVE || owner.worldObj.getTotalWorldTime() % 10 != 0 || !(owner instanceof EntityPlayer)) return;

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
				if (Potion.potionTypes[effect.getPotionID()].isInstant() || player.getActivePotionEffect(Potion.potionTypes[effect.getPotionID()]) != null) {
					shouldDrink = false;

					break;
				}
			}

			if (shouldDrink) {
				for (PotionEffect effect : effects) {
					player.addPotionEffect(new PotionEffect(effect.getPotionID(), effect.getDuration(), effect.getAmplifier(), true));
				}

				playerInventory.setInventorySlotContents(i, null);
				InventoryHelper.insertItem(playerInventory, new ItemStack(Items.glass_bottle));
			}
		}
	}
}

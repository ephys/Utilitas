package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

public class ItemDragonPickaxe extends ItemPickaxe {
	public static void register() {
		PlayerProxies.Items.dragonPickaxe = new ItemDragonPickaxe();
		PlayerProxies.Items.dragonPickaxe.setUnlocalizedName("PP_DragonPick")
				.setMaxStackSize(1)
				.setCreativeTab(PlayerProxies.creativeTab)
				.setTextureName("ephys.pp:dragonPick")
				.setHarvestLevel("pickaxe", 4);

		GameRegistry.registerItem(PlayerProxies.Items.dragonPickaxe, PlayerProxies.Items.dragonPickaxe.getUnlocalizedName());
	}

	public ItemDragonPickaxe() {
		super(PlayerProxies.Items.matDragonScale);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int par2) {
		return PlayerProxies.Items.dragonScale.getColorFromItemStack(stack, par2);
	}

	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return EnumRarity.uncommon;
	}

	@Override
	public boolean isItemTool(ItemStack par1ItemStack) {
		return true;
	}


}
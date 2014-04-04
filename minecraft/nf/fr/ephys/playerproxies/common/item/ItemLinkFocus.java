package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.entity.Ghost;

public class ItemLinkFocus extends Item {
	public static int ITEM_ID = 900;
	
	public static void register() {
		PlayerProxies.itemLinkFocus = new ItemLinkFocus();
		PlayerProxies.itemLinkFocus.setUnlocalizedName("PP_LinkFocus");
		GameRegistry.registerItem(PlayerProxies.itemLinkFocus, "PP_LinkFocus");
		LanguageRegistry.instance().addName(PlayerProxies.itemLinkFocus,
				"Link focus");
		
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.itemLinkFocus),
				"ipi", "qeq", "ipi", 
				'e', new ItemStack(Item.emerald), 
				'p', new ItemStack(Item.enderPearl), 
				'q', new ItemStack(Item.eyeOfEnder), 
				'i', new ItemStack(Item.blazePowder));
	}
	
	public ItemLinkFocus() {
		super(ITEM_ID);
		setMaxStackSize(64);
		setCreativeTab(CreativeTabs.tabMaterials);
		setTextureName("ephys.pp:linkFocus");
	}
}
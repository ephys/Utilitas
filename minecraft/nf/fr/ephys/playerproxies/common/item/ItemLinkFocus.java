package nf.fr.ephys.playerproxies.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.entity.Ghost;

public class ItemLinkFocus extends Item {
	public static int itemID = 900;
	
	public ItemLinkFocus() {
		super(itemID);
		setMaxStackSize(64);
		setCreativeTab(CreativeTabs.tabMaterials);
		setTextureName("ephys.pp:linkFocus");
	}
}

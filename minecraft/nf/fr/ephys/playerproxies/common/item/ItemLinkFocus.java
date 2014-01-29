package nf.fr.ephys.playerproxies.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemLinkFocus extends Item {
	public static int itemID = 900;
	
	public ItemLinkFocus() {
		super(itemID);
		setMaxStackSize(64);
		setCreativeTab(CreativeTabs.tabDecorations);
		setTextureName("ephys.pp:link_focus");
	}
}

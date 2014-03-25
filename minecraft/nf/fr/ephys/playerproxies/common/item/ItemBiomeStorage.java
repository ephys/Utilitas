package nf.fr.ephys.playerproxies.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeManager;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class ItemBiomeStorage extends Item {
	public static int itemID = 902;

	public ItemBiomeStorage() {
		super(itemID);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
		setTextureName("ephys.pp:itemBiomeStorage");
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		int biomeId = NBTHelper.getInt(stack, "", -1);

		list.add("Must be activated in a biome to duplicate it's signature.");
		
		if (biomeId != -1) {
			BiomeGenBase biome = BiomeGenBase.biomeList[biomeId];
			list.add("Signature: §5"+biome.biomeName);
		} else {
			list.add("Signature: §5Empty");
		}

		super.addInformation(stack, par2EntityPlayer, list, par4);
	}
}

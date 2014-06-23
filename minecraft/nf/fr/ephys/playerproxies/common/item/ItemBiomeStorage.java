package nf.fr.ephys.playerproxies.common.item;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
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
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class ItemBiomeStorage extends Item {
	public static int ITEM_ID = 902;
	
	public static void register() {
		PlayerProxies.Items.biomeStorage = new ItemBiomeStorage();
		PlayerProxies.Items.biomeStorage.setUnlocalizedName("PP_BiomeStorage");
		GameRegistry.registerItem(PlayerProxies.Items.biomeStorage, "PP_BiomeStorage");
		LanguageRegistry.instance().addName(PlayerProxies.Items.biomeStorage,
				"Biome signature handler");
	}

	public ItemBiomeStorage() {
		super(ITEM_ID);

		setMaxStackSize(1);
		setCreativeTab(PlayerProxies.creativeTab);

		setTextureName("ephys.pp:biomeStorage");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		NBTTagCompound nbt = NBTHelper.getNBT(stack);

		if (nbt.hasKey("biome")) {
			BiomeGenBase biome = BiomeGenBase.biomeList[nbt.getInteger("biome")];
			list.add("Signature: §5" + biome.biomeName);
		} else {
			list.add("Place this in a biome scanner to duplicate it's signature.");
		}

		super.addInformation(stack, par2EntityPlayer, list, par4);
	}
}

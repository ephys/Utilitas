package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

import java.util.List;

public class ItemBiomeStorage extends Item {
	public static void register() {
		PlayerProxies.Items.biomeStorage = new ItemBiomeStorage();
		PlayerProxies.Items.biomeStorage.setUnlocalizedName("PP_BiomeStorage")
				.setMaxStackSize(1)
				.setCreativeTab(PlayerProxies.creativeTab)
				.setTextureName("ephys.pp:biomeStorage");

		GameRegistry.registerItem(PlayerProxies.Items.biomeStorage, PlayerProxies.Items.biomeStorage.getUnlocalizedName());
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int par2) {
		NBTTagCompound nbt = NBTHelper.getNBT(stack);

		if (!nbt.hasKey("biome"))
			return super.getColorFromItemStack(stack, par2);

		BiomeGenBase biome = BiomeGenBase.getBiome(nbt.getInteger("biome"));

		if (biome == null) return super.getColorFromItemStack(stack, par2);

		return biome.color;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		NBTTagCompound nbt = NBTHelper.getNBT(stack);

		if (!nbt.hasKey("biome")) {
			list.add("Place this in a biome scanner to duplicate it's signature.");
			return;
		}

		int biomeID = nbt.getInteger("biome");
		BiomeGenBase biome = BiomeGenBase.getBiome(biomeID);

		if (biome == null) {
			list.add("CORRUPTED ITEM, PLEASE DESTROY");
		} else {
			list.add("Signature: §5" + biome.biomeName);
		}
	}
}
package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

import java.util.List;
import java.util.Random;

public class ItemBiomeStorage extends Item {
	private static CreativeTabs biomesTab = new CreativeTabs(PlayerProxies.MODID + ".biomes") {
		private ItemStack icon = new ItemStack(PlayerProxies.Items.biomeStorage, 1, MathHelper.getRandomIntegerInRange(new Random(), 0, 39));

		@Override
		public Item getTabIconItem() {
			return PlayerProxies.Items.biomeStorage;
		}

		@Override
		public ItemStack getIconItemStack() {
			return icon;
		}
	};

	public static void register() {
		PlayerProxies.Items.biomeStorage = new ItemBiomeStorage();
		PlayerProxies.Items.biomeStorage.setUnlocalizedName("PP_BiomeStorage")
				.setMaxStackSize(1)
				.setCreativeTab(biomesTab)
				.setTextureName("ephys.pp:biomeStorage")
				.setHasSubtypes(true);

		GameRegistry.registerItem(PlayerProxies.Items.biomeStorage, PlayerProxies.Items.biomeStorage.getUnlocalizedName());
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List subtypes) {
		ItemStack is = new ItemStack(item, 1, TileEntityBiomeScanner.NO_STORED_VALUE);
		subtypes.add(is);

		BiomeGenBase[] biomeList = BiomeGenBase.getBiomeGenArray();
		for (int i = 0; i < biomeList.length; i++) {
			if (biomeList[i] != null) {
				is = new ItemStack(item, 1, i);

				subtypes.add(is);
			}
		}
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		int biomeID = stack.getItemDamage();

		if (biomeID == TileEntityBiomeScanner.NO_STORED_VALUE) return super.getColorFromItemStack(stack, renderPass);

		BiomeGenBase biome = BiomeGenBase.getBiome(biomeID);

		if (biome == null) return super.getColorFromItemStack(stack, renderPass);

		return biome.color;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		int biomeID = stack.getItemDamage();

		if (biomeID == TileEntityBiomeScanner.NO_STORED_VALUE) {
			list.add("Place this in a biome scanner to duplicate it's signature.");
		} else {
			BiomeGenBase biome = BiomeGenBase.getBiome(biomeID);

			if (biome != null) {
				list.add("Signature: §5" + biome.biomeName);
			}
		}
	}
}
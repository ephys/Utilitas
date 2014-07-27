package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.BlockBiomeScanner;
import nf.fr.ephys.playerproxies.common.block.BlockToughwoodPlank;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;

import java.util.List;

public class ItemBiomeStorage extends Item {
	private static CreativeTabs biomesTab = new CreativeTabs(PlayerProxies.MODID + ".biomes") {
		//private ItemStack icon = new ItemStack(PlayerProxies.Items.biomeStorage, 1, MathHelper.getRandomIntegerInRange(new Random(), 0, 39));

		@Override
		public Item getTabIconItem() {
			return PlayerProxies.Items.biomeStorage;
		}

		/*@Override
		public ItemStack getIconItemStack() {
			return icon;
		}*/
	};

	public static void register() {
		if (!ItemBiomeStorage.enabled()) return;

		PlayerProxies.Items.biomeStorage = new ItemBiomeStorage();
		PlayerProxies.Items.biomeStorage.setUnlocalizedName("PP_BiomeStorage")
				.setMaxStackSize(1)
				.setCreativeTab(biomesTab)
				.setTextureName("ephys.pp:biomeStorage")
				.setHasSubtypes(true);

		GameRegistry.registerItem(PlayerProxies.Items.biomeStorage, PlayerProxies.Items.biomeStorage.getUnlocalizedName());
	}

	public static void registerCraft() {
		if (!ItemBiomeStorage.enabled()) return;

		MinecraftForge.EVENT_BUS.register(PlayerProxies.Items.biomeStorage);
	}

	@SubscribeEvent
	public void onEntityDrop(LivingDropsEvent event) {
		if (event.entity.worldObj.isRemote)
			return;

		if ((event.entity instanceof EntityWither
				&& event.source.getEntity() instanceof EntityPlayer
				&& Math.random() < 0.25D * (1 + event.lootingLevel))) {

			event.drops.add(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(PlayerProxies.Items.biomeStorage, 1)));
		}
	}

	public static boolean enabled() {
		return BlockBiomeScanner.enabled || BlockToughwoodPlank.transmuterEnabled;
	}

	@Override
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		int biomeID = stack.getItemDamage();

		if (biomeID == TileEntityBiomeScanner.NO_STORED_VALUE) {
			list.add("Place this in a biome scanner to duplicate it's signature.");
		} else {
			BiomeGenBase biome = BiomeGenBase.getBiome(biomeID);

			if (biome != null) {
				list.add("Signature: §5" + biome.biomeName);
			} else {
				list.add("Corrupted ! Destroy me :(");
			}
		}
	}
}
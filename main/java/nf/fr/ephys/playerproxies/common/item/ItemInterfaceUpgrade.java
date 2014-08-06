package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.MinecraftForge;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;

import java.util.List;

public class ItemInterfaceUpgrade extends Item implements IInterfaceUpgrade {
	private IIcon[] textures = new IIcon[3];

	public static final int CROSSDIM = 0;
	public static final int WIRELESS = 1;
	public static final int FLUIDHANDLER = 2;

	public static void register() {
		if (!ItemBiomeStorage.enabled()) return;

		PlayerProxies.Items.interfaceUpgrade = new ItemInterfaceUpgrade();
		PlayerProxies.Items.interfaceUpgrade.setUnlocalizedName("PP_InterfaceUpgrade")
				.setMaxStackSize(1)
				.setHasSubtypes(true);

		GameRegistry.registerItem(PlayerProxies.Items.interfaceUpgrade, PlayerProxies.Items.interfaceUpgrade.getUnlocalizedName());
	}

	@Override
	public void registerIcons(IIconRegister register) {
		textures[CROSSDIM] = register.registerIcon("ephys.pp:uniterface_upgrade_crossdim");
		textures[WIRELESS] = register.registerIcon("ephys.pp:uniterface_upgrade_wireless");
		textures[FLUIDHANDLER] = register.registerIcon("ephys.pp:uniterface_upgrade_fluidhandler");
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		return textures[damage];
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		String name = super.getUnlocalizedName(stack);

		switch (stack.getItemDamage()) {
			case CROSSDIM: return name + ".crossdim";
			case WIRELESS: return name + ".wireless";
			case FLUIDHANDLER: return name + ".fluidhandler";
		}

		return name;
	}

	public static void registerCraft() {
		if (!ItemBiomeStorage.enabled()) return;

		MinecraftForge.EVENT_BUS.register(PlayerProxies.Items.biomeStorage);
	}

	@Override
	public boolean getHasSubtypes() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void getSubItems(Item item, CreativeTabs tab, List itemList) {
		itemList.add(new ItemStack(item, 1, CROSSDIM));
		itemList.add(new ItemStack(item, 1, WIRELESS));
		itemList.add(new ItemStack(item, 1, FLUIDHANDLER));
	}

	@Override
	public boolean onInsert(TileEntityInterface tile, EntityPlayer player, ItemStack stack) {
		switch (stack.getItemDamage()) {
			case CROSSDIM:
				tile.setWorksCrossDim(true);
				break;

			case WIRELESS:
				tile.setWireless(true);
				break;

			case FLUIDHANDLER:
				tile.setIsFluidHandler(true);
				break;
		}

		return true;
	}

	@Override
	public void onRemove(TileEntityInterface tile, EntityPlayer player, ItemStack stack) {
		switch (stack.getItemDamage()) {
			case CROSSDIM:
				tile.setWorksCrossDim(false);
				break;

			case WIRELESS:
				tile.setWireless(false);
				break;

			case FLUIDHANDLER:
				tile.setIsFluidHandler(false);
				break;
		}
	}
}

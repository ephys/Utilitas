package be.ephys.utilitas.feature.universal_interface;

import be.ephys.utilitas.api.IInterfaceUpgrade;
import be.ephys.utilitas.base.helpers.ItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemInterfaceUpgrade extends Item implements IInterfaceUpgrade {

    public static final int CROSS_DIM = 0;
    public static final int WIRELESS = 1;
    public static final int FLUID_HANDLER = 2;

    public ItemInterfaceUpgrade() {
        ItemHelper.name(this, "interface_upgrade");

        this.setMaxStackSize(1).setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String name = super.getUnlocalizedName(stack);

        switch (stack.getItemDamage()) {
            case CROSS_DIM:
                return name + "-crossdim";
            case WIRELESS:
                return name + "-wireless";
            case FLUID_HANDLER:
                return name + "-fluidhandler";
        }

        return name;
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public boolean getHasSubtypes() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List itemList) {
        itemList.add(new ItemStack(item, 1, CROSS_DIM));
        itemList.add(new ItemStack(item, 1, WIRELESS));
        itemList.add(new ItemStack(item, 1, FLUID_HANDLER));
    }

    @Override
    public boolean onInsert(TileEntityInterface tile, ItemStack stack) {
        switch (stack.getItemDamage()) {
            case CROSS_DIM:
                tile.setWorksCrossDim(true);
                break;

            case WIRELESS:
                tile.setWireless(true);
                break;

            case FLUID_HANDLER:
                tile.setIsFluidHandler(true);
                break;
        }

        return true;
    }

    @Override
    public void onRemove(TileEntityInterface tile, ItemStack stack) {
        switch (stack.getItemDamage()) {
            case CROSS_DIM:
                tile.setWorksCrossDim(false);
                break;

            case WIRELESS:
                tile.setWireless(false);
                break;

            case FLUID_HANDLER:
                tile.setIsFluidHandler(false);
                break;
        }
    }
}

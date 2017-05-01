package be.ephys.utilitas.base.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class MutableCreativeTab extends CreativeTabs {

    private Item item = Items.APPLE;

    public MutableCreativeTab(String label) {
        super(label);
    }

    public void setIconItem(Item item) {
        this.item = item;
    }

    @Override
    public Item getTabIconItem() {
        return item;
    }
}

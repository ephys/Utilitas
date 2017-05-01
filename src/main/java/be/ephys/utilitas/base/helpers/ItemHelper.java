package be.ephys.utilitas.base.helpers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class ItemHelper {

    public static void name(Item item, String name) {
        item.setRegistryName(name);
        item.setUnlocalizedName(item.getRegistryName().toString());
    }

    public static void name(Block item, String name) {
        item.setRegistryName(name);
        item.setUnlocalizedName(item.getRegistryName().toString());
    }
}

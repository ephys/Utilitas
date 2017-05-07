package be.ephys.utilitas.feature.slime_staff;

import be.ephys.utilitas.base.helpers.ItemHelper;
import be.ephys.utilitas.base.helpers.NBTHelper;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSlimeStaff extends Item implements IItemColorable {

    public static final int META_DIRTY = 0;
    public static final int META_CLEAN = 1;

    public ItemSlimeStaff() {
        ItemHelper.name(this, "slime_staff");
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return Integer.MAX_VALUE;
    }

    @Override
    public IItemColor getColorHandler() {
        return ColorHandlerSlimeStaff.INSTANCE;
    }

    public static void setColor(ItemStack stack, int color) {
        NBTHelper.setInt(stack, "color", color);
    }

    public static int getColor(ItemStack stack) {
        return NBTHelper.getInt(stack, "color", -1);
    }
}

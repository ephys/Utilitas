package be.ephys.utilitas.feature.slime_staff;

import be.ephys.utilitas.base.helpers.ItemHelper;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;

public class ItemRainbowSlimeBall extends Item implements IItemColorable {

    public ItemRainbowSlimeBall() {
        ItemHelper.name(this, "rainbow_slime_ball");
    }

    @Override
    public IItemColor getColorHandler() {
        return ColorHandlerRainbowItem.INSTANCE;
    }
}

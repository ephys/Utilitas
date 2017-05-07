package be.ephys.utilitas.feature.slime_staff;

import be.ephys.utilitas.base.helpers.RenderHelper;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public class ColorHandlerRainbowItem implements IItemColor {

    public static final IItemColor INSTANCE = new ColorHandlerRainbowItem();

    @Override
    public int getColorFromItemstack(ItemStack stack, int tintIndex) {
        return RenderHelper.getRainbowColor(1, 0.9f, 1f);
    }
}

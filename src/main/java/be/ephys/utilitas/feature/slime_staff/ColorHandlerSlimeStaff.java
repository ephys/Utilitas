package be.ephys.utilitas.feature.slime_staff;

import be.ephys.utilitas.base.helpers.RenderHelper;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public class ColorHandlerSlimeStaff implements IItemColor {

    public static final IItemColor INSTANCE = new ColorHandlerSlimeStaff();

    @Override
    public int getColorFromItemstack(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) {
            return 0xffffff;
        }

        int color = ItemSlimeStaff.getColor(stack);

        if (color >= 0) {
            return color;
        }

        return RenderHelper.getRainbowColor(2, 0.8f, 0.8f);
    }
}

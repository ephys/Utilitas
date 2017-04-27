package be.ephys.utilitas.common.item;

import be.ephys.utilitas.common.block.IToolTipped;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemBlockTooltipped extends ItemBlock {

    public final boolean isTooltipped;

    public ItemBlockTooltipped(Block block) {
        super(block);

        isTooltipped = block instanceof IToolTipped;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> data, boolean debug) {
        if (isTooltipped) {
            block.addInformation(stack, player, data, debug);
        }

        super.addInformation(stack, player, data, debug);
    }
}

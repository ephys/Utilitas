package be.ephys.utilitas.base.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class MultiItemBlock extends ItemBlockTooltipped {

    public MultiItemBlock(Block block) {
        super(block);

        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damageValue) {
        return damageValue;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return super.getUnlocalizedName() + "-" + itemstack.getItemDamage();
    }
}

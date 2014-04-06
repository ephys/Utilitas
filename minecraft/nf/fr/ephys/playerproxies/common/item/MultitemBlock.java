package nf.fr.ephys.playerproxies.common.item;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class MultitemBlock extends ItemBlock {
	public MultitemBlock(int id) {
		super(id);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damageValue) {
		return damageValue;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return super.getUnlocalizedName() + "." + itemstack.getItemDamage();
	}
}
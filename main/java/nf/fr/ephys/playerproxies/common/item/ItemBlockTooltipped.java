package nf.fr.ephys.playerproxies.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.playerproxies.common.block.IToolTipped;

import java.util.List;

public class ItemBlockTooltipped extends ItemBlock {
	public final boolean IS_TOOLTIPPED;

	public ItemBlockTooltipped(Block block) {
		super(block);

		IS_TOOLTIPPED = block instanceof IToolTipped;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer player, List data, boolean debug) {
		if (IS_TOOLTIPPED)
			((IToolTipped) field_150939_a).addInformation(stack, player, data, debug);

		super.addInformation(stack, player, data, debug);
	}
}

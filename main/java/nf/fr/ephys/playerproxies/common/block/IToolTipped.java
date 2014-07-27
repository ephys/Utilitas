package nf.fr.ephys.playerproxies.common.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IToolTipped {
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> data, boolean debug);
}
package be.ephys.utilitas.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IToolTipped {

    void addInformation(ItemStack stack, EntityPlayer player, List<String> data, boolean debug);
}

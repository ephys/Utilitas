package be.ephys.utilitas.base.nbt_writer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackNbtWriter implements NbtWriter<ItemStack> {

    @Override
    public NBTBase toNbt(ItemStack data) {
        return data.writeToNBT(new NBTTagCompound());
    }

    @Override
    public ItemStack fromNbt(NBTBase nbt) {
        return ItemStack.loadItemStackFromNBT((NBTTagCompound) nbt);
    }
}

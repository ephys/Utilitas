package be.ephys.utilitas.base.nbt_writer;

import net.minecraft.nbt.NBTTagCompound;

public interface INbtSerializable {

    NBTTagCompound writeToNBT(NBTTagCompound tag);

    void readFromNBT(NBTTagCompound tag);
}

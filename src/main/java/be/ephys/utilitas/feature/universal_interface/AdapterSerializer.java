package be.ephys.utilitas.feature.universal_interface;

import be.ephys.utilitas.api.registry.UniversalInterfaceAdapter;
import be.ephys.utilitas.api.registry.UniversalInterfaceRegistry;
import be.ephys.utilitas.base.helpers.NBTHelper;
import be.ephys.utilitas.base.nbt_writer.NbtWriter;
import be.ephys.utilitas.feature.universal_interface.interface_adapters.InterfaceDummy;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class AdapterSerializer implements NbtWriter<UniversalInterfaceAdapter> {

    @Override
    public NBTBase toNbt(UniversalInterfaceAdapter adapter) {
        NBTTagCompound adapterData = new NBTTagCompound();
        NBTHelper.setClass(adapterData, "class", adapter.getClass());
        adapterData.setTag("instance", adapter.writeToNBT(new NBTTagCompound()));

        return adapterData;
    }

    @Override
    public UniversalInterfaceAdapter fromNbt(NBTBase nbt) {
        NBTTagCompound adapterData = (NBTTagCompound) nbt;

        Class<? extends UniversalInterfaceAdapter> clazz = (Class<? extends UniversalInterfaceAdapter>) NBTHelper.getClass(adapterData, "class");

        if (clazz == null || clazz == InterfaceDummy.class) {
            return InterfaceDummy.INSTANCE;
        }

        if (!UniversalInterfaceRegistry.hasHandler(clazz)) {
            return InterfaceDummy.INSTANCE;
        }

        try {
            UniversalInterfaceAdapter adapter = clazz.getConstructor(TileEntityInterface.class).newInstance(this);
            adapter.readFromNBT(adapterData.getCompoundTag("instance"));

            return adapter;
        } catch (Exception e) {
            e.printStackTrace();

            return InterfaceDummy.INSTANCE;
        }
    }
}

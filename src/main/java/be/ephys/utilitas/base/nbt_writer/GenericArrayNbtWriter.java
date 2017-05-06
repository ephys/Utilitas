package be.ephys.utilitas.base.nbt_writer;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;

public class GenericArrayNbtWriter<T> implements NbtWriter<T[]> {

    private final NbtWriter<T> componentWriter;

    public GenericArrayNbtWriter(NbtWriter<T> writer) {
        this.componentWriter = writer;
    }

    @Override
    public NBTBase toNbt(T[] data) {
        NBTTagList tagList = new NBTTagList();

        // this is a hack to note the length of the array as we cannot store nulls in the array
        NBTTagCompound finalTag = new NBTTagCompound();
        finalTag.setInteger("ganw__length", data.length);

        for (int i = 0; i < data.length; i++) {
            T datum = data[i];

            NBTBase componentNbt = componentWriter.toNbt(datum);
            if (!(componentNbt instanceof NBTTagCompound)) {
                NBTTagCompound wrapper = new NBTTagCompound();
                wrapper.setTag("ganw__item", componentNbt);

                componentNbt = wrapper;
            }

            NBTTagCompound subtag = (NBTTagCompound) componentNbt;
            subtag.setInteger("ganw__slot", i);

            tagList.appendTag(subtag);
        }

        return tagList;
    }

    @Override
    public T[] fromNbt(NBTBase nbt) {
        NBTTagList tagList = (NBTTagList) nbt;

        int length = ((NBTTagList) nbt).getCompoundTagAt(0).getInteger("ganw__length");

        Class<T> arrayGenericType = ((Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);

        @SuppressWarnings("unchecked")
        final T[] array = (T[]) Array.newInstance(arrayGenericType, length);

        int tagCount = ((NBTTagList) nbt).tagCount();
        for (int i = 1; i < tagCount; i++) {
            NBTTagCompound tagMeta = tagList.getCompoundTagAt(i);
            int slot = tagMeta.getInteger("ganw__slot");

            NBTBase tag = tagMeta.hasKey("ganw__slot") ? tagMeta.getTag("ganw__item") : tagMeta;

            array[slot] = this.componentWriter.fromNbt(tag);
        }

        return array;
    }
}

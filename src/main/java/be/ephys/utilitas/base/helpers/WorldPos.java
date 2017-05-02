package be.ephys.utilitas.base.helpers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by ephys on 02/05/2017.
 */
public class WorldPos {
    public final World world;
    public final BlockPos pos;

    public WorldPos(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    public static WorldPos readFromNbt(NBTTagCompound nbt) {
        BlockPos pos = NBTHelper.getBlockPos(nbt, "pos", null);
        World world = WorldHelper.getWorldForDim(nbt.getInteger("world"));

        return new WorldPos(world, pos);
    }

    public NBTTagCompound writeToNbt() {
        return this.writeToNbt(new NBTTagCompound());
    }

    public NBTTagCompound writeToNbt(NBTTagCompound nbt) {
        NBTHelper.setBlockPos(nbt, "pos", pos);
        nbt.setInteger("world", world.provider.getDimension());

        return nbt;
    }

    @Override
    public String toString() {
        return "BlockPos["+ world.provider.getDimensionType().getName() + " { " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + " }]";
    }
}

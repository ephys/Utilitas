package be.ephys.utilitas.common.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class WorldHelper {

    public static void markBlockForUpdate(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 8);
    }

    public static void markTileForUpdate(TileEntity tile) {
        markBlockForUpdate(tile.getWorld(), tile.getPos());
    }

    public static World getWorldForDim(int dimId) {
        MinecraftServer server = getServer();

        return server.worldServerForDimension(dimId);
    }

    public static MinecraftServer getServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }
}

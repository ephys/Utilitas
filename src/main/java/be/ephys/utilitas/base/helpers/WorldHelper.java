package be.ephys.utilitas.base.helpers;

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

    public static boolean areSideBySide(BlockPos pos1, BlockPos pos2) {

        return Math.abs(pos1.getX() - pos2.getX())
            + Math.abs(pos1.getY() - pos2.getY())
            + Math.abs(pos1.getZ() - pos2.getZ()) == 1;
    }

    public final boolean isClient() {
        return FMLCommonHandler.instance().getSide().isClient();
    }

    public final boolean isServer() {
        return !isClient();
    }
}

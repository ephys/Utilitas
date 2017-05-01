package be.ephys.utilitas.base.helpers;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public final class ChatHelper {
    public static void sendChatMessage(ICommandSender player, String message) {
        if (!player.getEntityWorld().isRemote) {
            player.addChatMessage(new TextComponentString(message));
        }
    }

    public static void sendChatMessage(ICommandSender player, ITextComponent message) {
        if (!player.getEntityWorld().isRemote) {
            player.addChatMessage(message);
        }
    }

    public static String getDisplayName(TileEntity te) {
        if (te == null) {
            return "null";
        }

        return getDisplayName(te.getBlockType(), te.getBlockMetadata());
    }

    public static String getDisplayName(Block block) {
        return getDisplayName(block, 0);
    }

    public static String getDisplayName(Block block, int metadata) {
        return new ItemStack(block, 1, metadata).getDisplayName();
    }

    public static String getDisplayName(Fluid fluid) {
        return getDisplayName(new FluidStack(fluid, 1000));
    }

    public static String getDisplayName(FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();

        String name = fluid.getLocalizedName(fluidStack);

        if (fluid.canBePlacedInWorld() && name.equals(fluid.getUnlocalizedName())) {
            return getDisplayName(fluid.getBlock());
        }

        return name;
    }
}

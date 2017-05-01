package be.ephys.utilitas.base.core;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class GuiHandler implements IGuiHandler {

    private List<GuiWrapper> wrapperList = new ArrayList<>();

    public int registerGui(GuiWrapper wrapper) {
        int id = wrapperList.size();

        wrapper.setId(id);
        wrapperList.add(wrapper);

        return id;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (wrapperList.size() <= ID) {
            return null;
        }

        GuiWrapper wrapper = wrapperList.get(ID);
        return wrapper.getContainer(player, world, new BlockPos(x, y, z));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (wrapperList.size() <= ID) {
            return null;
        }

        GuiWrapper wrapper = wrapperList.get(ID);
        return wrapper.getGui(player, world, new BlockPos(x, y, z));
    }

    public static abstract class GuiWrapper {

        private int id = -1;

        public abstract Container getContainer(EntityPlayer player, World world, BlockPos blockPos);

        @SideOnly(Side.CLIENT)
        public abstract Gui getGui(EntityPlayer player, World world, BlockPos blockPos);

        private void setId(int id) {
            this.id = id;
        }

        public int getId() {
            if (id < 0) {
                throw new IllegalStateException("Trying to get the ID of an unregistered GUI.");
            }

            return id;
        }
    }
}

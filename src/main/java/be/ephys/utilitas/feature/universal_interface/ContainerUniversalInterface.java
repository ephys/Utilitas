package be.ephys.utilitas.feature.universal_interface;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerUniversalInterface extends Container {
    TileEntityInterface te;

    public ContainerUniversalInterface(EntityPlayer player, TileEntityInterface te) {
        this.te = te;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }

    public TileEntityInterface getTileEntity() {
        return this.te;
    }
}

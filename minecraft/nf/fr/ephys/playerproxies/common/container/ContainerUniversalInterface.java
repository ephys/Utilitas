package nf.fr.ephys.playerproxies.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;

public class ContainerUniversalInterface extends Container {
	public ContainerUniversalInterface(EntityPlayer player, TEBlockInterface te) {

	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}
}

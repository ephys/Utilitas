package nf.fr.ephys.playerproxies.common.container;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import nf.fr.ephys.playerproxies.common.core.PacketHandler;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;

public class ContainerUniversalInterface extends Container {
	TileEntityInterface te;
	EntityPlayer player;

	public ContainerUniversalInterface(EntityPlayer player, TileEntityInterface te) {
		this.te = te;
		this.player = player;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void toggleEnderMode() {
		this.te.toggleEnderMode();

		PacketHandler.sendPacketInterfaceToggle(te.xCoord, te.yCoord, te.zCoord);
	}

	public TileEntityInterface getTileEntity() {
		return this.te;
	}
}

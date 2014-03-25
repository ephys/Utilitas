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
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;

public class ContainerBiomeScanner extends Container {
	private TileEntityBiomeScanner te;
	private EntityPlayer player;

	public ContainerBiomeScanner(EntityPlayer player, TileEntityBiomeScanner te) {
		this.te = te;
		this.player = player;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	public TileEntityBiomeScanner getTileEntity() {
		return this.te;
	}
}

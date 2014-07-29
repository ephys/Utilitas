package nf.fr.ephys.playerproxies.common.registry.uniterface;

import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.helpers.EntityHelper;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public class InterfaceMinecartFurnace extends UniversalInterface {
	private EntityMinecartFurnace minecart;
	private UUID uuid;

	public InterfaceMinecartFurnace(TileEntityInterface tileEntity) {
		super(tileEntity);
	}

	@Override
	public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
		//RenderManager.instance.renderEntityWithPosYaw(minecart, x, y, z, 0, 0);

		// todo: real render duh
		GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);

		nf.fr.ephys.playerproxies.client.renderer.TileEntityInterfaceRenderer.renderBlocksInstance.renderBlockAsItem(Blocks.chest, 0, 1.0F);
	}

	@Override
	public boolean setLink(Object link, EntityPlayer linker) {
		if (link instanceof EntityMinecartFurnace) {
			minecart = (EntityMinecartFurnace) link;

			return true;
		}

		return false;
	}

	@Override
	public String getName() {
		return minecart.getCommandSenderName();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTHelper.setEntity(nbt, "minecart", minecart);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		uuid = NBTHelper.getUUID(nbt, "minecart", null);
	}

	@Override
	public void onTick(int tick) {
		if (minecart == null && uuid != null) {
			minecart = (EntityMinecartFurnace) EntityHelper.getEntityByUUID(uuid);
		}

		if (minecart == null || minecart.isDead)
			getTileEntity().unlink();
	}

	@Override
	public IInventory getInventory() {
		// todo but needs ASM to expose the fuel level >_>
		return null;
	}

	@Override
	public IFluidHandler getFluidHandler() {
		return null;
	}

	@Override
	public void onBlockUpdate() {}

	@Override
	public void validate() {}
}

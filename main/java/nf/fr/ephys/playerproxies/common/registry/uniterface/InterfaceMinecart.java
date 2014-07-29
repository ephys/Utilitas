package nf.fr.ephys.playerproxies.common.registry.uniterface;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class InterfaceMinecart extends UniversalInterface {
	private EntityMinecartContainer minecart;

	public InterfaceMinecart(TileEntityInterface tileEntity) {
		super(tileEntity);
	}

	@Override
	public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
		RenderManager.instance.renderEntityWithPosYaw(minecart, x, y, z, 0, 0);
	}

	@Override
	public boolean setLink(Object link, EntityPlayer linker) {
		if (link instanceof EntityMinecartContainer) {
			minecart = (EntityMinecartContainer) link;

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
		minecart = (EntityMinecartContainer) NBTHelper.getEntity(nbt, "minecart", null);
	}

	@Override
	public void onTick(int tick) {
		if (minecart == null || minecart.isDead)
			getTileEntity().unlink();
	}

	@Override
	public IInventory getInventory() {
		return minecart;
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

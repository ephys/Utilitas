package be.ephys.utilitas.common.registry.interface_adapters;

import be.ephys.utilitas.common.tileentity.TileEntityInterface;
import be.ephys.utilitas.common.util.EntityHelper;
import be.ephys.utilitas.common.util.NBTHelper;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public class InterfaceMinecart extends UniversalInterfaceAdapter {

	private EntityMinecartContainer minecart;
	private UUID uuid;

	public InterfaceMinecart(TileEntityInterface tileEntity) {
		super(tileEntity);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
		GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);

		renderBlock(minecart.getDisplayTile().getBlock());
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
	public ITextComponent getName() {
		return minecart.getDisplayName();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTHelper.setEntityUuid(nbt, "minecart", minecart);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		uuid = NBTHelper.getUuid(nbt, "minecart", null);
	}

	@Override
	public void onTick(int tick) {
		if (isRemote()) {
			return;
		}

		if (minecart == null && uuid != null) {
			minecart = (EntityMinecartContainer) EntityHelper.getEntityByUuid(uuid);
		}

		if (minecart == null || minecart.isDead) {
			getInterface().unlink();
		}
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
	public boolean isNextTo(int xCoord, int yCoord, int zCoord) {
		return false;
	}

	@Override
	public int getDimension() {
		return minecart == null ? 0 : minecart.worldObj.provider.dimensionId;
	}

	@Override
	public void onBlockUpdate() {}

	@Override
	public void validate() {}
}

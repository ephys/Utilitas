package nf.fr.ephys.playerproxies.common.registry.uniterface;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;

public abstract class UniversalInterface {
	private TileEntityInterface tileEntity;

	public UniversalInterface(TileEntityInterface tileEntity) {
		this.tileEntity = tileEntity;
	}

	public TileEntityInterface getTileEntity() {
		return tileEntity;
	}

	/**
	 * This method renders the inside of the universal interface depending on it's link type
	 *
	 * @param tickCount the amount of times this method has been called
	 */
	@SideOnly(Side.CLIENT)
	public abstract void renderInventory(int tickCount, double x, double y, double z, float tickTime);

	/**
	 * Link this handler with another object
	 * @param link		The object to link
	 * @param linker	The player who tried linking
	 * @return success
	 */
	public abstract boolean setLink(Object link, EntityPlayer linker);

	public abstract String getName();

	public abstract void validate();

	public abstract void writeToNBT(NBTTagCompound nbt);

	public abstract void readFromNBT(NBTTagCompound nbt);

	public abstract void onBlockUpdate();

	public abstract void onTick(int tick);

	public abstract IInventory getInventory();

	public abstract IFluidHandler getFluidHandler();

	/**
	 * Gets the distance between coords and the linked object
	 *
	 * @param xCoord the x coord of the first point
	 * @param yCoord the y coord of the first point
	 * @param zCoord the z coord of the first point
	 * @return the distance or -1 if unlinked
	 */
	public abstract int getDistance(int xCoord, int yCoord, int zCoord);

	/**
	 * @return the dimention id of the object, 0 if the object is null
	 */
	public abstract int getDim();
}
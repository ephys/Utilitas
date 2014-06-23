package nf.fr.ephys.playerproxies.common.block.uniterface;

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
	public abstract void renderInventory(int tickCount, double par1, double par3, double par5, float par7);

	/**
	 * Link this handler with another object
	 * @param link
	 * @return success
	 */
	public abstract boolean setLink(Object link);

	public abstract void writeToNBT(NBTTagCompound nbt);

	public abstract void readFromNBT(NBTTagCompound nbt);

	public abstract void onBlockUpdate(int side);
	
	public abstract void onTick();

	public abstract IInventory getInventory();

	public abstract IFluidHandler getFluidHandler();
}
package nf.fr.ephys.playerproxies.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.playerproxies.util.cofh.TileEnergyHandler;

public class TileEntityPotionDiffuser extends TileEnergyHandler implements ISidedInventory, IFluidHandler {
	private FluidTank tank = new FluidTank(20000);
	
	public static final int RANGE = 16;

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return this.tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource != null && tank.getFluid().equals(resource))
			return drain(from, resource.amount, doDrain);

		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		FluidStack stack = this.tank.drain(maxDrain, doDrain);

		return stack;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { this.tank.getInfo() };
	}
	
	@Override
	public void updateEntity() {
		System.out.println(this.tank.getFluidAmount());
		if (this.tank.getFluidAmount() >= 1000) {
			List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(
				this.xCoord - RANGE, this.yCoord - RANGE, this.zCoord - RANGE, 
				this.xCoord + RANGE, this.yCoord + RANGE, this.zCoord + RANGE
			));
			
			for (EntityLivingBase entity : entities) {
				Block block = Block.blocksList[this.tank.getFluid().getFluid().getBlockID()];

				block.onEntityCollidedWithBlock(worldObj, xCoord, yCoord, zCoord, entity);
				block.onEntityWalking(worldObj, xCoord, yCoord, zCoord, entity);
			}
			
			this.tank.drain(1000, true);
		}
		
		super.updateEntity();
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInvName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInvNameLocalized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openChest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeChest() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		// TODO Auto-generated method stub
		return false;
	}

}

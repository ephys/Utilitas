package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.util.cofh.TileEnergyHandler;

import java.util.List;

public class TileEntityPotionDiffuser extends TileEnergyHandler implements IFluidHandler {
	private FluidTank tank = new FluidTank(20000);

	public static final int RANGE = 8;
	public static final int ENERGY_CONSUMPTION = 80;
	public static final int INTERVAL = 50;

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		tank.writeToNBT(nbt);

		super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		tank.readFromNBT(nbt);

		super.readFromNBT(nbt);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);

		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());

		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord + 1, zCoord);
	}

	@Override
	public int fill(ForgeDirection forgeDirection, FluidStack fluidStack, boolean b) {
		if (!fluidStack.getFluid().canBePlacedInWorld()) return 0;

		int amnt = this.tank.fill(fluidStack, b);

		sendUpdate();

		return amnt;
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

		sendUpdate();

		return stack;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid.canBePlacedInWorld();
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
		if (worldObj.getTotalWorldTime() % INTERVAL != 0) return;

		if (PlayerProxies.getConfig().requiresPower()) {
			if (storage.getEnergyStored() < ENERGY_CONSUMPTION) return;

			storage.extractEnergy(ENERGY_CONSUMPTION, false);
		}

		if (this.tank.getFluidAmount() >= INTERVAL) {
			@SuppressWarnings("unchecked")
			List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(
				this.xCoord, this.yCoord, this.zCoord,
				this.xCoord, this.yCoord, this.zCoord
			).expand(RANGE, RANGE, RANGE));

			for (EntityLivingBase entity : entities) {
				Block block = this.tank.getFluid().getFluid().getBlock();

				// TODO: add a new fluid that will remove the effects
				if (block.equals(Blocks.water)) {
					entity.clearActivePotions();
				} else if (block.getMaterial().equals(Material.lava)) {
					if (!entity.isImmuneToFire()) {
						entity.setFire(INTERVAL);
					}
				}

				block.onEntityCollidedWithBlock(worldObj, xCoord, yCoord, zCoord, entity);
				block.onEntityWalking(worldObj, xCoord, yCoord, zCoord, entity);
			}

			this.tank.drain(INTERVAL, true);
		}

		super.updateEntity();
	}

	private void sendUpdate() {
		markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public FluidStack getFluid() {
		return tank.getFluid();
	}
}
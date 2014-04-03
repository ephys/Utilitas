package nf.fr.ephys.playerproxies.common.tileentity;

import cofh.api.energy.TileEnergyHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import nf.fr.ephys.playerproxies.common.core.GravitationalFieldRegistry;

public class TileEntityGravitationalField extends TileEnergyHandler {
	public static final int RANGE = 64;

	public static final float MIN_GRAVITY = 0.74F;
	public static final float MAX_GRAVITY = 1.2F;

	private boolean isPowered;
	private boolean wasPowered;
	
	private boolean hasEnergy;
	private boolean hadEnergy;
	
	public TileEntityGravitationalField() {
		storage.setCapacity(10000);
		storage.setMaxReceive(5000);
	}

	public boolean isActive() {
		return !isPowered && hasEnergy;
	}

	public Packet132TileEntityData getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("isPowered", isPowered);
		nbt.setBoolean("hasEnergy", hasEnergy);

		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 4, nbt);
	}

	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		if (!this.worldObj.isRemote) return;

		NBTTagCompound nbt = packet.data;
		
		if (nbt.hasKey("isPowered")) {
			isPowered = nbt.getBoolean("isPowered");
			hasEnergy = nbt.getBoolean("hasEnergy");
			worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		}
	}

	public void checkPowered() {
		isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		
		if (isPowered ^ wasPowered) {
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			wasPowered = isPowered;
		}
	}

	public float getGravityModifier() {
		return 0.75F;
	}

	public final boolean inRange(Entity player) {
		// cubed range or spherical range ?
		/*
		 * return Math.cbrt(Math.abs(Math.pow(tile[0] - player.posX, 2)) +
		 * Math.abs(Math.pow(tile[1] - player.posY, 2)) +
		 * Math.abs(Math.pow(tile[2] - player.posZ, 2))) < RANGE;
		 */

		return Math.abs(xCoord - player.posX) < RANGE
				&& Math.abs(yCoord - player.posY) < RANGE
				&& Math.abs(zCoord - player.posZ) < RANGE;
	}
	
	@Override
	public void updateEntity() {
		if (this.worldObj.isRemote) return;
		if (isPowered) return;

		this.hasEnergy = this.storage.getEnergyStored() > 100;

		if (this.hasEnergy)
			this.storage.extractEnergy(100, false);
		
		if (hasEnergy ^ hadEnergy) {
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			hadEnergy = hasEnergy;
		}
	}

	@Override
	public void invalidate() {
		GravitationalFieldRegistry.remove(this);

		super.invalidate();
	}

	@Override
	public void validate() {
		super.validate();

		GravitationalFieldRegistry.add(this);
	}
}

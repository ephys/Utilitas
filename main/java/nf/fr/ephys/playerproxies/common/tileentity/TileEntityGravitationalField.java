package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.registry.GravitationalFieldRegistry;
import nf.fr.ephys.playerproxies.util.cofh.TileEnergyHandler;

public class TileEntityGravitationalField extends TileEnergyHandler {
	public static final int RANGE = 64;

	private boolean isPowered;
	private boolean wasPowered;

	private boolean hasEnergy = true;
	private boolean hadEnergy = true;

	public static final float MIN_GRAVITY = 0.7399F;
	public static final float MAX_GRAVITY = 1.2F;

	private float gravityModifier = 0.75F;

	public TileEntityGravitationalField() {
		storage.setCapacity(10000);
		storage.setMaxReceive(5000);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setFloat("gravityModifier", gravityModifier);

		super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("gravityModifier"))
			gravityModifier = nbt.getFloat("gravityModifier");

		super.readFromNBT(nbt);
	}

	public float setGravityModifier(float modifier) {
		this.gravityModifier = Math.min(Math.max(modifier, MIN_GRAVITY), MAX_GRAVITY);

		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);

		return this.gravityModifier;
	}

	public float getGravityModifier() {
		return gravityModifier;
	}

	public boolean isActive() {
		return !isPowered && hasEnergy;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		if (!this.worldObj.isRemote) return;

		NBTTagCompound nbt = packet.func_148857_g();

		if (nbt.hasKey("isPowered"))
			isPowered = nbt.getBoolean("isPowered");

		if (nbt.hasKey("hasEnergy"))
			hasEnergy = nbt.getBoolean("hasEnergy");

		if (nbt.hasKey("gravityModifier"))
			gravityModifier = nbt.getFloat("gravityModifier");

		worldObj.func_147479_m(xCoord, yCoord, zCoord);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("isPowered", isPowered);
		nbt.setBoolean("hasEnergy", hasEnergy);
		nbt.setFloat("gravityModifier", gravityModifier);

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 4, nbt);
	}

	public void checkPowered() {
		if (this.getWorldObj() == null)
			isPowered = false;
		else {
			isPowered = this.getWorldObj().isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

			if (isPowered ^ wasPowered) {
				this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				wasPowered = isPowered;
			}
		}
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

		if (PlayerProxies.requiresPower()) {
			this.hasEnergy = this.storage.getEnergyStored() >= 100;

			if (this.hasEnergy)
				this.storage.extractEnergy(100, false);

			if (hasEnergy ^ hadEnergy) {
				this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				hadEnergy = hasEnergy;
			}
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

package nf.fr.ephys.playerproxies.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumGameType;
import nf.fr.ephys.playerproxies.common.entity.Ghost;

public class TESpawnerLoader extends TileEntity {
	private Ghost ghost = null;
	private String owner = null;

	@Override
	public void invalidate() {
		if (ghost != null)
			ghost.setDead();

		super.invalidate();
	}

	public void setOwner(String username) {
		if (this.owner != null)
			return;

		this.owner = username;
		spawnGhost();
	}

	private void spawnGhost() {
		ghost = new Ghost(this.worldObj, owner);

		ghost.setPosition(this.xCoord + 0.5, this.yCoord + 1, this.zCoord + 0.5);

		this.worldObj.spawnEntityInWorld(ghost);

		ghost.setPosition(this.xCoord + 0.5, this.yCoord + 1, this.zCoord + 0.5);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("owner", this.owner);

		super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.owner = nbt.getString("owner");

		super.readFromNBT(nbt);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (ghost == null || ghost.isDead)
			spawnGhost();
	}
}

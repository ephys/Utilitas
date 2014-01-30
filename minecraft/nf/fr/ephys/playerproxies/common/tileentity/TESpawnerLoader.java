package nf.fr.ephys.playerproxies.common.tileentity;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumGameType;
import nf.fr.ephys.playerproxies.common.entity.Ghost;

public class TESpawnerLoader extends TileEntity {
	private Ghost ghost = null;
	private String owner = null;

	@Override
	public void invalidate() {
		detach();

		super.invalidate();
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new Packet132TileEntityData(this.xCoord, this.yCoord,
				this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		readFromNBT(packet.data);
	}
	
	public void setOwner(String username) {
		if (this.owner != null)
			return;
		
		this.owner = username;

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		if(this.owner != null && !this.owner.equals(""))
			nbt.setString("owner", this.owner);

		super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey("owner"))
			this.owner = nbt.getString("owner");

		super.readFromNBT(nbt);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (this.ghost == null || this.ghost.isDead) {
			if(this.owner != null) {
				//spawnGhost(); TODO: catch closest ghost with that username
			} else if(Math.random() > 0.85F) { // search for a ghost in a 10 blocks radius
				if(worldObj.getBlockId(xCoord, yCoord+1, zCoord) == 0 && worldObj.getBlockId(xCoord, yCoord+2, zCoord) == 0) {	
					List<Ghost> ghostList = this.worldObj.getEntitiesWithinAABB(Ghost.class, AxisAlignedBB.getBoundingBox(
						this.xCoord-10, this.yCoord-10, this.zCoord-10,
						this.xCoord+10, this.yCoord+10, this.zCoord+10
					));
					
					int ghostSize = ghostList.size();
					if(ghostSize != 0) {
						for(int i = 0; i < ghostSize; i++) {
							Ghost ghost = ghostList.get(i);
							if(ghost.getLinkedStabilizer() == null) {
								this.ghost = ghost;
								this.ghost.setLinkedStabilizer(this);
								this.owner = this.ghost.username;
								break;
							}
						}
					}
				}
			}
		}
	}

	public void detach() {
		if(this.ghost != null) {
			this.ghost.setLinkedStabilizer(null);
			this.ghost = null;
		}

		this.owner = null;
	}
	
	public void recreate() {
		ghost = new Ghost(this.worldObj, owner, this);
	}

	public boolean isWorking() {
		// TODO require RFs
		return true;
	}

	public String getOwner() {
		return this.owner;
	}
}

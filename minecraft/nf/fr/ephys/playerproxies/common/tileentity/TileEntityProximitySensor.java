package nf.fr.ephys.playerproxies.common.tileentity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class TileEntityProximitySensor extends TileEntity {
	private int RADIUS_X = 3;
	private int RADIUS_Y = 3;
	private int RADIUS_Z = 3;

	public static int MAX_RADIUS = 10;

	public boolean isActivated = false;
	private boolean lastIsActivated = false;

	private int updateTick = 0;

	private Class<? extends Entity> entityFilter = Entity.class;
	private String playerFilter = null;

	public void setEntityFilter(String playerName, EntityPlayer player) {
		if(playerFilter != null && playerFilter.equals(playerName)) {
			player.addChatMessage("Entity filter already set to "+playerName);
			return;
		}
		
		player.addChatMessage("Sensor filtered to only match player "+playerName);

		this.playerFilter = playerName;
		this.entityFilter = null;
		
		this.updateTick = 25;
	}
	
	public void setEntityFilter(Class<? extends Entity> clazz, EntityPlayer player, String entityName) {
		clazz = (clazz == null) ? Entity.class : clazz;

		if(this.entityFilter != null && this.entityFilter.equals(clazz)) {
			player.addChatMessage("Entity filter already set to "+entityName);
			return;
		}
			
		if(clazz == Entity.class) {
			player.addChatMessage("Removed entity filter");
		} else {
			player.addChatMessage("Sensor filtered to only match: "+entityName);
		}
		
		this.playerFilter = null;
		this.entityFilter = clazz;
		
		this.updateTick = 25;
	}

	public void updateRadius(int side, EntityPlayer player) {
		switch(side) {
			case 1:
			case 0:
				if(player.isSneaking()) {
					if(RADIUS_Y == 1)
						break;
					
					RADIUS_Y--;
				} else {
					if(RADIUS_Y == MAX_RADIUS)
						break;
					
					RADIUS_Y++;
				}
				
				break;
			
			case 4:
			case 5:
				if(player.isSneaking()) {
					if(RADIUS_X == 1)
						break;
					
					RADIUS_X--;
				} else {
					if(RADIUS_X == MAX_RADIUS)
						break;
					
					RADIUS_X++;
				}
				
				break;
				
			case 3:
			case 2:
				if(player.isSneaking()) {
					if(RADIUS_Z == 1)
						break;
					
					RADIUS_Z--;
				} else {
					if(RADIUS_Z == MAX_RADIUS)
						break;
					
					RADIUS_Z++;
				}
				
				break;
		}
		
		player.addChatMessage("Detection radius: "+RADIUS_X+"*"+RADIUS_Y+"*"+RADIUS_Z);
		updateTick = 25;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if (updateTick != 15) {
			updateTick++;
			return;
		}
		
		updateTick = 0;
		
		AxisAlignedBB radius = AxisAlignedBB.getBoundingBox(
			xCoord-RADIUS_X, 
			yCoord-RADIUS_Y, 
			zCoord-RADIUS_Z,
			xCoord+RADIUS_X,
			yCoord+RADIUS_Y,
			zCoord+RADIUS_Z
		);

		if(this.entityFilter != null) {
			List entityList = this.worldObj.getEntitiesWithinAABB(entityFilter, radius);
			
			isActivated = (entityList.size() != 0);
		} else if(this.playerFilter != null) {
			List<EntityPlayer> entityList = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, radius);
			
			isActivated = false;
			for (int i = 0; i < entityList.size(); i++) {
				if (entityList.get(i).username.equals(this.playerFilter))
					isActivated = true;
			}
		}
		
		if(isActivated ^ lastIsActivated) {
	        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
	        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
			lastIsActivated = isActivated;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.isActivated = NBTHelper.getBoolean(nbt, "isActivated", this.isActivated);
		this.entityFilter = (Class<? extends Entity>) NBTHelper.getClass(nbt, "entityFilter", null);
		this.playerFilter = NBTHelper.getString(nbt, "playerFilter", null);

		if(nbt.hasKey("size")) {
			int[] size = nbt.getIntArray("size");
			this.RADIUS_X = size[0];
			this.RADIUS_Y = size[1];
			this.RADIUS_Z = size[2];
		}

		super.readFromNBT(nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("isActivated", isActivated);
		nbt.setIntArray("size", new int[]{RADIUS_X, RADIUS_Y, RADIUS_Z});
		NBTHelper.setString(nbt, "playerFilter", playerFilter);
		NBTHelper.setClass(nbt, "entityFilter", entityFilter);

		super.writeToNBT(nbt);
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		this.readFromNBT(pkt.data);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}


}

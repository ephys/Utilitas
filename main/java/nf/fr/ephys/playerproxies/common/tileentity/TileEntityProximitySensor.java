package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import nf.fr.ephys.playerproxies.helpers.CommandHelper;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

import java.util.List;

public class TileEntityProximitySensor extends TileEntity {
	private int RADIUS_X = 3;
	private int RADIUS_Y = 3;
	private int RADIUS_Z = 3;

	public static int MAX_RADIUS = 15;

	public boolean isActivated = false;

	private int updateTick = 0;

	private Object[] entityList = new Entity[0];

	private Class<? extends Entity> entityFilter = Entity.class;
	private String playerFilter = null;

	public void setEntityFilter(Entity entity, EntityPlayer player) {
		if (entity == null) {
			this.entityFilter = Entity.class;

			CommandHelper.sendChatMessage(player, "Filter cleared");
		} else if (entity instanceof EntityPlayer) {
			EntityPlayer playerFilter = (EntityPlayer) entity;

			this.playerFilter = playerFilter.getGameProfile().getId();
			this.entityFilter = null;

			CommandHelper.sendChatMessage(player, "Filter set to user " + playerFilter.getDisplayName());
		} else {
			this.playerFilter = null;
			this.entityFilter = entity.getClass();

			CommandHelper.sendChatMessage(player, "Filter set to " + entity.getCommandSenderName());
		}
	}

	public void updateRadius(int side, EntityPlayer player) {
		int increase = player.isSneaking() ? -1 : 1;
		switch(side) {
			case 1:
			case 0:
				RADIUS_Y += increase;
				break;

			case 4:
			case 5:
				RADIUS_X += increase;
				break;

			case 3:
			case 2:
				RADIUS_Z += increase;
				break;
		}

		CommandHelper.sendChatMessage(player, "Detection radius: " + RADIUS_X + "*" + RADIUS_Y + "*" + RADIUS_Z);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (updateTick != 10) {
			updateTick++;
			return;
		}

		updateTick = 0;

		AxisAlignedBB radius = AxisAlignedBB.getBoundingBox(
			xCoord - RADIUS_X,
			yCoord - RADIUS_Y,
			zCoord - RADIUS_Z,
			xCoord + RADIUS_X,
			yCoord + RADIUS_Y,
			zCoord + RADIUS_Z
		);

		boolean active = false;
		if(this.entityFilter != null) {
			List entityList = this.worldObj.getEntitiesWithinAABB(entityFilter, radius);

			this.entityList = entityList.toArray();

			active = (entityList.size() != 0);
		} else if(this.playerFilter != null) {
			List<EntityPlayer> entityList = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, radius);

			this.entityList = entityList.toArray();

			active = false;
			for (EntityPlayer anEntityList : entityList) {
				if (anEntityList.getGameProfile().getId().equals(this.playerFilter)) {
					active = true;

					break;
				}
			}
		}

		if(isActivated ^ active) {
	        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
	        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
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
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}

	@Override
	public S35PacketUpdateTileEntity getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);

		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	public void setRadius(int x, int y, int z) {
		RADIUS_X = Math.min(x, MAX_RADIUS);
		RADIUS_Y = Math.min(y, MAX_RADIUS);
		RADIUS_Z = Math.min(z, MAX_RADIUS);
	}

	public int[] getRadius() {
		return new int[] { RADIUS_X, RADIUS_Y, RADIUS_Z };
	}

	public Object[] getEntityList() {
		return entityList;
	}
}

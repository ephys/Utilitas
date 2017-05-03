package be.ephys.utilitas.feature.entity_sensor;

import be.ephys.utilitas.api.ILinkable;
import be.ephys.utilitas.base.helpers.ChatHelper;
import be.ephys.utilitas.base.helpers.NBTHelper;
import be.ephys.utilitas.base.helpers.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;
import java.util.UUID;

public class TileEntityProximitySensor extends TileEntity implements ILinkable, ITickable {

    private int radiusX = 3;
    private int radiusY = 3;
    private int radiusZ = 3;

    public static int MAX_RADIUS = 15;

    private int currentEntityCount = 0;

    private Class<? extends Entity> entityFilter = Entity.class;
    private UUID playerFilter = null;

    @Override
    public boolean link(EntityPlayer player, Object linkedObject) {

        if (linkedObject == null) {
            this.clearFilter(player);
            return true;
        }

        if (linkedObject instanceof EntityPlayer) {
            UUID newFilter = ((EntityPlayer) linkedObject).getUniqueID();

            if (this.playerFilter == newFilter) {
                this.clearFilter(player);
                return true;
            }

            this.playerFilter = newFilter;
            this.entityFilter = EntityPlayer.class;

            ITextComponent msg = new TextComponentTranslation("message.utilitas:sensor_binding_set_user", ((EntityPlayer) linkedObject).getDisplayName());
            ChatHelper.sendChatMessage(player, msg);

            return true;
        }

        if (linkedObject instanceof Entity) {

            @SuppressWarnings("unchecked")
            Class<? extends Entity> newFilter = (Class<? extends Entity>) linkedObject.getClass();

            if (this.entityFilter == newFilter) {
                this.clearFilter(player);
                return true;
            }

            this.playerFilter = null;
            this.entityFilter = newFilter;

            ITextComponent msg = new TextComponentTranslation("message.utilitas:sensor_binding_set_generic", ((Entity) linkedObject).getDisplayName());
            ChatHelper.sendChatMessage(player, msg);

            return true;
        }

        ChatHelper.sendChatMessage(player, new TextComponentTranslation("message.utilitas:sensor_binding_unsupported"));
        return false;
    }

    private void clearFilter(EntityPlayer player) {
        this.entityFilter = Entity.class;
        this.playerFilter = null;

        ChatHelper.sendChatMessage(player, new TextComponentTranslation("message.utilitas:sensor_binding_cleared"));
    }

    private static int clamp(int min, int max, int val) {
        return Math.max(Math.min(max, val), min);
    }

    public void updateRadius(EnumFacing side, EntityPlayer player) {
        int increase = player.isSneaking() ? -1 : 1;

        switch (side) {
            case UP:
            case DOWN:
                radiusY = clamp(0, MAX_RADIUS, radiusY + increase);
                break;

            case EAST:
            case WEST:
                radiusX = clamp(0, MAX_RADIUS, radiusX + increase);
                break;

            case NORTH:
            case SOUTH:
                radiusZ = clamp(0, MAX_RADIUS, radiusZ + increase);
                break;
        }

        ITextComponent msg = new TextComponentTranslation("message.utilitas:sensor_radius", radiusX, radiusY, radiusZ);
        ChatHelper.sendChatMessage(player, msg);
    }

    @Override
    public void update() {
        if (this.worldObj.getTotalWorldTime() % 10 != 0) {
            return;
        }

        AxisAlignedBB radius = new AxisAlignedBB(
            pos.getX() - radiusX,
            pos.getY() - radiusY,
            pos.getZ() - radiusZ,
            pos.getX() + radiusX,
            pos.getY() + radiusY,
            pos.getZ() + radiusZ
        );

        int entityCount = 0;
        if (this.entityFilter != null) {
            List entityList = this.worldObj.getEntitiesWithinAABB(entityFilter, radius);

            if (playerFilter == null) {
                entityCount = entityList.size();
            } else {
                for (Object o : entityList) {
                    if (o instanceof EntityPlayer) {
                        EntityPlayer entity = (EntityPlayer) o;

                        if (this.playerFilter.equals(entity.getGameProfile().getId())) {
                            entityCount = 1;
                            break;
                        }
                    }
                }
            }
        }

        if (entityCount != currentEntityCount) {
            // wasActive != isActive
            if (entityCount != 0 ^ currentEntityCount != 0) {
                worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockProximitySensor.POWERED, entityCount != 0), 3);
            }

            currentEntityCount = entityCount;

            this.worldObj.notifyNeighborsOfStateChange(getPos(), this.getBlockType());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.currentEntityCount = NBTHelper.getInt(nbt, "entity_count", this.currentEntityCount);
        this.entityFilter = (Class<? extends Entity>) NBTHelper.getClass(nbt, "entity_filter", null);
        this.playerFilter = NBTHelper.getUuid(nbt, "player_filter", null);

        if (nbt.hasKey("size")) {
            int[] size = nbt.getIntArray("size");
            this.radiusX = size[0];
            this.radiusY = size[1];
            this.radiusZ = size[2];
        }

        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("entity_count", currentEntityCount);
        nbt.setIntArray("size", new int[]{radiusX, radiusY, radiusZ});

        if (playerFilter != null) {
            NBTHelper.setUuid(nbt, "player_filter", playerFilter);
        }

        if (entityFilter != null) {
            NBTHelper.setClass(nbt, "entity_filter", entityFilter);
        }

        return super.writeToNBT(nbt);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    public int getEntityCount() {
        return currentEntityCount;
    }
}

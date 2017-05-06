package be.ephys.utilitas.feature.entity_sensor;

import be.ephys.utilitas.api.ILinkable;
import be.ephys.utilitas.base.helpers.ChatHelper;
import be.ephys.utilitas.base.syncable.Persist;
import be.ephys.utilitas.base.tile_entity.BaseTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class TileEntityProximitySensor extends BaseTileEntity implements ILinkable, ITickable {

    @Persist(name = "rx")
    private int radiusX = 3;

    @Persist(name = "xy")
    private int radiusY = 3;

    @Persist(name = "rz")
    private int radiusZ = 3;

    @Persist(name = "entity_count")
    private int currentEntityCount = 0;

    @Persist(name = "entity_filter")
    private Class<? extends Entity> entityFilter = Entity.class;

    @Persist(name = "player_filter")
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
        int maxRadius = getMaxRadius();

        switch (side) {
            case UP:
            case DOWN:
                radiusY = clamp(0, maxRadius, radiusY + increase);
                break;

            case EAST:
            case WEST:
                radiusX = clamp(0, maxRadius, radiusX + increase);
                break;

            case NORTH:
            case SOUTH:
                radiusZ = clamp(0, maxRadius, radiusZ + increase);
                break;
        }

        ITextComponent msg = new TextComponentTranslation("message.utilitas:sensor_radius", radiusX, radiusY, radiusZ);
        ChatHelper.sendChatMessage(player, msg);
    }

    @Override
    public void update() {
        if (worldObj.isRemote) {
            return;
        }

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

    public int getMaxRadius() {
        return FeatureProximitySensor.INSTANCE.maxRadius;
    }

    public int getEntityCount() {
        return currentEntityCount;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
}

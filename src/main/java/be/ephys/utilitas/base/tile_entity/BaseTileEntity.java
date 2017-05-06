package be.ephys.utilitas.base.tile_entity;

import be.ephys.utilitas.base.syncable.PersisterRegistry;
import be.ephys.utilitas.base.syncable.PersisterRegistry.Persister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class BaseTileEntity extends TileEntity {

    private final Persister persister = PersisterRegistry.getPersisterFor(this);

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = super.writeToNBT(compound);

        return this.persister.writeToNbt(this, tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.persister.readFromNbt(this, compound);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(
            this.getPos(),
            this.getBlockMetadata(),
            this.persister.writeDesynchronizedFieldsToUpdateTag(this, new NBTTagCompound())
        );
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);

        this.persister.readFromUpdateTag(this, pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();

        return this.persister.writeToUpdateTag(this, tag);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.persister.readFromUpdateTag(this, tag);
    }
}

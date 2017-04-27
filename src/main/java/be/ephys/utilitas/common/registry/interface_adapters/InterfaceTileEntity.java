package be.ephys.utilitas.common.registry.interface_adapters;

import be.ephys.utilitas.common.tileentity.TileEntityInterface;
import be.ephys.utilitas.common.util.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class InterfaceTileEntity extends UniversalInterfaceAdapter {

    private TileEntity blockEntity = null;

    private BlockPos tilePos = null;
    private int tileDim;

    public InterfaceTileEntity(TileEntityInterface tileEntity) {
        super(tileEntity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(int tickCount, double par1, double par3, double par5, float par7) {
        UniversalInterfaceAdapter.defaultRenderInventory(tickCount);
    }

    @Override
    public boolean setLink(Object link, EntityPlayer linker) {
        if (link instanceof TileEntity && (link instanceof IInventory || link instanceof IFluidHandler)) {
            this.blockEntity = (TileEntity) link;

            return true;
        }

        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("entity_pos", NBTUtil.createPosTag(tilePos));
        nbt.setInteger("entity_world", blockEntity.getWorld().provider.getDimension());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.tilePos = NBTUtil.getPosFromTag(nbt.getCompoundTag("entity_pos"));
        this.tileDim = nbt.getInteger("entity_world");
    }

    @Override
    public void onBlockUpdate() {}

    @Override
    public IInventory getInventory() {
        return blockEntity instanceof IInventory ? (IInventory) blockEntity : null;
    }

    @Override
    public boolean isNextTo(BlockPos pos) {
        return blockEntity != null && WorldHelper.areSideBySide(pos, blockEntity.getPos());
    }

    @Override
    public int getDimension() {
        return blockEntity == null ? 0 : blockEntity.getWorld().provider.getDimension();
    }

    @Override
    public void onTick(int tick) {
        if (isRemote()) {
            return;
        }

        if (blockEntity == null) {
            if (tilePos == null) {
                this.getInterface().unlink();
                return;
            }

            World world = WorldHelper.getWorldForDim(tileDim);
            this.blockEntity = world.getTileEntity(tilePos);
        }

        if (blockEntity == null || this.blockEntity.isInvalid()) {
            this.getInterface().unlink();
        }
    }

    @Override
    public ITextComponent getName() {
        return blockEntity.getDisplayName();
    }

    @Override
    public void validate() {}

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return blockEntity != null && blockEntity.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (blockEntity == null) {
            return null;
        }

        return blockEntity.getCapability(capability, facing);
    }
}

package be.ephys.utilitas.feature.universal_interface.interface_adapters;

import be.ephys.utilitas.api.registry.UniversalInterfaceAdapter;
import be.ephys.utilitas.base.helpers.WorldHelper;
import be.ephys.utilitas.base.helpers.WorldPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class InterfaceTileEntity extends UniversalInterfaceAdapter<TileEntity> {

    private TileEntity blockEntity = null;
    private WorldPos tilePos = null;

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(long tickCount, double par1, double par3, double par5, float par7) {
        if (blockEntity != null) {
            IBlockState state = blockEntity.getWorld().getBlockState(blockEntity.getPos());

            if (state.getRenderType() == EnumBlockRenderType.MODEL) {
                UniversalInterfaceAdapter.renderBlock(state, tickCount);
                return;
            }
        }

        UniversalInterfaceAdapter.defaultRenderInventory(tickCount);
    }

    @Override
    public boolean setLink(TileEntity link, EntityPlayer linker) {
        this.blockEntity = link;
        tilePos = new WorldPos(this.blockEntity.getWorld(), this.blockEntity.getPos());
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("pos", this.tilePos.writeToNbt());
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.tilePos = WorldPos.readFromNbt(nbt.getCompoundTag("pos"));

        System.out.println(tilePos);
    }

    @Override
    public void onBlockUpdate() {
    }

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
    public void onTick(long tick) {

        if (blockEntity == null) {
            if (tilePos == null) {
                this.getInterface().unlink();
                return;
            }

            this.blockEntity = tilePos.world.getTileEntity(tilePos.pos);
        }

        if (!isRemote()) {
            if (blockEntity == null || this.blockEntity.isInvalid()) {
                this.getInterface().unlink();
            }
        }
    }

    @Override
    public ITextComponent getName() {
        return blockEntity.getDisplayName();
    }

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

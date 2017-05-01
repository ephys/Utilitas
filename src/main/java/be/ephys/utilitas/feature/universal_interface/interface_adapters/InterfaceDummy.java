package be.ephys.utilitas.feature.universal_interface.interface_adapters;

import be.ephys.utilitas.api.registry.UniversalInterfaceAdapter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class InterfaceDummy extends UniversalInterfaceAdapter {
    public static final InterfaceDummy INSTANCE = new InterfaceDummy();

    private InterfaceDummy() {
        super(null);
    }

    @Override
    public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
    }

    @Override
    public boolean setLink(Object link, EntityPlayer linker) {
        return false;
    }

    @Override
    public ITextComponent getName() {
        return null;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
    }

    @Override
    public void onBlockUpdate() {
    }

    @Override
    public void onTick(int tick) {
    }

    @Override
    public IInventory getInventory() {
        return null;
    }

    @Override
    public boolean isNextTo(BlockPos pos) {
        return false;
    }

    @Override
    public int getDimension() {
        return 0;
    }

    @Override
    protected boolean isRemote() {
        throw new RuntimeException("should not be called");
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
    }
}

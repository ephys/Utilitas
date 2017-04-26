package be.ephys.utilitas.common.registry.interface_adapters;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class InterfaceDummy extends UniversalInterfaceAdapter {
    public static final InterfaceDummy INSTANCE = new InterfaceDummy();

    private InterfaceDummy() {
        super(null);
    }

    @Override
    public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {}

    @Override
    public boolean setLink(Object link, EntityPlayer linker) {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void validate() {}

    @Override
    public void writeToNBT(NBTTagCompound nbt) {}

    @Override
    public void readFromNBT(NBTTagCompound nbt) {}

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
    public IFluidHandler getFluidHandler() {
        return null;
    }

    @Override
    public boolean isNextTo(int xCoord, int yCoord, int zCoord) {
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
}

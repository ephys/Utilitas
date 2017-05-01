package be.ephys.utilitas.feature.universal_interface.interface_adapters;

import be.ephys.utilitas.api.registry.UniversalInterfaceAdapter;
import be.ephys.utilitas.base.helpers.EntityHelper;
import be.ephys.utilitas.base.helpers.NBTHelper;
import be.ephys.utilitas.feature.universal_interface.TileEntityInterface;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.UUID;

public class InterfaceMinecart extends UniversalInterfaceAdapter {

    private EntityMinecartContainer minecart;
    private UUID uuid;

    public InterfaceMinecart(TileEntityInterface tileEntity) {
        super(tileEntity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
        renderBlock(minecart.getDisplayTile().getBlock(), tickCount);
    }

    @Override
    public boolean setLink(Object link, EntityPlayer linker) {
        if (link instanceof EntityMinecartContainer) {
            minecart = (EntityMinecartContainer) link;

            return true;
        }

        return false;
    }

    @Override
    public ITextComponent getName() {
        return minecart.getDisplayName();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTHelper.setEntityUuid(nbt, "minecart", minecart);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        uuid = NBTHelper.getUuid(nbt, "minecart", null);
    }

    @Override
    public void onTick(int tick) {
        if (isRemote()) {
            return;
        }

        if (minecart == null && uuid != null) {
            minecart = (EntityMinecartContainer) EntityHelper.getEntityByUuid(uuid);
        }

        if (minecart == null || minecart.isDead) {
            getInterface().unlink();
        }
    }

    @Override
    public IInventory getInventory() {
        return minecart;
    }

    @Override
    public boolean isNextTo(BlockPos pos) {
        return false;
    }

    @Override
    public int getDimension() {
        return minecart == null ? 0 : minecart.worldObj.provider.getDimension();
    }

    @Override
    public void onBlockUpdate() {
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

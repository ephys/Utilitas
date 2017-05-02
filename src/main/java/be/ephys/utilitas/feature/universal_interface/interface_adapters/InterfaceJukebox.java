package be.ephys.utilitas.feature.universal_interface.interface_adapters;

import be.ephys.utilitas.api.registry.UniversalInterfaceAdapter;
import be.ephys.utilitas.base.helpers.ChatHelper;
import be.ephys.utilitas.base.helpers.WorldHelper;
import be.ephys.utilitas.base.helpers.WorldPos;
import be.ephys.utilitas.feature.universal_interface.TileEntityInterface;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class InterfaceJukebox extends UniversalInterfaceAdapter<TileEntityJukebox> {

    private JukeBoxProxy jukeboxProxy;
    private WorldPos tilePos;

    public InterfaceJukebox(TileEntityInterface tileEntity) {
        super(tileEntity);

        jukeboxProxy = new JukeBoxProxy();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
        renderBlock(Blocks.JUKEBOX, tickCount);
    }

    @Override
    public boolean setLink(TileEntityJukebox link, EntityPlayer linker) {
        jukeboxProxy.jukebox = link;
        tilePos = new WorldPos(jukeboxProxy.jukebox.getWorld(), jukeboxProxy.jukebox.getPos());

        return true;
    }

    @Override
    public ITextComponent getName() {
        return new TextComponentString(ChatHelper.getDisplayName(Blocks.JUKEBOX));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("jukebox", tilePos.writeToNbt());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        tilePos = WorldPos.readFromNbt(nbt.getCompoundTag("jukebox"));
    }

    @Override
    public void onBlockUpdate() {
    }

    @Override
    public void onTick(int tick) {
        if (isRemote()) {
            return;
        }

        if (jukeboxProxy.jukebox == null) {
            if (tilePos == null || tilePos.pos == null || tilePos.world == null) {
                this.getInterface().unlink();
                return;
            }

            TileEntity te = tilePos.world.getTileEntity(tilePos.pos);

            if (te instanceof TileEntityJukebox) {
                jukeboxProxy.jukebox = (TileEntityJukebox) te;
            } else {
                this.getInterface().unlink();
                return;
            }
        }

        if (jukeboxProxy.jukebox.isInvalid()) {
            jukeboxProxy.jukebox = null;
            this.getInterface().unlink();
        }
    }

    @Override
    public IInventory getInventory() {
        return jukeboxProxy.jukebox == null ? null : jukeboxProxy;
    }

    @Override
    public boolean isNextTo(BlockPos interfacePos) {
        return jukeboxProxy.jukebox != null && WorldHelper.areSideBySide(interfacePos, jukeboxProxy.jukebox.getPos());
    }

    @Override
    public int getDimension() {
        return jukeboxProxy.jukebox == null ? 0 : jukeboxProxy.jukebox.getWorld().provider.getDimension();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
    }

    public static class JukeBoxProxy implements IInventory {
        private TileEntityJukebox jukebox;
        private long insertTime = Integer.MIN_VALUE;

        @Override
        public int getSizeInventory() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot != 0) {
                return null;
            }

            return jukebox.getRecord();
        }

        @Override
        public ItemStack decrStackSize(int slot, int amount) {
            if (amount < 1) {
                return null;
            }

            return removeStackFromSlot(slot);
        }

        @Nullable
        @Override
        public ItemStack removeStackFromSlot(int slot) {
            if (slot != 0) {
                return null;
            }

            // this is a workaround to prevent the music from /not/ stopping when inserting/extracting too quickly
            if (jukebox.getWorld().getTotalWorldTime() - 3 < insertTime) {
                return null;
            }

            ItemStack previousStack = jukebox.getRecord();
            setInventorySlotContents(slot, null);

            return previousStack;
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            if (slot != 0) {
                return;
            }

            if (stack == null) {
                jukebox.getWorld().playEvent(1010, jukebox.getPos(), 0);
                jukebox.getWorld().playRecord(jukebox.getPos(), null);
                jukebox.setRecord(null);
                return;
            }

            if (!isItemValidForSlot(slot, stack)) {
                return;
            }

            BlockPos pos = jukebox.getPos();
            World world = jukebox.getWorld();

            ((BlockJukebox) Blocks.JUKEBOX).insertRecord(world, pos, world.getBlockState(pos), stack);

            jukebox.getWorld().playRecord(jukebox.getPos(), ((ItemRecord) stack.getItem()).getSound());
            jukebox.getWorld().playEvent(null, 1010, jukebox.getPos(), Item.getIdFromItem(stack.getItem()));

            insertTime = jukebox.getWorld().getTotalWorldTime();
        }

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            return stack != null && slot == 0 && (stack.getItem() instanceof ItemRecord);
        }

        @Override
        public int getField(int id) {
            return 0;
        }

        @Override
        public void setField(int id, int value) {
        }

        @Override
        public int getFieldCount() {
            return 0;
        }

        @Override
        public void clear() {
        }

        @Override
        public int getInventoryStackLimit() {
            return 1;
        }

        @Override
        public void markDirty() {
            jukebox.markDirty();
        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
            return true;
        }

        @Override
        public void openInventory(EntityPlayer player) {
        }

        @Override
        public void closeInventory(EntityPlayer player) {
        }

        @Override
        public String getName() {
            return Blocks.JUKEBOX.getLocalizedName();
        }

        @Override
        public boolean hasCustomName() {
            return false;
        }

        @Override
        public ITextComponent getDisplayName() {
            return jukebox.getDisplayName();
        }
    }
}

package be.ephys.utilitas.feature.universal_interface.interface_adapters;

import be.ephys.utilitas.api.registry.UniversalInterfaceAdapter;
import be.ephys.utilitas.base.helpers.ChatHelper;
import be.ephys.utilitas.base.helpers.NBTHelper;
import be.ephys.utilitas.base.helpers.WorldHelper;
import be.ephys.utilitas.feature.universal_interface.TileEntityInterface;
import net.minecraft.block.BlockJukebox;
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
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class InterfaceJukebox extends UniversalInterfaceAdapter {

    private JukeBoxProxy jukeboxProxy;
    private BlockPos tilePos;
    private int tileDim;

    public InterfaceJukebox(TileEntityInterface tileEntity) {
        super(tileEntity);

        jukeboxProxy = new JukeBoxProxy();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
        GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);

        renderBlock(Blocks.JUKEBOX);
    }

    @Override
    public boolean setLink(Object link, EntityPlayer linker) {
        if (link instanceof BlockJukebox.TileEntityJukebox) {
            jukeboxProxy.jukebox = (BlockJukebox.TileEntityJukebox) link;

            return true;
        }

        return false;
    }

    @Override
    public ITextComponent getName() {
        return new TextComponentString(ChatHelper.getDisplayName(Blocks.JUKEBOX));
    }

    @Override
    public void validate() {
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTHelper.setBlockPos(nbt, "entity_pos", jukeboxProxy.jukebox.getPos());
        nbt.setInteger("entity_dim", jukeboxProxy.jukebox.getWorld().provider.getDimension());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        tilePos = NBTHelper.getBlockPos(nbt, "entity_pos", null);
        tileDim = NBTHelper.getInt(nbt, "entity_dim", 0);
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
            if (tilePos == null) {
                this.getInterface().unlink();
                return;
            }

            World world = WorldHelper.getWorldForDim(tileDim);
            TileEntity te = world.getTileEntity(tilePos);

            if (te instanceof BlockJukebox.TileEntityJukebox) {
                jukeboxProxy.jukebox = (BlockJukebox.TileEntityJukebox) te;
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
        private BlockJukebox.TileEntityJukebox jukebox;
        private long insertTime = Integer.MIN_VALUE;

        @Override
        public int getSizeInventory() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot != 0) return null;

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
            setInventorySlotContents(0, null);

            return previousStack;
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            if (!isItemValidForSlot(slot, stack)) {
                return;
//                jukebox.setRecord(stack);
            }

            BlockPos pos = jukebox.getPos();
            World world = jukebox.getWorld();

            ((BlockJukebox) Blocks.JUKEBOX).insertRecord(world, pos, world.getBlockState(pos), stack);
            jukebox.getWorld().playEvent(null, 1005, jukebox.getPos(), Item.getIdFromItem(stack.getItem()));

            insertTime = jukebox.getWorld().getTotalWorldTime();
        }

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            // TODO figure out a way to support more records
            return slot == 0 && (stack.getItem() instanceof ItemRecord);
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

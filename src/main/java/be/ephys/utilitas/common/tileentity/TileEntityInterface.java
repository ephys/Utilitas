package be.ephys.utilitas.common.tileentity;

import be.ephys.utilitas.common.Utilitas;
import be.ephys.utilitas.common.item.IInterfaceUpgrade;
import be.ephys.utilitas.common.item.ItemLinker;
import be.ephys.utilitas.common.registry.UniversalInterfaceRegistry;
import be.ephys.utilitas.common.registry.interface_adapters.InterfaceDummy;
import be.ephys.utilitas.common.registry.interface_adapters.UniversalInterfaceAdapter;
import be.ephys.utilitas.common.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class TileEntityInterface extends TileEntity implements ISidedInventory, ITickable {

    private UniversalInterfaceAdapter activeAdapter = InterfaceDummy.INSTANCE;
    private ItemStack[] upgrades = new ItemStack[5];

    private boolean isFluidHandler = false;
    private boolean isWireless = false;
    private boolean worksCrossDim = false;

    public int tick = 0;

    public UniversalInterfaceAdapter getAdapter() {
        return activeAdapter;
    }

    public void onBlockUpdate() {
        this.activeAdapter.onBlockUpdate();
    }

    private IInventory getInventory() {
        if (!isInRange()) {
            return null;
        }

        return activeAdapter.getInventory();
    }

    public boolean isInRange() {
        if (activeAdapter == null) {
            return false;
        }

        if (!worksCrossDim() && activeAdapter.getDimension() != worldObj.provider.getDimension()) {
            return false;
        }

        if (!isWireless() && !activeAdapter.isNextTo(getPos())) {
            return false;
        }

        return true;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);

        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);

        nbt.setInteger("tick", tick);

        nbt.setBoolean("isFluidHandler", isFluidHandler);
        nbt.setBoolean("isWireless", isWireless);
        nbt.setBoolean("worksCrossDim", worksCrossDim);

        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i] == null) continue;

            NBTTagCompound upgradeNBT = new NBTTagCompound();
            upgrades[i].writeToNBT(upgradeNBT);

            nbt.setTag("upgrade_" + i, upgradeNBT);
        }

        if (this.activeAdapter != null) {
            NBTHelper.setClass(nbt, "handler", this.activeAdapter.getClass());
            this.activeAdapter.writeToNBT(nbt);
        }

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        tick = NBTHelper.getInt(nbt, "tick", 0);

        isFluidHandler = NBTHelper.getBoolean(nbt, "isFluidHandler", isFluidHandler);
        isWireless = NBTHelper.getBoolean(nbt, "isWireless", isWireless);
        worksCrossDim = NBTHelper.getBoolean(nbt, "worksCrossDim", worksCrossDim);

        for (int i = 0; i < upgrades.length; i++) {
            if (nbt.hasKey("upgrade_" + i)) {
                upgrades[i] = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("upgrade_" + i));
            } else {
                upgrades[i] = null;
            }
        }

        @SuppressWarnings("unchecked")
        Class<? extends UniversalInterfaceAdapter> clazz = (Class<? extends UniversalInterfaceAdapter>) NBTHelper.getClass(nbt, "handler");

        if (clazz != null && UniversalInterfaceRegistry.hasHandler(clazz)) {
            try {
                this.activeAdapter = clazz.getConstructor(TileEntityInterface.class).newInstance(this);
                this.activeAdapter.readFromNBT(nbt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.activeAdapter = null;
        }
    }

    @Override
    public void validate() {
        super.validate();

        if (this.activeAdapter != null) this.activeAdapter.validate();
    }

    public void unlink() {
        this.activeAdapter = null;

        WorldHelper.markTileForUpdate(this);
    }


    @Override
    public void update() {
        if (this.activeAdapter == null) {
            return;
        }

        this.activeAdapter.onTick(tick++);
    }

    public boolean addUpgrade(ItemStack heldItem, EntityPlayer player) {
        if (heldItem == null || !(heldItem.getItem() instanceof IInterfaceUpgrade)) {
            return false;
        }

        IInterfaceUpgrade upgrade = (IInterfaceUpgrade) heldItem.getItem();

        // TODO replace with GUI
        int upgradeSlot = hasUpgrade(heldItem);
        if (upgradeSlot != -1) {
            ItemStack stack = upgrades[upgradeSlot];
            upgrades[upgradeSlot] = null;

            upgrade.onRemove(this, player, stack);

            InventoryHelper.dropItem(stack, player);
        } else {
            upgradeSlot = getEmptySlot();

            if (upgradeSlot == -1) return false;

            if (!upgrade.onInsert(this, player, heldItem)) return false;

            ItemStack stack = heldItem.copy();
            stack.stackSize = 1;
            upgrades[upgradeSlot] = stack;

            heldItem.stackSize--;
        }

        WorldHelper.markTileForUpdate(this);

        return true;
    }

    public int hasUpgrade(ItemStack upgrade) {
        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i] != null && upgrades[i].isItemEqual(upgrade))
                return i;
        }

        return -1;
    }

    private int getEmptySlot() {
        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i] == null) return i;
        }

        return -1;
    }

    public boolean link(EntityPlayer player) {
        if (activeAdapter != null) {
            unlink();
            ChatHelper.sendChatMessage(player, "Interface unlinked");

            return true;
        }

        Object toLink = getLinkableEntity(player);
        UniversalInterfaceAdapter handler = UniversalInterfaceRegistry.getHandler(toLink, this, player);

        if (handler == null) {
            ChatHelper.sendChatMessage(player, "Could not bind the universal interface.");
            return false;
        }

        this.activeAdapter = handler;

        ITextComponent msg = new TextComponentString("Universal interface linked to").appendSibling(handler.getName());
        player.addChatComponentMessage(msg);

        WorldHelper.markTileForUpdate(this);

        return true;
    }

    private Object getLinkableEntity(EntityPlayer player) {
        ItemStack mainItem = player.getHeldItem(EnumHand.MAIN_HAND);

        if (mainItem == null) {
            return player;
        }

        if (mainItem.getItem().equals(Utilitas.Items.linkDevice)) {
            Object toLink = ItemLinker.getLinkedObject(mainItem, worldObj);

            if (toLink == null) {
                ChatHelper.sendChatMessage(player, "Link wand not bound");
                return null;
            }

            if (toLink instanceof TileEntityInterface) {
                ChatHelper.sendChatMessage(player, "Cannot link to another Universal Interface.");
                return null;
            }

            return toLink;
        }

        return null;
    }

    // ================================================================================
    // IInventory interface
    // ================================================================================
    @Override
    public int getSizeInventory() {
        IInventory linkedInventory = this.getInventory();
        return (linkedInventory != null) ? linkedInventory.getSizeInventory()
            : 0;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        IInventory linkedInventory = this.getInventory();

        if (linkedInventory == null)
            return null;

        return linkedInventory.getStackInSlot(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        IInventory linkedInventory = this.getInventory();

        if (linkedInventory == null)
            return null;

        return linkedInventory.decrStackSize(i, j);
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        IInventory linkedInventory = this.getInventory();
        if (linkedInventory != null)
            linkedInventory.setInventorySlotContents(i, itemstack);
    }

    @Override
    public int getInventoryStackLimit() {
        IInventory linkedInventory = this.getInventory();
        return (linkedInventory != null) ? linkedInventory.getInventoryStackLimit() : 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        IInventory linkedInventory = this.getInventory();

        return linkedInventory != null && linkedInventory.isItemValidForSlot(i, itemstack);
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

    // ================================================================================
    // ISidedInventory interface
    // ================================================================================

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        IInventory linkedInventory = this.getInventory();

        if (linkedInventory == null) {
            return new int[0];
        }

        if (linkedInventory instanceof ISidedInventory) {
            return ((ISidedInventory) linkedInventory).getSlotsForFace(side);
        }

        return InventoryHelper.getUnSidedInventorySlots(linkedInventory);
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        IInventory linkedInventory = this.getInventory();

        if (linkedInventory == null) {
            return false;
        }

        if (linkedInventory instanceof ISidedInventory) {
            return ((ISidedInventory) linkedInventory).canInsertItem(index, itemStackIn, direction);
        }

        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        IInventory linkedInventory = this.getInventory();

        if (linkedInventory == null) {
            return false;
        }

        if (linkedInventory instanceof ISidedInventory) {
            return ((ISidedInventory) linkedInventory).canExtractItem(index, stack, direction);
        }

        return true;
    }

    @Override
    public String getName() {
        IInventory inv = this.getInventory();

        if (inv == null) {
            return null;
        }

        return inv.getName();
    }

    @Override
    public boolean hasCustomName() {
        IInventory inv = this.getInventory();

        if (inv == null) {
            return false;
        }

        return inv.hasCustomName();
    }

    // ================================================================================
    // ICapabilityProvider interface
    // ================================================================================

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return this.isInRange() && this.getAdapter().hasCapability(capability, facing);

    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (!this.isInRange()) {
            return null;
        }

        return this.getAdapter().getCapability(capability, facing);
    }

    // ================================================================================
    // Upgrade Getter/Setters
    // ================================================================================

    public void setIsFluidHandler(boolean b) {
        isFluidHandler = b;
    }

    public void setWorksCrossDim(boolean b) {
        worksCrossDim = b;
    }

    public void setWireless(boolean b) {
        isWireless = b;
    }

    public boolean worksCrossDim() {
        return worksCrossDim;
    }

    public boolean isWireless() {
        return isWireless;
    }

    public boolean isFluidHandler() {
        return isFluidHandler;
    }
}

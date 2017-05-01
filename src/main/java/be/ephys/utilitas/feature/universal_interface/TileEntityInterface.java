package be.ephys.utilitas.feature.universal_interface;

import be.ephys.utilitas.api.IInterfaceUpgrade;
import be.ephys.utilitas.api.ILinkable;
import be.ephys.utilitas.api.registry.UniversalInterfaceAdapter;
import be.ephys.utilitas.api.registry.UniversalInterfaceRegistry;
import be.ephys.utilitas.base.helpers.ChatHelper;
import be.ephys.utilitas.base.helpers.InventoryHelper;
import be.ephys.utilitas.base.helpers.NBTHelper;
import be.ephys.utilitas.base.helpers.WorldHelper;
import be.ephys.utilitas.feature.link_wand.ItemLinker;
import be.ephys.utilitas.feature.universal_interface.interface_adapters.InterfaceDummy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.tools.nsc.transform.patmat.Interface;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityInterface extends TileEntity implements ISidedInventory, ITickable, ILinkable {

    @Nonnull
    private UniversalInterfaceAdapter activeAdapter = InterfaceDummy.INSTANCE;
    protected ItemStack[] upgrades = new ItemStack[5];

    private boolean isFluidHandler = false;
    private boolean isWireless = false;
    private boolean worksCrossDim = false;

    public int tick = 0;

    @Nonnull
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
        if (!worksCrossDim && activeAdapter.getDimension() != worldObj.provider.getDimension()) {
            return false;
        }

        if (!isWireless && !activeAdapter.isNextTo(getPos())) {
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

        if (this.activeAdapter != InterfaceDummy.INSTANCE) {
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
            this.activeAdapter = InterfaceDummy.INSTANCE;
        }
    }

    @Override
    public void validate() {
        super.validate();
        this.activeAdapter.validate();
    }

    public void unlink() {
        this.activeAdapter = InterfaceDummy.INSTANCE;

        WorldHelper.markTileForUpdate(this);
    }


    @Override
    public void update() {
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

            if (upgradeSlot == -1) {
                return false;
            }

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

    @Override
    public boolean link(EntityPlayer player, Object linkedObject) {
        if (activeAdapter != InterfaceDummy.INSTANCE || linkedObject == null) {
            unlink();
            ChatHelper.sendChatMessage(player, new TextComponentTranslation("message.utilitas:interface_unbound"));

            return true;
        }

        if (linkedObject instanceof ItemLinker.WorldPos) {
            ItemLinker.WorldPos worldPos = (ItemLinker.WorldPos) linkedObject;

            TileEntity te = worldPos.world.getTileEntity(worldPos.pos);
            if (te != null) {
                linkedObject = te;
            }
        }

        if (linkedObject instanceof TileEntityInterface) {
            ChatHelper.sendChatMessage(player, new TextComponentTranslation("message.utilitas:interface_binding_denied"));
            return false;
        }

        UniversalInterfaceAdapter handler = UniversalInterfaceRegistry.getHandler(linkedObject, this, player);

        if (handler == null) {
            ChatHelper.sendChatMessage(player, new TextComponentTranslation("message.utilitas:interface_binding_unsupported"));
            return false;
        }

        ChatHelper.sendChatMessage(player, new TextComponentTranslation("message.utilitas:interface_bound", handler.getName()));

        this.activeAdapter = handler;
        WorldHelper.markTileForUpdate(this);

        return true;
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

        if (!this.isInRange()) {
            return false;
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && !this.isFluidHandler) {
            return false;
        }

        return this.getAdapter().hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (!this.isInRange()) {
            return null;
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && !this.isFluidHandler) {
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
}

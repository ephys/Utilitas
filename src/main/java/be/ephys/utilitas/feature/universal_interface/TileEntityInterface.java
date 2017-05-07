package be.ephys.utilitas.feature.universal_interface;

import be.ephys.utilitas.api.IInterfaceUpgrade;
import be.ephys.utilitas.api.ILinkable;
import be.ephys.utilitas.api.registry.UniversalInterfaceAdapter;
import be.ephys.utilitas.api.registry.UniversalInterfaceRegistry;
import be.ephys.utilitas.base.helpers.ChatHelper;
import be.ephys.utilitas.base.helpers.InventoryHelper;
import be.ephys.utilitas.base.helpers.WorldHelper;
import be.ephys.utilitas.base.helpers.WorldPos;
import be.ephys.utilitas.base.syncable.Persist;
import be.ephys.utilitas.base.syncable.Sync;
import be.ephys.utilitas.base.tile_entity.BaseTileEntity;
import be.ephys.utilitas.feature.universal_interface.interface_adapters.InterfaceDummy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityInterface extends BaseTileEntity implements ISidedInventory, ITickable, ILinkable {

    @Nonnull
    @Sync
    @Persist(name = "adapter", serializer = AdapterSerializer.class)
    private UniversalInterfaceAdapter activeAdapter = InterfaceDummy.INSTANCE;

    @Persist(name = "upgrades")
    protected ItemStack[] upgrades = new ItemStack[5];

    @Persist(name = "is_fluid_handler")
    private boolean isFluidHandler = false;

    @Persist(name = "is_wireless")
    private boolean isWireless = false;

    @Persist(name = "is_cross_dim")
    private boolean worksCrossDim = false;

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

    public void unlink() {
        this.activeAdapter = InterfaceDummy.INSTANCE;

        WorldHelper.markTileForUpdate(this);
    }

    @Override
    public void update() {
        long tick = FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter();

        this.activeAdapter.onTick(tick);
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

            upgrade.onRemove(this, stack);

            InventoryHelper.dropItem(stack, player);
        } else {
            upgradeSlot = getEmptySlot();

            if (upgradeSlot == -1) {
                return false;
            }

            if (!upgrade.onInsert(this, heldItem)) {
                return false;
            }

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

        if (linkedObject instanceof WorldPos) {
            WorldPos worldPos = (WorldPos) linkedObject;

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

        if (linkedInventory == null) {
            return 0;
        }

        return linkedInventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        IInventory linkedInventory = this.getInventory();

        if (linkedInventory == null) {
            return null;
        }

        return linkedInventory.getStackInSlot(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        IInventory linkedInventory = this.getInventory();

        if (linkedInventory == null) {
            return null;
        }

        return linkedInventory.decrStackSize(i, j);
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        IInventory linkedInventory = this.getInventory();

        if (linkedInventory != null) {
            return linkedInventory.removeStackFromSlot(index);
        }

        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        IInventory linkedInventory = this.getInventory();

        if (linkedInventory != null) {
            linkedInventory.setInventorySlotContents(i, itemstack);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        IInventory linkedInventory = this.getInventory();

        if (linkedInventory == null) {
            return 0;
        }

        return linkedInventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
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

    @Override
    protected void postUpdate() {
        this.activeAdapter.setInterface(this);
        this.activeAdapter.onLoad();
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

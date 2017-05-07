package be.ephys.utilitas.feature.reconditioner;

import be.ephys.utilitas.base.helpers.InventoryHelper;
import be.ephys.utilitas.base.helpers.WorldHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentMending;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.List;

// TODO render the item with particles flowing into it as it repairs (item should not turn nor have particles if the device is inactive)
// TODO drop xp when broken
// TODO drop inventory
// TODO mana/energy support
public class TileEntityReconditioner extends TileEntity implements ITickable, IInventory {

    private static final EnchantmentMending ENCHANTMENT_MENDING = (EnchantmentMending) Enchantment.getEnchantmentByLocation("mending");
    public static final int MAX_XP_STORAGE = 100;
    private static final int XP_RADIUS = 3;

    public static final int SLOT_ENCHANTED_BOOK = 0;
    public static final int SLOT_REPAIRABLE_ITEM = 1;

    public static final int FIELD_XP = 0;

    private final IInventory inventory = new InventoryBasic(null, false, 2);
    private final IItemHandler inventoryCapability = new InvWrapper(this);
    private int xpCount;

    private int repairTime = 0;

    @SideOnly(Side.CLIENT)
    public int getRepairTime() {
        return repairTime;
    }

    public boolean isRunning() {
        if (xpCount <= 0) {
            return false;
        }

        if (inventory.getStackInSlot(SLOT_ENCHANTED_BOOK) == null) {
            return false;
        }

        ItemStack item = inventory.getStackInSlot(SLOT_REPAIRABLE_ITEM);
        if (item == null || !item.isItemDamaged()) {
            return false;
        }

        return true;
    }

    @Override
    public void update() {
        long tick = FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter();

        if (tick % 20 == 0) {
            tryRepairing();
        }

        if (tick % 40 == 0) {
            tryGrabbingXp();
        }

        if (worldObj.isRemote && isRunning()) {
            repairTime++;
        }
    }

    private void tryGrabbingXp() {
        if (xpCount > MAX_XP_STORAGE) {
            return;
        }

        AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - XP_RADIUS, pos.getY() - XP_RADIUS, pos.getZ() - XP_RADIUS, pos.getX() + XP_RADIUS, pos.getY() + XP_RADIUS, pos.getZ() + XP_RADIUS);
        List<EntityXPOrb> xpOrbs = worldObj.getEntitiesWithinAABB(EntityXPOrb.class, bb);

        for (EntityXPOrb xpOrb : xpOrbs) {
            xpCount += xpOrb.getXpValue();
            xpOrb.setDead();
        }

        WorldHelper.markTileForUpdate(this);
    }

    private void tryRepairing() {
        if (!isRunning()) {
            return;
        }

        ItemStack item = inventory.getStackInSlot(SLOT_REPAIRABLE_ITEM);

        int addedRepairability = Math.min(item.getItemDamage(), xpToDurability(1));
        this.xpCount -= durabilityToXp(addedRepairability);

        item.setItemDamage(item.getItemDamage() - addedRepairability);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("xp", xpCount);
        compound.setTag("inventory", InventoryHelper.toNbt(this.inventory));

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        xpCount = compound.getInteger("xp");
        InventoryHelper.fromNbt(this.inventory, compound.getTagList("inventory", 10));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), writeToNBT(new NBTTagCompound()));
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Nullable
    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory.getStackInSlot(index);
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int index, int count) {
        WorldHelper.markTileForUpdate(this);

        return inventory.decrStackSize(index, count);
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        WorldHelper.markTileForUpdate(this);

        return inventory.removeStackFromSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        WorldHelper.markTileForUpdate(this);

        inventory.setInventorySlotContents(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        inventory.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        inventory.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack == null) {
            return true;
        }

        if (index == SLOT_ENCHANTED_BOOK) {
            return isMendingBook(stack);
        }

        if (index == SLOT_REPAIRABLE_ITEM) {
            return canRepair(stack);
        }

        return false;
    }

    @Override
    public int getField(int id) {
        if (id == FIELD_XP) {
            return xpCount;
        }

        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 1;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inventory.hasCustomName();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) inventoryCapability;
        }

        return super.getCapability(capability, facing);
    }

    private static boolean isMendingBook(ItemStack stack) {
        return stack.getItem() == Items.ENCHANTED_BOOK && InventoryHelper.getBookEnchantmentLevel(ENCHANTMENT_MENDING, stack) >= 1;
    }

    public static boolean canRepair(ItemStack stack) {
        return ENCHANTMENT_MENDING.canApply(stack);
    }

    /**
     * Thanks for writing this in an instance method, mojang.
     * @see EntityXPOrb#xpToDurability(int)
     */
    public static int xpToDurability(int xpCount) {
        return xpCount * 2;
    }

    /**
     * Thanks for writing this in an instance method, mojang.
     * @see EntityXPOrb#durabilityToXp(int)
     */
    public static int durabilityToXp(int durability) {
        return durability / 2;
    }
}

package be.ephys.utilitas.base.helpers;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class InventoryHelper {

    private static final Random RANDOM = new Random();

    public static int[] getUnSidedInventorySlots(IInventory inventory) {
        int[] slots = new int[inventory.getSizeInventory()];

        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }

        return slots;
    }

    public static void dropContents(TileEntity te) {
        if (!(te instanceof IInventory)) {
            throw new RuntimeException("tile must be an IIventory");
        }

        dropContents((IInventory) te, te.getWorld(), te.getPos());
    }

    public static void dropContents(IInventory te, World world, BlockPos pos) {
        for (int i = 0; i < te.getSizeInventory(); ++i) {
            ItemStack itemstack = te.removeStackFromSlot(i);

            dropItem(itemstack, world, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public static void dropItem(ItemStack itemstack, World world, double x, double y, double z) {
        if (itemstack == null) {
            return;
        }

        float randX = RANDOM.nextFloat() * 0.8F + 0.1F;
        float randY = RANDOM.nextFloat() * 0.8F + 0.1F;
        float randZ = RANDOM.nextFloat() * 0.8F + 0.1F;

        while (itemstack.stackSize > 0) {
            int k1 = RANDOM.nextInt(21) + 10;

            if (k1 > itemstack.stackSize) {
                k1 = itemstack.stackSize;
            }

            itemstack.stackSize -= k1;
            EntityItem entityitem = new EntityItem(world,
                x + randX,
                y + randY,
                z + randZ,
                new ItemStack(itemstack.getItem(), k1,
                    itemstack.getItemDamage())
            );

            float f3 = 0.05F;
            entityitem.motionX = RANDOM.nextGaussian() * f3;
            entityitem.motionY = RANDOM.nextGaussian() * f3 + 0.2F;
            entityitem.motionZ = RANDOM.nextGaussian() * f3;

            if (itemstack.hasTagCompound()) {
                entityitem.getEntityItem().setTagCompound(itemstack.getTagCompound().copy());
            }

            world.spawnEntityInWorld(entityitem);
        }
    }

    public static void dropItem(ItemStack itemstack, EntityPlayer player) {
        dropItem(itemstack, player.getEntityWorld(), player.posX, player.posY, player.posZ);
    }

//    public static IInventory getInventoryAt(World world, double x, double y, double z) {
//        IInventory inventory = getBlockInventoryAt(world, (int) x, (int) y, (int) z);
//
//        if (inventory == null) {
//            inventory = getEntityInventoryAt(world, x, y, z);
//        }
//
//        return inventory;
//    }
//
//    public static IInventory getBlockInventoryAt(World world, int x, int y, int z) {
//        // special mojang bad code hotfix yay
//        Block block = world.getBlock(x, y, z);
//        if (world.getBlock(x, y, z) instanceof BlockChest) {
//            IInventory chestInventory = ((BlockChest) block).func_149951_m(world, x, y, z);
//
//            if (chestInventory != null) {
//                return chestInventory;
//            }
//        }
//
//        TileEntity te = world.getTileEntity(x, y, z);
//
//        if (te instanceof IInventory) {
//            return (IInventory) te;
//        }
//
//        return null;
//    }
//
//    // todo: check if the AABB is valid
//    @SuppressWarnings("unchecked")
//    public static IInventory getEntityInventoryAt(World world, double x, double y, double z) {
//        List<IInventory> entities = world.selectEntitiesWithinAABB(Entity.class,
//            AxisAlignedBB
//                .getBoundingBox(x, y,
//                    z, x,
//                    y,
//                    z),
//            IEntitySelector.selectInventories);
//
//        return (IInventory) MathHelper.getRandom(entities);
//    }
//
//    public static boolean isBlockEqual(ItemStack stack, World world, int x, int y, int z) {
//        return world.getBlock(x, y, z).equals(Block.getBlockFromItem(stack.getItem()))
//            && world.getBlockMetadata(x, y, z) == stack.getItemDamage();
//    }
//
//    public static boolean isBlockEqual(String oredictName, World world, int x, int y, int z) {
//        int needle = OreDictionary.getOreID(oredictName);
//
//        ItemStack stack = new ItemStack(world.getBlock(x, y, z), 1, world.getBlockMetadata(x, y, z));
//
//        int[] haystack = OreDictionary.getOreIDs(stack);
//
//        for (int id : haystack) {
//            if (id == needle) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    public static boolean itemIsOre(ItemStack stack) {
//        int[] ids = OreDictionary.getOreIDs(stack);
//
//        for (int id : ids) {
//            if (OreDictionary.getOreName(id).startsWith("ore")) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    public static void ensureOreIsRegistered(String oreName, ItemStack is) {
//        int ids[] = OreDictionary.getOreIDs(is);
//
//        for (int id : ids) {
//            if (OreDictionary.getOreName(id).equals(oreName)) {
//                return;
//            }
//        }
//
//        OreDictionary.registerOre(oreName, is);
//    }
//
//    /**
//     * @see EntityHelper#getClosestEntity(Class, World, double, double, double, float, IFilter)
//     */
//    @SuppressWarnings("unchecked")
//    public static EntityItem getClosestItem(World world, double x, double y, double z, float range,
//                                            IFilter<EntityItem> filter) {
//        return EntityHelper.getClosestEntity(EntityItem.class, world, x, y, z, range, filter);
//    }
}

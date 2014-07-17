package nf.fr.ephys.playerproxies.helpers;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.List;
import java.util.Random;

public class BlockHelper {
	public static Random random = new Random();

	private static final int OPPOSITE_SIDES[] = { 1, 0, 3, 2, 5, 4 };

	public static int removeItemRecipe(ItemStack stack) {
		List crafts = CraftingManager.getInstance().getRecipeList();

		int nbRemoved = 0;
		for (int i = 0; i < crafts.size(); i++) {
			if (crafts.get(i) instanceof IRecipe) {
				ItemStack output = ((IRecipe) crafts.get(i)).getRecipeOutput();

				if (output == null) continue;

				if (output.isItemEqual(stack)) {
					crafts.remove(i);
					nbRemoved++;
				}
			}
		}

		return nbRemoved;
	}

	public static boolean insert(IInventory inventory, ItemStack stack) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.isItemValidForSlot(i, stack)) {
				inventory.setInventorySlotContents(i, stack);
				return true;
			}
		}

		return false;
	}

	public static void setBiome(World world, int x, int z, int biomeID) {
		Chunk chunk = world.getChunkFromBlockCoords(x, z);
		byte[] biomes = chunk.getBiomeArray();
		biomes[(z & 15) << 4 | (x & 15)] = (byte) (biomeID - 128);

		chunk.setBiomeArray(biomes);
		chunk.setChunkModified();
	}

	public static int getTopYCoord(World world, int x, int z) {
		int y = 63;

		while (!world.getBlock(x, 63, z).equals(Blocks.air)) {
			y++;
		}

		return y;
	}

	public static boolean isUnbreakable(Block block, World world, int x, int y, int z) {
		return block.getBlockHardness(world, x, y, z) < 0;
	}

	/**
	 * returns the opposite side of side
	 * @param side  the side to oppose ?
	 * @return      the opposite side
	 */
	public static int getOppositeSide(int side) {
		return OPPOSITE_SIDES[side];
	}

	public static int[] getAdjacentBlock(int x, int y, int z, int side) {
		/*switch (side) {
			case 0:
				y--;
				break;
			case 1:
				y++;
				break;
			case 2:
				z--;
				break;
			case 3:
				z++;
				break;
			case 4:
				x--;
				break;
			case 5:
				x++;
				break;
		}*/

		// I'm a mad man

		if ((side & 0b100) == 0b100)
			x += ((side & 0b001) == 0) ? -1 : 1;
		else if ((side & 0b010) == 0b010)
			z += ((side & 0b001) == 0) ? -1 : 1;
		else
			y += ((side & 0b001) == 0) ? -1 : 1;

		return new int[] {x, y, z};
	}

	public static int[] getAdjacentBlock(int[] coords, int side) {
		return getAdjacentBlock(coords[0], coords[1], coords[2], side);
	}

	public static String getDisplayName(TileEntity te) {
		if (te == null) return "null";
		return new ItemStack(te.getBlockType(), 1, te.getBlockMetadata()).getDisplayName();
	}

	public static Vec3 relativePos(TileEntity te, Entity e) {
		return Vec3.createVectorHelper(te.xCoord - e.posX, te.yCoord - e.posY, te.zCoord - e.posZ);
	}

	public static Vec3 relativePos(Entity e, TileEntity te) {
		return Vec3.createVectorHelper(e.posX - te.xCoord, e.posY - te.yCoord, e.posZ - te.zCoord);
	}

	public static Vec3 relativePos(TileEntity te1, TileEntity te2) {
		return Vec3.createVectorHelper(te1.xCoord - te2.xCoord, te1.yCoord - te2.yCoord, te1.zCoord - te2.zCoord);
	}

	public static Vec3 relativePos(Entity e1, Entity e2) {
		return Vec3.createVectorHelper(e1.posX - e2.posX, e1.posY - e2.posY, e1.posZ - e2.posZ);
	}

	public static int[] getCoords(TileEntity te) {
		return new int[] { te.xCoord, te.yCoord, te.zCoord };
	}

	public static double[] getCoords(Entity e) {
		return new double[] { e.posX, e.posY, e.posZ };
	}

	public static int orientationToMetadataXZ(double rotationYaw) {
	    int l = MathHelper.floor_double((rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	    return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
	}

	public static void insertItem(IInventory inventory, ItemStack stack) {
		insertItem(inventory, stack, -1);
	}

	public static boolean insertItem(IInventory inventory, ItemStack stack, int side) {
		boolean isSided = inventory instanceof ISidedInventory;

		int[] accessibleSlots = isSided ? ((ISidedInventory) inventory).getAccessibleSlotsFromSide(side) : null;
		int size = isSided ? accessibleSlots.length : inventory.getSizeInventory();

		for (int i = 0; i < size; i++) {
			int slot = isSided ? accessibleSlots[i] : i;

			ItemStack otherStack = inventory.getStackInSlot(slot);

			if (otherStack == null || otherStack.stackSize == 0) {
				inventory.setInventorySlotContents(slot, stack);

				inventory.markDirty();
				return true;
			} else if (otherStack.isItemEqual(stack) && otherStack.stackSize < inventory.getInventoryStackLimit() && otherStack.stackSize < otherStack.getItem().getItemStackLimit(stack)) {
				otherStack.stackSize++;

				inventory.markDirty();
				return true;
			}
		}

		return false;
	}

	public static void dropContents(IInventory te, World world, int x, int y, int z) {
		for (int i = 0; i < te.getSizeInventory(); ++i) {
			ItemStack itemstack = te.getStackInSlotOnClosing(i);

			dropItem(itemstack, world, x, y, z);
		}
	}

	public static void dropItem(ItemStack itemstack, World world, double x, double y, double z) {
		if (itemstack != null) {
			float randX = random.nextFloat() * 0.8F + 0.1F;
			float randY = random.nextFloat() * 0.8F + 0.1F;
			float randZ = random.nextFloat() * 0.8F + 0.1F;

			while (itemstack.stackSize > 0) {
				int k1 = random.nextInt(21) + 10;

				if (k1 > itemstack.stackSize) {
					k1 = itemstack.stackSize;
				}

				itemstack.stackSize -= k1;
				EntityItem entityitem = new EntityItem(world,
						(double) ((float) x + randX),
						(double) ((float) y + randY),
						(double) ((float) z + randZ),
						new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage())
				);

				float f3 = 0.05F;
				entityitem.motionX = (double) ((float) random.nextGaussian() * f3);
				entityitem.motionY = (double) ((float) random.nextGaussian() * f3 + 0.2F);
				entityitem.motionZ = (double) ((float) random.nextGaussian() * f3);

				if (itemstack.hasTagCompound()) {
					entityitem.getEntityItem().setTagCompound(
							(NBTTagCompound) itemstack.getTagCompound()
									.copy());
				}

				world.spawnEntityInWorld(entityitem);
			}
		}
	}

	public static void dropItem(ItemStack itemstack, EntityPlayer player) {
		dropItem(itemstack, player.worldObj, player.posX, player.posY, player.posZ);
	}
}
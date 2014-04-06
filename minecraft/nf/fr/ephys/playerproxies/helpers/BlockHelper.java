package nf.fr.ephys.playerproxies.helpers;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockHelper {
	private static Random random = new Random();
	
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
	    int l = MathHelper.floor_double((double)(rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	    return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
	}
	
	public static void dropContents(IInventory te, World world, int x, int y, int z) {
		for (int i = 0; i < te.getSizeInventory(); ++i) {
			ItemStack itemstack = te.getStackInSlotOnClosing(i);

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
							new ItemStack(itemstack.itemID, k1, itemstack.getItemDamage())
					);
					
					float f3 = 0.05F;
					entityitem.motionX = (double) ((float) random
							.nextGaussian() * f3);
					entityitem.motionY = (double) ((float) random
							.nextGaussian() * f3 + 0.2F);
					entityitem.motionZ = (double) ((float) random
							.nextGaussian() * f3);

					if (itemstack.hasTagCompound()) {
						entityitem.getEntityItem().setTagCompound(
								(NBTTagCompound) itemstack.getTagCompound()
										.copy());
					}

					world.spawnEntityInWorld(entityitem);
				}
			}
		}

		world.func_96440_m(x, y, z, 0);
	}
}
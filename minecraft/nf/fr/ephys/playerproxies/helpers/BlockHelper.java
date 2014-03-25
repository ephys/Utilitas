package nf.fr.ephys.playerproxies.helpers;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockHelper {
	private static Random random = new Random();
	
	public static int orientationToMetadataXZ(double rotationYaw) {
	    int l = MathHelper.floor_double((double)(rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	    return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
	}
	
	public static void dropContents(IInventory te, World world, int x, int y, int z) {
		for (int i = 0; i < te.getSizeInventory(); ++i) {
			ItemStack itemstack = te.getStackInSlot(i);

			if (itemstack != null) {
				float f = random.nextFloat() * 0.8F + 0.1F;
				float f1 = random.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem;

				for (float f2 = random.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem)) {
					int k1 = random.nextInt(21) + 10;

					if (k1 > itemstack.stackSize) {
						k1 = itemstack.stackSize;
					}

					itemstack.stackSize -= k1;
					entityitem = new EntityItem(world,
							(double) ((float) x + f),
							(double) ((float) y + f1),
							(double) ((float) z + f2), 
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
				}
			}
		}

		world.func_96440_m(x, y, z, 0);
	}
}
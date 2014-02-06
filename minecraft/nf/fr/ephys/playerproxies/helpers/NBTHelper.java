package nf.fr.ephys.playerproxies.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class NBTHelper {
	public static NBTTagCompound getNBT(ItemStack stack) {
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		
		return stack.getTagCompound();
	}
	
	public static void setIntArray(ItemStack stack, String name, int[] arr) {
		getNBT(stack).setIntArray(name, arr);
	}
	
	public static void setString(ItemStack stack, String name, String str) {
		getNBT(stack).setString(name, str);
	}

	public static String getString(ItemStack stack, String name) {
		return getString(stack, name, null);
	}
	
	public static String getString(ItemStack stack, String name, String def) {
		NBTTagCompound nbt = getNBT(stack);
		if(!nbt.hasKey(name))
			return def;
		
		return nbt.getString(name);
	}

	public static int[] getIntArray(ItemStack stack, String name) {
		return getIntArray(stack, name, null);
	}

	public static int[] getIntArray(ItemStack stack, String name, int[] def) {
		NBTTagCompound nbt = getNBT(stack);
		if(!nbt.hasKey(name))
			return def;
		
		return nbt.getIntArray(name);
	}
	
	public static Class<?> getClass(ItemStack stack, String name) {
		return getClass(stack, name);
	}
	
	public static Class<?> getClass(ItemStack stack, String name, Class<?> def) {
		String className = getString(stack, name);
		
		if(className == null)
			return def;

		Class<?> clazz;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			clazz = null;
			e.printStackTrace();
		}

		return clazz;
	}
}

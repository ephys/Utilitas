package nf.fr.ephys.playerproxies.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class NBTHelper {
	public static NBTTagCompound getNBT(ItemStack stack) {
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		
		return stack.getTagCompound();
	}
	
	// ==========================================================================
	// Setters
	// ==========================================================================
	public static void setIntArray(ItemStack stack, String name, int[] arr) {
		setIntArray(getNBT(stack), name, arr);
	}
	
	public static void setIntArray(NBTTagCompound nbt, String name, int[] arr) {
		if(arr == null) return;

		nbt.setIntArray(name, arr);
	}

	public static void setString(ItemStack stack, String name, String str) {
		setString(getNBT(stack), name, str);
	}
	
	public static void setString(NBTTagCompound nbt, String name, String str) {
		if(str == null) return;
		
		nbt.setString(name, str);
	}

	public static void setClass(ItemStack stack, String name, Class<?> clazz) {
		setString(getNBT(stack), name, clazz.getName());
	}
	
	public static void setClass(NBTTagCompound nbt, String name, Class<?> clazz) {
		if(clazz == null) return;

		setString(nbt, name, clazz.getName());
	}

	// ==========================================================================
	// Getters
	// ==========================================================================
	
	// --------------------------------------------------------------------------
	// String
	// --------------------------------------------------------------------------
	public static String getString(NBTTagCompound nbt, String name, String def) {
		if(!nbt.hasKey(name))
			return def;

		return nbt.getString(name);
	}
	
	public static String getString(NBTTagCompound nbt, String name) {
		return getString(nbt, name, null);
	}
	
	public static String getString(ItemStack stack, String name) {
		return getString(getNBT(stack), name, null);
	}
	
	public static String getString(ItemStack stack, String name, String def) {
		return getString(getNBT(stack), name, def);
	}

	// --------------------------------------------------------------------------
	// int Array
	// --------------------------------------------------------------------------
	public static int[] getIntArray(ItemStack stack, String name) {
		return getIntArray(getNBT(stack), name, null);
	}

	public static int[] getIntArray(ItemStack stack, String name, int[] def) {
		return getIntArray(getNBT(stack), name, def);
	}
	
	public static int[] getIntArray(NBTTagCompound nbt, String name) {
		return getIntArray(nbt, name, null);
	}

	public static int[] getIntArray(NBTTagCompound nbt, String name, int[] def) {
		if(!nbt.hasKey(name))
			return def;
		
		return nbt.getIntArray(name);
	}

	// --------------------------------------------------------------------------
	// Classes
	// --------------------------------------------------------------------------
	public static Class<?> getClass(NBTTagCompound nbt, String name) {
		return getClass(nbt, name, null);
	}
	
	public static Class<?> getClass(NBTTagCompound nbt, String name, Class<?> def) {
		String className = getString(nbt, name);
		
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
	
	
	public static Class<?> getClass(ItemStack stack, String name) {
		return getClass(getNBT(stack), name, null);
	}
	
	public static Class<?> getClass(ItemStack stack, String name, Class<?> def) {
		return getClass(getNBT(stack), name, def);
	}
	
	// --------------------------------------------------------------------------
	// Boolean
	// --------------------------------------------------------------------------
	public static boolean getBoolean(NBTTagCompound nbt, String name, boolean def) {
		if(!nbt.hasKey(name))
			return def;

		return nbt.getBoolean(name);
	}
	
	public static boolean getBoolean(ItemStack stack, String name, boolean def) {
		return getBoolean(getNBT(stack), name, def);
	}

	// --------------------------------------------------------------------------
	// Double
	// --------------------------------------------------------------------------
	public static double getDouble(NBTTagCompound nbt, String name, double def) {
		if(!nbt.hasKey(name))
			return def;

		return nbt.getDouble(name);
	}

	// --------------------------------------------------------------------------
	// Integer
	// --------------------------------------------------------------------------
	public static int getInt(NBTTagCompound nbt, String name, int def) {
		if(!nbt.hasKey(name))
			return def;

		return nbt.getInteger(name);
	}
}

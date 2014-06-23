package nf.fr.ephys.playerproxies.common.openperipheral;

import java.util.HashMap;
import java.util.List;

import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.api.OpenPeripheralAPI;

public class AdaptorBiomeScanner implements IPeripheralAdapter {
	public static void register() {
		OpenPeripheralAPI.register(new AdaptorBiomeScanner());
	}

	public Class getTargetClass() {
		return TileEntityBiomeScanner.class;
	}
	
	@LuaMethod(returnType = LuaType.NUMBER, onTick = false, description = "Returns the world time")
	public long getWorldTime(IComputerAccess computer, TileEntity fieldHandler) {
		return fieldHandler.worldObj.getWorldTime();
	}
	
	@LuaMethod(returnType = LuaType.NUMBER, onTick = false, description = "Returns the world time")
	public long getWorldSeed(IComputerAccess computer, TileEntity fieldHandler) {
		return fieldHandler.worldObj.getSeed();
	}
}
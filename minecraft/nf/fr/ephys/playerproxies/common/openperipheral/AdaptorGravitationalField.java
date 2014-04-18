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
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.api.OpenPeripheralAPI;

public class AdaptorGravitationalField implements IPeripheralAdapter {
	public static void register() {
		OpenPeripheralAPI.register(new AdaptorGravitationalField());
	}

	public Class getTargetClass() {
		return TileEntityGravitationalField.class;
	}

	@LuaMethod(returnType = LuaType.NUMBER, onTick = false, description = "Changes the Gravitational Field gravity level (returns the new gravity level)", args = { 
			@openperipheral.api.Arg(type = LuaType.NUMBER, name = "level", description = "Gravity level") 
	})
	public float setGravityLevel(IComputerAccess computer, TileEntity fieldHandler, float level) {
		TileEntityGravitationalField field = (TileEntityGravitationalField) fieldHandler;

		return scale(field.setGravityModifier(unscale(level)));
	}
	
	@LuaMethod(returnType = LuaType.NUMBER, onTick = false, description = "Returns the gravity level")
	public float getGravityLevel(IComputerAccess computer, TileEntity fieldHandler) {
		TileEntityGravitationalField field = (TileEntityGravitationalField) fieldHandler;

		return scale(field.getGravityModifier());
	}
	
	private static float scale(float val) {
		// scale = (VAL - MIN) / (MAX - MIN)
		return (val - TileEntityGravitationalField.MIN_GRAVITY) / (TileEntityGravitationalField.MAX_GRAVITY - TileEntityGravitationalField.MIN_GRAVITY);
	}
	
	private static float unscale(float scale) {
		// scale * (MAX - MIN) + MIN = VAL
		return scale * (TileEntityGravitationalField.MAX_GRAVITY - TileEntityGravitationalField.MIN_GRAVITY) + TileEntityGravitationalField.MIN_GRAVITY;
	}
}
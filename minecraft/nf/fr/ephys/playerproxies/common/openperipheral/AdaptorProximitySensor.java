package nf.fr.ephys.playerproxies.common.openperipheral;

import dan200.computer.api.IComputerAccess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityProximitySensor;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;
import openperipheral.api.APIHelpers;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.api.OpenPeripheralAPI;

public class AdaptorProximitySensor implements IPeripheralAdapter {
	private static Method entityToMap;
	
	public static void register() {
		try {
			Class<?> entityUtils = Class.forName("openperipheral.util.EntityUtils");
			entityToMap = entityUtils.getMethod("entityToMap", Entity.class, Vec3.class);
		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("openperipheral.util.EntityUtils probably not found");

			// in dev mode, force die because we HAVE to know that it failed to find the class.
			if (PlayerProxies.DEV_MODE)
				System.exit(1);

			return;
		}

		OpenPeripheralAPI.register(new AdaptorProximitySensor());
	}

	public Class getTargetClass() {
		return TileEntityProximitySensor.class;
	}

	@LuaMethod(returnType = LuaType.TABLE, onTick = false, description = "Returns the last set of detected entities (checks twice a second)")
	public Map<String, Object>[] getEntities(IComputerAccess computer, TileEntityProximitySensor detector) {
		Object[] entityList = detector.getEntityList();

		Map<String, Object>[] luaEntityList = new Map[entityList.length];

		for (int i = 0; i < entityList.length; i++) {
			Vec3 entityPos = BlockHelper.relativePos(detector, (Entity) entityList[i]);

			try {
				Map<String, Object> entityData = (Map) entityToMap.invoke(null, (Entity) entityList[i], entityPos);

				luaEntityList[i] = entityData;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return luaEntityList;
	}

	@LuaMethod(returnType = LuaType.TABLE, onTick = false, description = "Returns the detection radius [x, y, z]")
	public int[] getRadius(IComputerAccess computer, TileEntityProximitySensor detector) {
		return detector.getRadius();
	}

	@LuaMethod(returnType = LuaType.TABLE, onTick = false, description = "Sets the new detection radius and returns it", args = { 
			@openperipheral.api.Arg(type = LuaType.NUMBER, name = "x", description = "Radius x"),
			@openperipheral.api.Arg(type = LuaType.NUMBER, name = "y", description = "Radius y"),
			@openperipheral.api.Arg(type = LuaType.NUMBER, name = "z", description = "Radius z")
	})
	public int[] setRadius(IComputerAccess computer, TileEntityProximitySensor detector, int x, int y, int z) {
		detector.setRadius(x, y, z);

		return getRadius(computer, detector);
	}
}
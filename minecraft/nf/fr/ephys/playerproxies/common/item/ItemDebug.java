package nf.fr.ephys.playerproxies.common.item;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class ItemDebug extends Item {
	public static final int ITEM_ID = 9999;
	private String[] modeNames = new String[]{"Energy level"};
	
	public static void register() {
		ItemDebug itemDebug = new ItemDebug();
		itemDebug.setUnlocalizedName("PP_Debug");
		GameRegistry.registerItem(itemDebug, "PP_Debug");
		LanguageRegistry.instance().addName(itemDebug,
				"Debug tool -- REPORT THIS A BUG");
	}
	
	public ItemDebug() {
		super(ITEM_ID);
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
	}
	
	/**
	 * Sets the tool mode to the next available
	 * @return the new mode
	 */
	private int nextMode(ItemStack stack) {
		int mode = getMode(stack);
		
		mode = mode + 1 % modeNames.length;
		
		NBTHelper.setInt(stack, "mode", mode);
		
		return mode;
	}

	private int getMode(ItemStack stack) {
		return NBTHelper.getInt(stack, "mode", 0);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
		System.out.println(world.getBlockId(x, y, z));
		if (player.isSneaking() && world.getBlockId(x, y, z) == 0) {
			int mode = nextMode(stack);

			System.out.println("Current debug mode: "+modeNames[mode]);

			return true;
		}

		int mode = getMode(stack);
		
		switch (mode) {
			case 0:
				TileEntity te = world.getBlockTileEntity(x, y, z);

				if (te instanceof IEnergyHandler) {
					int energy = ((IEnergyHandler) te).getEnergyStored(ForgeDirection.getOrientation(par7));

					if (world.isRemote)
						player.addChatMessage("[Client] Energy: "+energy);
					else
						player.addChatMessage("[Server] Energy: "+energy);
				}
	
				return true;
		}
		
		return false;
	}
}

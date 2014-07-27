package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityProximitySensor;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;
import nf.fr.ephys.playerproxies.helpers.ChatHelper;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

import java.util.List;

public class ItemLinker extends Item {
	public static int ITEM_ID = 901;

	public static void register() {
		PlayerProxies.Items.linkDevice = new ItemLinker();
		PlayerProxies.Items.linkDevice.setUnlocalizedName("PP_LinkWand")
			.setMaxStackSize(1)
			.setCreativeTab(PlayerProxies.creativeTab)
			.setTextureName("ephys.pp:linkDevice");

		GameRegistry.registerItem(PlayerProxies.Items.linkDevice, PlayerProxies.Items.linkDevice.getUnlocalizedName());

		MinecraftForge.EVENT_BUS.register(PlayerProxies.Items.linkDevice);
	}

	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Items.linkDevice),
				" il", " si", "s  ",
				'l', new ItemStack(PlayerProxies.Items.linkFocus),
				'i', new ItemStack(Items.iron_ingot),
				's', new ItemStack(Items.stick));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean debug) {
		list.add("Can configure an interface.");
		list.add("Can filter a proximity sensor.");

		NBTTagCompound nbt = NBTHelper.getNBT(stack);

		String name = null;

		if (nbt.hasKey("entity")) {
			Entity entity = null;
			entity = player.worldObj.getEntityByID(nbt.getInteger("entity"));

			if (entity != null) {
				if (entity instanceof EntityPlayer)
					name = ((EntityPlayer) entity).getDisplayName();
				else
					name = entity.getCommandSenderName();
			}
		} else if (nbt.hasKey("tile")) {
			int[] coords = nbt.getIntArray("tile");
			name = "{" + coords[0] + ", " + coords[1] + ", " + coords[2] + "}";
		}

		list.add("§5Bound to: " + (name == null ? "none" : name));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World par2World, EntityPlayer player) {
		if(!par2World.isRemote && player.isSneaking()) {
			NBTTagCompound nbt = NBTHelper.getNBT(stack);

			if (nbt.hasKey("entity") || nbt.hasKey("tile")) {
				nbt.removeTag("entity");
				nbt.removeTag("tile");

				ChatHelper.sendChatMessage(player, "Wand data cleared");
			} else {
				nbt.setInteger("entity", player.getEntityId());

				ChatHelper.sendChatMessage(player, "Wand bound to " + player.getDisplayName());
			}
		}

		return super.onItemRightClick(stack, par2World, player);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		TileEntity te = world.getTileEntity(x, y, z);

		if (te == null)
			return false;

		if(te instanceof TileEntityProximitySensor) {
			NBTTagCompound nbt = NBTHelper.getNBT(stack);

			if (!world.isRemote) {
				if (nbt.hasKey("entity")) {
					Entity entity = player.worldObj.getEntityByID(nbt.getInteger("entity"));

					((TileEntityProximitySensor)te).setEntityFilter(entity, player);
				} else {
					((TileEntityProximitySensor)te).setEntityFilter(null, player);
				}
			}

			return true;
		} else {
			int[] coords = BlockHelper.getCoords(te);
			NBTHelper.setIntArray(stack, "tile", coords);
			NBTHelper.getNBT(stack).removeTag("entity");

			if (!world.isRemote)
				ChatHelper.sendChatMessage(player, "Wand bound to {" + coords[0] + ", " + coords[1] + ", " + coords[2] + "}");

			return true;
		}
	}

	@SubscribeEvent
	public void onEntityInteraction(EntityInteractEvent event) {
		if (event.entityPlayer.worldObj.isRemote)
			return;

		ItemStack item = event.entityPlayer.getHeldItem();
		if (item == null || !(item.getItem() instanceof ItemLinker))
			return;

		String entityName = event.target.getCommandSenderName();

		NBTHelper.setInt(item, "entity", event.target.getEntityId());

		if (event.target instanceof EntityPlayer)
			ChatHelper.sendChatMessage(event.entityPlayer, "Wand bound to " + ((EntityPlayer) event.target).getDisplayName());
		else
			ChatHelper.sendChatMessage(event.entityPlayer, "Wand bound to " + entityName);

		event.setCanceled(true);
	}

	public static Object getLinkedObject(ItemStack item, World world) {
		NBTTagCompound nbt = NBTHelper.getNBT(item);

		if (nbt.hasKey("entity"))
			return world.getEntityByID(nbt.getInteger("entity"));

		if (nbt.hasKey("tile")) {
			int[] coords = nbt.getIntArray("tile");
			return world.getTileEntity(coords[0], coords[1], coords[2]);
		}

		return null;
	}
}
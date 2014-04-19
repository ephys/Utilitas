package nf.fr.ephys.playerproxies.common.item;

import java.util.List;

import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import nf.fr.ephys.playerproxies.client.gui.GuiUniversalInterface;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityProximitySensor;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class ItemLinker extends Item {
	public static int ITEM_ID = 901;
	
	public static void register() {
		PlayerProxies.Items.linkeDevice = new ItemLinker();
		PlayerProxies.Items.linkeDevice.setUnlocalizedName("PP_LinkWand");
		MinecraftForge.EVENT_BUS.register(PlayerProxies.Items.linkeDevice);
		GameRegistry.registerItem(PlayerProxies.Items.linkeDevice, "PP_LinkWand");
		LanguageRegistry.instance().addName(PlayerProxies.Items.linkeDevice, "Linking wand");
	}
	
	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Items.linkeDevice), 
				" il", " si", "s  ", 
				'l', new ItemStack(PlayerProxies.Items.linkFocus),
				'i', new ItemStack(Item.ingotIron), 
				's', new ItemStack(Item.stick));
	}

	public ItemLinker() {
		super(ITEM_ID);
		setMaxStackSize(1);
		setCreativeTab(PlayerProxies.creativeTab);
		setTextureName("ephys.pp:linkDevice");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		String name = NBTHelper.getString(stack, "playerName", "none");

		if(name == null)
			name = NBTHelper.getString(stack, "entityName", "none");

		list.add("Can configure an interface.");
		list.add("Can filter a proximity sensor: §5"+name);

		super.addInformation(stack, par2EntityPlayer, list, par4);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World par2World, EntityPlayer player) {
		if(!par2World.isRemote && player.isSneaking()) {
			NBTTagCompound nbt = NBTHelper.getNBT(stack);
			if(nbt.hasKey("entityName") || nbt.hasKey("playerName")) {
				stack.setTagCompound(new NBTTagCompound());

				player.addChatMessage("Link wand data cleared");
			} else {
				nbt.setString("playerName", player.username);

				player.addChatMessage("Link wand bound to "+player.username);
			}
		}

		return super.onItemRightClick(stack, par2World, player);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!world.isRemote) {
			TileEntity te = world.getBlockTileEntity(x, y, z);

			if (te == null)
				return false;

			if (te instanceof TileEntityInterface) {
				if (!player.isSneaking()) {
					player.openGui(PlayerProxies.instance, PlayerProxies.GUI_UNIVERSAL_INTERFACE, world, x, y, z);
				} else {
					NBTHelper.setIntArray(stack, "linkedInterface", new int[]{te.xCoord, te.yCoord, te.zCoord});

					player.addChatMessage("The link device will now connect to this Universal Interface");
				}
			} else if (TileEntityInterface.isValidTE(te)) {
				TileEntityInterface linkedInterface = null;
				
				int[] teCoords = NBTHelper.getIntArray(stack, "linkedInterface");
				if(teCoords != null) {
					TileEntity bi = world.getBlockTileEntity(teCoords[0], teCoords[1], teCoords[2]);
					
					if(bi instanceof TileEntityInterface)
						linkedInterface = (TileEntityInterface) bi;
				}
				
				if (linkedInterface == null || linkedInterface.isInvalid())
					player.addChatMessage("You must link this device to an Universal Interface first");
				else {
					linkedInterface.toggleLinked(te, player);
					world.markBlockForUpdate(linkedInterface.xCoord, linkedInterface.yCoord, linkedInterface.zCoord);
				}
			} else if(te instanceof TileEntityProximitySensor) {
				String playerName = NBTHelper.getString(stack, "playerName", null);
				
				if(playerName == null) {
					String entityName = NBTHelper.getString(stack, "entityName", "none");
					
					Class<? extends Entity> proxSensorEntity = (Class<? extends Entity>)NBTHelper.getClass(stack, "entityClass", Entity.class);
					
					((TileEntityProximitySensor)te).setEntityFilter(proxSensorEntity, player, entityName);
				} else {
					((TileEntityProximitySensor)te).setEntityFilter(playerName, player);
				}
			}
		}

		return true;
	}

	@ForgeSubscribe
	public void onEntityInteraction(EntityInteractEvent event) {
		if(event.entityPlayer.worldObj.isRemote)
			return;
		
		ItemStack item = event.entityPlayer.getHeldItem();
		if(!(item.getItem() instanceof ItemLinker))
			return;

		String entityName = event.target.getEntityName();
		NBTHelper.setString(item, "entityName", entityName);
		NBTHelper.setClass(item, "entityClass", event.target.getClass());

		if(event.entityPlayer != null)
			event.entityPlayer.addChatMessage("Entity filter set to "+entityName);
		
		event.setCanceled(true);
	}
}
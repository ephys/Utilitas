package nf.fr.ephys.playerproxies.common.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;

public class ItemLinker extends Item {
	public static int itemID = 901;
	
	private TEBlockInterface linkedInterface = null;
	
	public ItemLinker() {
		super(itemID);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
		setTextureName("ephys.pp:link_device");
	}
	
	@Override
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("Shift and click to link");
		super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
	}
	
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		 if(!world.isRemote) {
			 TileEntity te = world.getBlockTileEntity(x, y, z);
			 
			 if(te == null)
				 return false;
	
			 if(te instanceof TEBlockInterface) {
				 if(!player.isSneaking()) {
					 ((TEBlockInterface)te).sendLinkedListTo(player);
				 } else {
					 this.linkedInterface = (TEBlockInterface)te;
					 player.addChatMessage("The link device will now connect to this Universal Interface");
				 }
			 } else if(linkedInterface == null || this.linkedInterface.isInvalid()) {
				 player.addChatMessage("You must link this device to an Universal Interface first");
			 } else {
				 this.linkedInterface.toggleLinked(te, player);
			 }
		 }
		 
		 if(linkedInterface != null && !linkedInterface.isInvalid())
			 world.markBlockForUpdate(linkedInterface.xCoord, linkedInterface.yCoord, linkedInterface.zCoord);
		 
		 return true;
	 }
}

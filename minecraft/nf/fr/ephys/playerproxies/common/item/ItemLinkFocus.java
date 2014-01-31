package nf.fr.ephys.playerproxies.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.entity.Ghost;

public class ItemLinkFocus extends Item {
	public static int itemID = 900;
	
	public ItemLinkFocus() {
		super(itemID);
		setMaxStackSize(64);
		setCreativeTab(CreativeTabs.tabMaterials);
		setTextureName("ephys.pp:link_focus");
	}
	
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World world, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		if(!world.isRemote) {
			new Ghost(world, par2EntityPlayer.username, par4, par5+1, par6);
		}
		
		return super.onItemUse(par1ItemStack, par2EntityPlayer, world, par4, par5, par6, par7, par8, par9, par10);
	}
	
	@Override
	public boolean hitEntity(ItemStack par1ItemStack,
			EntityLivingBase attacked,
			EntityLivingBase attacker) {

		if(attacked.worldObj.isRemote)
			System.out.println("[REMOTE] "+attacked);
		else
			System.out.println("[NOT REMOTE] "+attacked);

		return super.hitEntity(par1ItemStack, attacked,
				attacker);
	}
}

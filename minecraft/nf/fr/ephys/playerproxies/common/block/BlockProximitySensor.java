package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityProximitySensor;

public class BlockProximitySensor extends BlockContainer {
	public static int BLOCK_ID = 805;
	
	private Icon iconTop;
	private Icon iconSide;
	
	public static void register() {
		PlayerProxies.blockProximitySensor = new BlockProximitySensor();
		PlayerProxies.blockProximitySensor.setUnlocalizedName("PP_ProximitySensor");
		GameRegistry.registerBlock(PlayerProxies.blockProximitySensor, "PP_ProximitySensor");
		GameRegistry.registerTileEntity(TileEntityProximitySensor.class, "PP_ProximitySensor");
		LanguageRegistry.instance().addName(PlayerProxies.blockProximitySensor, "Proximity Sensor");
	}
	
	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockProximitySensor), 
				"hhh", "hlh", "hrh",
				'h', new ItemStack(PlayerProxies.blockHardenedStone), 
				'l', new ItemStack(PlayerProxies.itemLinkFocus), 
				'r', new ItemStack(Item.redstone));
	}
	
	public BlockProximitySensor() {
		super(BLOCK_ID, Material.iron);
		
		setHardness(2F);
		setResistance(500.0F);
		
		setCreativeTab(PlayerProxies.creativeTab);
	}
	
	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9) {
		if(!world.isRemote) {
			if(player.getHeldItem() != null)
				return false;

			TileEntityProximitySensor te = (TileEntityProximitySensor)world.getBlockTileEntity(x, y, z);
			
			te.updateRadius(side, player);
		}
		
		return true;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		return par5 == 1 ? this.isProvidingWeakPower(par1iBlockAccess, par2, par3, par4, par5) : 0;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		TileEntity te = par1iBlockAccess.getBlockTileEntity(par2, par3, par4);

		return ((TileEntityProximitySensor)te).isActivated ? 15:0;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityProximitySensor();
	}
	
	@Override
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		return par5 == 1 ? iconTop : iconSide;
	}
	
	@Override
	public Icon getIcon(int par1, int par2) {
		return par1 == 1 ? iconTop : iconSide;
	}
	
	@Override
	public void registerIcons(IconRegister register) {
		iconSide = register.registerIcon("ephys.pp:proximitySensor");
		iconTop = register.registerIcon("ephys.pp:hardenedStone");
	}
}

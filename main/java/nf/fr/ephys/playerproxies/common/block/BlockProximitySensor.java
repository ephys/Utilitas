package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityProximitySensor;

public class BlockProximitySensor extends BlockContainer {
	private IIcon iconTop;
	private IIcon iconSide;

	public static void register() {
		PlayerProxies.Blocks.proximitySensor = new BlockProximitySensor(Material.iron);
		PlayerProxies.Blocks.proximitySensor.setBlockName("PP_ProximitySensor")
				.setHardness(2F)
				.setResistance(500.0F)
				.setCreativeTab(PlayerProxies.creativeTab);

		GameRegistry.registerBlock(PlayerProxies.Blocks.proximitySensor, PlayerProxies.Blocks.proximitySensor.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityProximitySensor.class, PlayerProxies.Blocks.proximitySensor.getUnlocalizedName());
	}

	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.proximitySensor),
				"hhh", "hlh", "hrh",
				'h', new ItemStack(PlayerProxies.Blocks.hardenedStone),
				'l', new ItemStack(PlayerProxies.Items.linkFocus),
				'r', new ItemStack(Items.redstone));
	}

	public BlockProximitySensor(Material material) {
		super(material);
	}

	@Override
	public boolean isBlockSolid(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		return true;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9) {
		if(player.getHeldItem() != null)
			return false;

		if (!world.isRemote) {
			TileEntityProximitySensor te = (TileEntityProximitySensor)world.getTileEntity(x, y, z);

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
		TileEntity te = par1iBlockAccess.getTileEntity(par2, par3, par4);

		return ((TileEntityProximitySensor)te).isActivated ? 15:0;
	}

	@Override
	public IIcon getIcon(int side, int par2) {
		return side == 1 ? iconTop : iconSide;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		iconSide = register.registerIcon("ephys.pp:proximitySensor");
		iconTop = register.registerIcon("ephys.pp:hardenedStone");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityProximitySensor();
	}
}

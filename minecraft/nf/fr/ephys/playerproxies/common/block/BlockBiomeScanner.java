package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeReplicator;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;

public class BlockBiomeScanner extends BlockContainer {
	public static int BLOCK_ID = 807;

	private Icon iconTop;
	private Icon iconSide;
	private Icon iconBottom;
	private Icon iconScreen;
	
	public static void register() {
		PlayerProxies.Blocks.biomeScanner = new BlockBiomeScanner();
		PlayerProxies.Blocks.biomeScanner.setUnlocalizedName("PP_BiomeScanner");
		GameRegistry.registerBlock(PlayerProxies.Blocks.biomeScanner, "PP_BiomeScanner");
		GameRegistry.registerTileEntity(TileEntityBiomeScanner.class, "PP_BiomeScanner");
		LanguageRegistry.instance().addName(PlayerProxies.Blocks.biomeScanner, "Biome scanner");
	}

	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.biomeScanner), 
				" g ", "pop", " p ",
				'g', new ItemStack(Item.goldNugget),
				'o', new ItemStack(Block.obsidian),
				'p', new ItemStack(PlayerProxies.Blocks.toughwoodPlank)
		);
	}
	
	public BlockBiomeScanner() {
		super(BLOCK_ID, Material.wood);

		setHardness(1.0F);
		setCreativeTab(PlayerProxies.creativeTab);
		
		setStepSound(soundWoodFootstep);
	}

	@Override
	public Icon getIcon(int side, int metadata) {
		switch (side) {
		case 0:
			return iconBottom;
		case 1:
			return iconTop;
		default:
			if (metadata == 0)
				return side == 2 || side == 4 ? iconScreen : iconSide;

			return side == metadata ? iconScreen : iconSide;
		}
	}

	@Override
	public void registerIcons(IconRegister register) {
		iconTop = register.registerIcon("ephys.pp:biomeScannerTop");
		iconBottom = register.registerIcon("ephys.pp:biomeChangerBottom");
		iconSide = register.registerIcon("ephys.pp:biomeScannerSide");
		iconScreen = register.registerIcon("ephys.pp:biomeScannerScreen");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityBiomeScanner();
	}

	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4,
			EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		par1World.setBlockMetadataWithNotify(par2, par3, par4, BlockHelper
				.orientationToMetadataXZ(par5EntityLivingBase.rotationYaw), 2);
	}

	@Override
	public void onPostBlockPlaced(World world, int x, int y, int z, int metadata) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof TileEntityBiomeScanner)
			((TileEntityBiomeScanner) te).setBiome(world.getBiomeGenForCoords(
					x, z));
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ, int metadata) {
		return 2;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (player.isSneaking())
			return false;

		player.openGui(PlayerProxies.instance, PlayerProxies.GUI_BIOME_SCANNER,
				world, x, y, z);

		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int oldID, int oldMetadata) {
		IInventory te = (IInventory) world.getBlockTileEntity(x, y, z);

		if (te != null)
			BlockHelper.dropContents(te, world, x, y, z);

		super.breakBlock(world, x, y, z, oldID, oldMetadata);
	}
}

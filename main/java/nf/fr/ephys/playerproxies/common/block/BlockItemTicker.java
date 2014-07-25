package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityItemTicker;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;

public class BlockItemTicker extends BlockContainer {
	public static boolean enabled = true;
	private IIcon iconTop;
	private IIcon iconSide;
	private IIcon iconBottom;

	public static void register() {
		if (!enabled) return;

		PlayerProxies.Blocks.itemTicker = new BlockItemTicker(Material.iron);
		PlayerProxies.Blocks.itemTicker.setBlockName("PP_ItemActivator")
			.setHardness(2.5F)
			.setCreativeTab(PlayerProxies.creativeTab)
			.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8F, 1.0F);

		GameRegistry.registerBlock(PlayerProxies.Blocks.itemTicker, PlayerProxies.Blocks.itemTicker.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityItemTicker.class, PlayerProxies.Blocks.itemTicker.getUnlocalizedName());
	}

	public static void registerCraft() {
		if (!enabled) return;

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.itemTicker),
				"   ", " c ", "sss",
				'c', new ItemStack(Items.clock),
				's', new ItemStack(Blocks.stone_slab)
		);
	}

	public BlockItemTicker(Material material) {
		super(material);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		switch (side) {
		case 0:
			return iconBottom;
		case 1:
			return iconTop;
		default:
			return iconSide;
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		iconTop = register.registerIcon("ephys.pp:itemTickerTop");
		iconBottom = register.registerIcon("ephys.pp:itemTickerBottom");
		iconSide = register.registerIcon("ephys.pp:itemTickerSide");
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityItemTicker();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (world.isRemote)
			return true;

		TileEntityItemTicker te = (TileEntityItemTicker) world.getTileEntity(x, y, z);

		if (te != null) {
			if (te.hasStackInSlot(0))
				BlockHelper.dropContents(te, world, x, y, z);
			else if (te.isItemValidForSlot(0, player.getHeldItem())) {
				te.setInventorySlotContents(0, player.getHeldItem().copy());
				player.getHeldItem().stackSize--;
			}
		}

		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int metadata) {
		TileEntityItemTicker te = (TileEntityItemTicker) world.getTileEntity(x, y, z);

		if (te != null)
			BlockHelper.dropContents(te, world, x, y, z);

		super.onBlockPreDestroy(world, x, y, z, metadata);
	}
}

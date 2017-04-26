package be.ephys.utilitas.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;

public class BlockGravitationalField extends BlockContainer {
	public static boolean enabled = true;
	private IIcon iconBottom;
	private IIcon iconTop;
	private IIcon iconSide;

	public static void register() {
		if (!enabled) return;

		PlayerProxies.Blocks.gravitationalField = new BlockGravitationalField();
		PlayerProxies.Blocks.gravitationalField.setBlockName("PP_GravitationalField")
			.setHardness(1.0F)
			.setCreativeTab(PlayerProxies.creativeTab);

		GameRegistry.registerBlock(PlayerProxies.Blocks.gravitationalField, PlayerProxies.Blocks.gravitationalField.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityGravitationalField.class, PlayerProxies.Blocks.gravitationalField.getUnlocalizedName());
	}

	public static void registerCraft() {
		if (!enabled) return;

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.gravitationalField),
			" l ", "gsg", " l ",
			'l', PlayerProxies.Items.linkFocus,
			'g', new ItemStack(PlayerProxies.Blocks.baseShineyGlass, 1, BlockShinyGlass.METADATA_GLASS),
			's', PlayerProxies.Blocks.hardenedStone
		);
	}

	public BlockGravitationalField() {
		super(Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityGravitationalField();
	}

	@Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (world.isRemote) return;

        ((TileEntityGravitationalField) world.getTileEntity(x, y, z)).checkPowered();
    }

	@Override
	public IIcon getIcon(int side, int par2) {
		switch (side) {
			case 0: return iconBottom;
			case 1: return iconTop;
			default: return iconSide;
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		iconBottom = register.registerIcon("ephys.pp:gravitationalFieldBottom");
		iconSide = register.registerIcon("ephys.pp:gravitationalFieldSide");
		iconTop = register.registerIcon("ephys.pp:gravitationalFieldTop");
	}
}

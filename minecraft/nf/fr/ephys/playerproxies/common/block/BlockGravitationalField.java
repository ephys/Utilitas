package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityGravitationalField;

public class BlockGravitationalField extends BlockContainer {
	public static int BLOCK_ID = 809;

	private Icon iconBottom;
	private Icon iconTop;
	private Icon iconSide;

	public static void register() {
		PlayerProxies.Blocks.gravitationalField = new BlockGravitationalField();
		PlayerProxies.Blocks.gravitationalField.setUnlocalizedName("PP_GravitationalField");
		GameRegistry.registerBlock(PlayerProxies.Blocks.gravitationalField, "PP_GravitationalField");
		GameRegistry.registerTileEntity(TileEntityGravitationalField.class, "PP_GravitationalField");
		LanguageRegistry.instance().addName(PlayerProxies.Blocks.gravitationalField, "Gravitational Field Handler");
	}
	
	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.gravitationalField), 
			" l ", "gsg", " l ",
			'l', PlayerProxies.Items.linkFocus, 
			'g', new ItemStack(PlayerProxies.Blocks.baseShineyGlass, 1, BlockBaseShineyGlass.METADATA_ETHEREAL_GLASS), 
			's', PlayerProxies.Blocks.hardenedStone
		);
	}
	
	public BlockGravitationalField() {
		super(BLOCK_ID, Material.iron);

		setHardness(1.0F);

		this.setCreativeTab(PlayerProxies.creativeTab);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityGravitationalField();
	}

    public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {
        if (world.isRemote) return;

        ((TileEntityGravitationalField) world.getBlockTileEntity(x, y, z)).checkPowered();
    }

	@Override
	public Icon getIcon(int side, int par2) {
		switch (side) {
			case 0: return iconBottom;
			case 1: return iconTop;
			default: return iconSide;
		}
	}

	@Override
	public void registerIcons(IconRegister register) {
		iconBottom = register.registerIcon("ephys.pp:gravitationalFieldBottom");
		iconSide = register.registerIcon("ephys.pp:gravitationalFieldSide");
		iconTop = register.registerIcon("ephys.pp:gravitationalFieldTop");
	}
}

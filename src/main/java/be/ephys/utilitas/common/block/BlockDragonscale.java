package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.playerproxies.client.registry.DragonColorRegistry;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

public class BlockDragonscale extends Block {
	public static void register() {
		PlayerProxies.Blocks.dragonScale = new BlockDragonscale(Material.iron);
		PlayerProxies.Blocks.dragonScale.setBlockName("PP_DragonScale")
				.setHardness(3.0F)
				.setCreativeTab(PlayerProxies.creativeTab)
				.setBlockTextureName("iron_block")
				.setStepSound(soundTypeMetal);

		GameRegistry.registerBlock(PlayerProxies.Blocks.dragonScale, PlayerProxies.Blocks.dragonScale.getUnlocalizedName());
	}

	public static void registerCraft() {
		GameRegistry.addShapelessRecipe(new ItemStack(PlayerProxies.Items.dragonScaleIngot, 9), PlayerProxies.Blocks.dragonScale);

		GameRegistry.addShapelessRecipe(new ItemStack(PlayerProxies.Blocks.dragonScale), new ItemStack(PlayerProxies.Items.dragonScaleIngot, 9));
	}

	protected BlockDragonscale(Material material) {
		super(material);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int p_149741_1_) {
		return getBlockColor();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor() {
		return DragonColorRegistry.getColor();
	}
}

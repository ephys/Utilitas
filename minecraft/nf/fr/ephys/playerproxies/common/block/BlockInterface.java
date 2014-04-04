package nf.fr.ephys.playerproxies.common.block;

import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.turtle.api.TurtleAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;

public class BlockInterface extends BlockContainer {
	public static int BLOCK_ID = 800;
	
	public static void register() {
		PlayerProxies.blockInterface = new BlockInterface();
		PlayerProxies.blockInterface.setUnlocalizedName("PP_UniversalInterface");
		GameRegistry.registerBlock(PlayerProxies.blockInterface, "PP_UniversalInterface");
		GameRegistry.registerTileEntity(TileEntityInterface.class, "PP_UniversalInterface");
		LanguageRegistry.instance().addName(PlayerProxies.blockInterface, "Universal Interface");
	}
	
	public static void registerCraft() {
		GameRegistry.addRecipe(new ItemStack(PlayerProxies.blockInterface),
				"dld", "geg", "dgd", 
				'd', new ItemStack(Item.diamond), 
				'l', new ItemStack(PlayerProxies.itemLinkFocus), 
				'g', new ItemStack(PlayerProxies.blockEtherealGlass), 'e', new ItemStack(Block.enderChest)
		);
	}

	public BlockInterface() {
		super(BlockInterface.BLOCK_ID, Material.glass);
		this.setStepSound(this.soundGlassFootstep);
		setLightValue(1.0F);
		setHardness(1.0F);
		setCreativeTab(CreativeTabs.tabDecorations);
		setTextureName("ephys.pp:pureGlass");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityInterface();
	}

	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4,
			EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
	}

	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitVectX, float hitVectY,
			float hitVectZ) {
		if (player.getHeldItem() == null) {
			if (!world.isRemote)
				((TileEntityInterface) world.getBlockTileEntity(x, y, z))
						.toggleLinked(player);

			world.markBlockForUpdate(x, y, z);

			return true;
		}

		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess par1iBlockAccess,
			int par2, int par3, int par4, int par5) {
		return true;
	}
}

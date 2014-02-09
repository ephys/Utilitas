package nf.fr.ephys.playerproxies.common.block;

import java.util.Random;

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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;

public class BlockEtherealGlass extends Block {
	public static int blockID = 804;
	
	public BlockEtherealGlass() {
		super(BlockEtherealGlass.blockID, Material.glass);
		this.setStepSound(Block.soundGlassFootstep);
		setLightValue(1.0F);
		setHardness(1.0F);
		setCreativeTab(CreativeTabs.tabBlock);
		setTextureName("ephys.pp:pureGlass");
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

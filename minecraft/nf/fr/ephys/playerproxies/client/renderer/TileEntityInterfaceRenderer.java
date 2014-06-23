package nf.fr.ephys.playerproxies.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityInterfaceRenderer extends TileEntitySpecialRenderer {
	public static final RenderBlocks renderBlocksInstance = new RenderBlocks();

	public static void renderBlockInterface(TileEntityInterface bi, double par1, double par3, double par5, float par7) {
		final float scale = 0.4375F;

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslatef(0.0F, 0.5F, 0.0F);
		GL11.glScalef(scale, scale, scale);

		bi.getInterface().renderInventory(bi.tick++, par1, par3, par5, par7);
	}

	public void renderTileEntityAt(TileEntity par1, double par2, double par4, double par6, float par8) {
		TileEntityInterface tile = (TileEntityInterface) par1;
		
		if (tile.getInterface() == null) return;
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) par2 + 0.5F, (float) par4, (float) par6 + 0.5F);
		renderBlockInterface(tile, par2, par4, par6, par8);
		GL11.glPopMatrix();
	}
}

package nf.fr.ephys.playerproxies.client.renderer;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityItemTicker;

public class TileEntityItemTickerRenderer extends TileEntitySpecialRenderer {
	private static final RenderBlocks renderBlocksInstance = new RenderBlocks();

	public static void renderTickerRenderer(TileEntityItemTicker bi, double par1, double par3, double par5, float par7) {
		ItemStack item = bi.getStackInSlot(0);
		
		if (item == null) return;
		
		GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, 1.0F);

		int renderPasses = item.getItem().getRenderPasses(item.getItemDamage());
		
		for (int i = 0; i < renderPasses; i++) {
			Icon icon = item.getItem().getIcon(item, i);
			
        	if(icon != null) {
				Color color = new Color(item.getItem().getColorFromItemStack(item, i));

				GL11.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());

				ItemRenderer.renderItemIn2D(Tessellator.instance, 
						icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), 
						icon.getIconWidth(), icon.getIconHeight(), 
						1F / 16F);

				GL11.glColor3f(1F, 1F, 1F);
        	}
		}
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double par2, double par4, double par6, float par8) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) par2 + 0.5F, (float) par4, (float) par6 + 0.5F);
		renderTickerRenderer((TileEntityItemTicker) tileentity, par2, par4, par6, par8);
		GL11.glPopMatrix();
	}
}
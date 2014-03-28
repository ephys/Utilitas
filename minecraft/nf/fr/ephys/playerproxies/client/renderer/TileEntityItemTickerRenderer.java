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
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityItemTicker;
import nf.fr.ephys.playerproxies.helpers.RenderHelper;

public class TileEntityItemTickerRenderer extends TileEntitySpecialRenderer {
	private static final RenderBlocks renderBlocksInstance = new RenderBlocks();

	public static void renderTickerRenderer(TileEntityItemTicker bi, double x, double y, double z, float par7) {
		ItemStack item = bi.getStackInSlot(0);
		
		if(item == null) return;

		GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			final float scale = 0.725F;
			GL11.glScalef(scale, scale, scale);

			GL11.glTranslatef(0.5F, 1.10F, 0F);

			GL11.glRotatef(90F, 1F, 0F, 0F);
			GL11.glRotatef(30F, 0F, 0F, 1F);

			RenderHelper.loadTexturesMap();
			RenderHelper.renderItem3D(item);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float par8) {
		renderTickerRenderer((TileEntityItemTicker) tileentity, x, y, z, par8);
	}
}
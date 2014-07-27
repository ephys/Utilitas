package nf.fr.ephys.playerproxies.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBeaconTierII;
import nf.fr.ephys.playerproxies.helpers.RenderHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class TileEntityBeaconTierIIRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation beamTexture = new ResourceLocation("textures/entity/beacon_beam.png");

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double v, double v2, double v3, float v4) {
		renderTileEntityAt((TileEntityBeaconTierII) tileEntity, v, v2, v3, v4);
	}

	private void renderInventory(TileEntityBeaconTierII tile,  double x, double y, double z) {
		GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			final float scale = 0.5F;
			GL11.glScalef(scale, scale, scale);

			float radius = 0.5F;

			// center the stacks
			GL11.glTranslatef(0.5F, 2.5F, -radius);

			RenderHelper.loadItemMap();

			GL11.glTranslatef(0.5F, 0, 1.5F);
			GL11.glRotatef(tile.displayTick++, 0F, 1F, 0F);
			GL11.glTranslatef(-0.5F, 0, -1.5F);

			float angle = 360F / tile.getItemCount();

			for (int i = 0; i < tile.getSizeInventory(); i++) {
				ItemStack stack = tile.getStackInSlot(i);

				if (stack != null)
					RenderHelper.renderItem3D(stack);

				GL11.glTranslatef(0.5F, 0, 1F + radius);
				GL11.glRotatef(angle, 0F, 1F, 0F);
				GL11.glTranslatef(-0.5F, 0, -1F - radius);
			}
		GL11.glPopMatrix();
	}

	public void renderTileEntityAt(TileEntityBeaconTierII tile, double x, double y, double z, float paramFloat) {
		renderInventory(tile, x, y, z);

		float f1 = tile.getRotation();

		GL11.glAlphaFunc(516, 0.1F);
		if (f1 > 0.0F)
		{
			Tessellator localTessellator = Tessellator.instance;

			bindTexture(beamTexture);
			GL11.glTexParameterf(3553, 10242, 10497.0F);
			GL11.glTexParameterf(3553, 10243, 10497.0F);
			GL11.glDisable(2896);
			GL11.glDisable(2884);
			GL11.glDisable(3042);
			GL11.glDepthMask(true);
			OpenGlHelper.glBlendFunc(770, 1, 1, 0);

			float f2 = (float) tile.getWorldObj().getTotalWorldTime() + paramFloat;
			float f3 = -f2 * 0.2F - net.minecraft.util.MathHelper.floor_float(-f2 * 0.1F);


			int i = 1;

			double d2 = f2 * 0.025D * (1.0D - (i & 0x1) * 2.5D);

			localTessellator.startDrawingQuads();

			Color color = tile.getBeaconColor();

			localTessellator.setColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), 32);

			double d4 = i * 0.2D;

			double d6 = 0.5D + Math.cos(d2 + 2.356194490192345D) * d4;
			double d8 = 0.5D + Math.sin(d2 + 2.356194490192345D) * d4;
			double d10 = 0.5D + Math.cos(d2 + 0.7853981633974483D) * d4;
			double d12 = 0.5D + Math.sin(d2 + 0.7853981633974483D) * d4;

			double d14 = 0.5D + Math.cos(d2 + 3.9269908169872414D) * d4;
			double d16 = 0.5D + Math.sin(d2 + 3.9269908169872414D) * d4;
			double d18 = 0.5D + Math.cos(d2 + 5.497787143782138D) * d4;
			double d20 = 0.5D + Math.sin(d2 + 5.497787143782138D) * d4;

			double d22 = 256.0F * f1;

			double d24 = 0.0D;
			double d26 = 1.0D;
			double d27 = -1.0F + f3;
			double d28 = 256.0F * f1 * (0.5D / d4) + d27;

			localTessellator.addVertexWithUV(x + d6, y + d22, z + d8, d26, d28);
			localTessellator.addVertexWithUV(x + d6, y, z + d8, d26, d27);
			localTessellator.addVertexWithUV(x + d10, y, z + d12, d24, d27);
			localTessellator.addVertexWithUV(x + d10, y + d22, z + d12, d24, d28);

			localTessellator.addVertexWithUV(x + d18, y + d22, z + d20, d26, d28);
			localTessellator.addVertexWithUV(x + d18, y, z + d20, d26, d27);
			localTessellator.addVertexWithUV(x + d14, y, z + d16, d24, d27);
			localTessellator.addVertexWithUV(x + d14, y + d22, z + d16, d24, d28);

			localTessellator.addVertexWithUV(x + d10, y + d22, z + d12, d26, d28);
			localTessellator.addVertexWithUV(x + d10, y, z + d12, d26, d27);
			localTessellator.addVertexWithUV(x + d18, y, z + d20, d24, d27);
			localTessellator.addVertexWithUV(x + d18, y + d22, z + d20, d24, d28);

			localTessellator.addVertexWithUV(x + d14, y + d22, z + d16, d26, d28);
			localTessellator.addVertexWithUV(x + d14, y, z + d16, d26, d27);
			localTessellator.addVertexWithUV(x + d6, y, z + d8, d24, d27);
			localTessellator.addVertexWithUV(x + d6, y + d22, z + d8, d24, d28);

			localTessellator.draw();


			GL11.glEnable(3042);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glDepthMask(false);

			localTessellator.startDrawingQuads();
			localTessellator.setColorRGBA(255, 255, 255, 32);

			double d1 = 0.2D;
			double d3 = 0.2D;
			double d5 = 0.8D;
			double d7 = 0.2D;

			double d9 = 0.2D;
			double d11 = 0.8D;
			double d13 = 0.8D;
			double d15 = 0.8D;

			double d17 = 256.0F * f1;

			double d19 = 0.0D;
			double d21 = 1.0D;
			double d23 = -1.0F + f3;
			double d25 = 256.0F * f1 + d23;

			localTessellator.addVertexWithUV(x + d1, y + d17, z + d3, d21, d25);
			localTessellator.addVertexWithUV(x + d1, y, z + d3, d21, d23);
			localTessellator.addVertexWithUV(x + d5, y, z + d7, d19, d23);
			localTessellator.addVertexWithUV(x + d5, y + d17, z + d7, d19, d25);

			localTessellator.addVertexWithUV(x + d13, y + d17, z + d15, d21, d25);
			localTessellator.addVertexWithUV(x + d13, y, z + d15, d21, d23);
			localTessellator.addVertexWithUV(x + d9, y, z + d11, d19, d23);
			localTessellator.addVertexWithUV(x + d9, y + d17, z + d11, d19, d25);

			localTessellator.addVertexWithUV(x + d5, y + d17, z + d7, d21, d25);
			localTessellator.addVertexWithUV(x + d5, y, z + d7, d21, d23);
			localTessellator.addVertexWithUV(x + d13, y, z + d15, d19, d23);
			localTessellator.addVertexWithUV(x + d13, y + d17, z + d15, d19, d25);

			localTessellator.addVertexWithUV(x + d9, y + d17, z + d11, d21, d25);
			localTessellator.addVertexWithUV(x + d9, y, z + d11, d21, d23);
			localTessellator.addVertexWithUV(x + d1, y, z + d3, d19, d23);
			localTessellator.addVertexWithUV(x + d1, y + d17, z + d3, d19, d25);

			localTessellator.draw();


			GL11.glEnable(2896);
			GL11.glEnable(3553);

			GL11.glDepthMask(true);
		}
		GL11.glAlphaFunc(516, 0.5F);
	}
}

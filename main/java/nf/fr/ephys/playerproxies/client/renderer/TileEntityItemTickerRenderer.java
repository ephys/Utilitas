package nf.fr.ephys.playerproxies.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityItemTicker;
import nf.fr.ephys.playerproxies.helpers.RenderHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityItemTickerRenderer extends TileEntitySpecialRenderer {
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

			RenderHelper.loadItemMap();
			RenderHelper.renderItem3D(item);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float par8) {
		renderTickerRenderer((TileEntityItemTicker) tileentity, x, y, z, par8);
	}
}
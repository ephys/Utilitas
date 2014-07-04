package nf.fr.ephys.playerproxies.helpers;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class RenderHelper {
	public static void loadTexturesMap() {
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
	}

	public static void renderItem3D(ItemStack item) {
		int maxRenderPasses = item.getItem().getRenderPasses(item.getItemDamage());
		
		for (int i = 0; i < maxRenderPasses; i++) {
			Icon icon = item.getItem().getIcon(item, i);

			if (icon == null)
				continue;

			Color color = new Color(item.getItem().getColorFromItemStack(item, i));
			GL11.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());

			ItemRenderer.renderItemIn2D(Tessellator.instance,
					icon.getMaxU(),
					icon.getMinV(),
					icon.getMinU(),
					icon.getMaxV(), 
					icon.getIconWidth(), 
					icon.getIconHeight(), 
					1F / 16F
			);
		}

		GL11.glColor3f(1F, 1F, 1F);
	}
}

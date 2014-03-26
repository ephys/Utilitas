package nf.fr.ephys.playerproxies.client.renderer;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.IItemRenderer;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class ItemBiomeStorageRenderer implements IItemRenderer {
	private static RenderItem renderItem = new RenderItem();
	
	public static final int COLOR_HOT  = 0xffbb7b;
	public static final int COLOR_COLD = 0x9db4ff;

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true; // all render types
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
		int biomeID = NBTHelper.getInt(itemStack, "biome", -1);
		Icon icon = itemStack.getIconIndex();
		
		if (biomeID == -1) {
			renderItem.renderIcon(0, 0, icon, 16, 16);
			return;
		}
		
		BiomeGenBase biome = BiomeGenBase.biomeList[biomeID];		
		
		int color = (int) ((COLOR_COLD - COLOR_HOT) * biome.temperature) + COLOR_HOT;

		Color colorObj = new Color(color);
		
		GL11.glColor3b((byte) colorObj.getRed(), (byte) colorObj.getGreen(), (byte) colorObj.getBlue());

		renderItem.renderIcon(0, 0, icon, 16, 16);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}

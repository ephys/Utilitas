package nf.fr.ephys.playerproxies.client.renderer;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.IItemRenderer;
import nf.fr.ephys.playerproxies.common.item.ItemBiomeStorage;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class ItemBiomeStorageRenderer implements IItemRenderer {
	private static RenderItem renderItem = new RenderItem();
	
	public static final int COLOR_HOT  = 0xffbb7b;
	public static final int COLOR_COLD = 0x9db4ff;

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return ItemBiomeStorage.hasBiome(item);
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
		byte biomeID = ItemBiomeStorage.getBiome(itemStack);
		Icon icon = itemStack.getIconIndex();
		
		BiomeGenBase biome = BiomeGenBase.biomeList[biomeID];		

		int color = (int) ((COLOR_COLD - COLOR_HOT) * biome.temperature) + COLOR_HOT;

		Color colorObj = new Color(color);
		
		GL11.glColor3b((byte) colorObj.getRed(), (byte) colorObj.getGreen(), (byte) colorObj.getBlue());

		if (type.equals(ItemRenderType.INVENTORY)) {
			renderItem.renderIcon(0, 0, icon, 16, 16);
		} else {
			RenderBlocks renderBlock = (RenderBlocks) data[0];
			EntityClientPlayerMP player = (EntityClientPlayerMP) data[1];
			
			//renderItem.doRenderItem(itemStack., itemStack., par4, par6, par8, par9);
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}

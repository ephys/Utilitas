package nf.fr.ephys.playerproxies.client.renderer;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.item.EntityItem;
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
		return ItemBiomeStorage.hasBiome(item) && type.equals(type.INVENTORY); // TODO: why isn't there a pre/post render event for items ? >_>
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return type.equals(type.ENTITY) && (
					helper.equals(helper.ENTITY_ROTATION) || 
					helper.equals(helper.ENTITY_BOBBING) ||
					helper.equals(helper.BLOCK_3D)
				);
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
		byte biomeID = ItemBiomeStorage.getBiome(itemStack);
		Icon icon = itemStack.getIconIndex();
		
		BiomeGenBase biome = BiomeGenBase.biomeList[biomeID];		

		int color = (int) ((COLOR_COLD - COLOR_HOT) * biome.temperature) + COLOR_HOT;

		Color colorObj = new Color(color);
		
		GL11.glColor3b((byte) colorObj.getRed(), (byte) colorObj.getGreen(), (byte) colorObj.getBlue());

		RenderBlocks renderBlock;
		switch(type) {
			case INVENTORY:
				renderItem.renderIcon(0, 0, icon, 16, 16);
				break;
			case ENTITY:
				renderBlock = (RenderBlocks) data[0];
				EntityItem item = (EntityItem) data[1];
				
				//renderItem.;
				
				break;
			
			case EQUIPPED:
			case EQUIPPED_FIRST_PERSON:
				renderBlock = (RenderBlocks) data[0];
				EntityClientPlayerMP player = (EntityClientPlayerMP) data[1];
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}

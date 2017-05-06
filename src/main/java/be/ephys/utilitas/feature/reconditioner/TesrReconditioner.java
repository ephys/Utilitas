package be.ephys.utilitas.feature.reconditioner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

@SideOnly(Side.CLIENT)
public class TesrReconditioner extends TileEntitySpecialRenderer<TileEntityReconditioner> {

    private final static int BAR_WIDTH = 50;
    private final static int BAR_HEIGHT = 9;
    private final static int LABEL_OFFSET = 1;

    private static final Color XP_COLOR = new Color(22, 147, 73, 128);
    private static final Color ENERGY_COLOR = new Color(114, 0, 0, 128);
    private static final Color EMPTY_COLOR = new Color(0, 0, 0, 64);

    @Override
    public void renderTileEntityAt(TileEntityReconditioner tile, double x, double y, double z, float partialTicks, int destroyStage) {
        int xp = tile.getField(TileEntityReconditioner.FIELD_XP);
        int maxXp = TileEntityReconditioner.MAX_XP_STORAGE;
        float xpPercent = ((float) xp) / maxXp;

        if (Minecraft.getMinecraft().objectMouseOver.getBlockPos().equals(tile.getPos())) {
            drawBar(x + 0.5, y + 1.75, z + 0.5, xpPercent, "XP", XP_COLOR);

            // TODO energy
        }

        renderRepairableItem(tile, x, y, z);
        renderEnchantedBook(tile, x, y, z);
    }

    private void renderEnchantedBook(TileEntityReconditioner tile, double x, double y, double z) {
        ItemStack stack = tile.getStackInSlot(TileEntityReconditioner.SLOT_ENCHANTED_BOOK);
        if (stack == null) {
            return;
        }

        GlStateManager.pushMatrix();

        GlStateManager.translate(x + 0.5, y + 0.75 + (1f / 32), z + 0.35);

        GL11.glRotatef(90, 1.0F, 0.0F, 0.0F);

        renderItemStack(stack);

        GlStateManager.popMatrix();
    }

    private void renderRepairableItem(TileEntityReconditioner tile, double x, double y, double z) {
        ItemStack stack = tile.getStackInSlot(TileEntityReconditioner.SLOT_REPAIRABLE_ITEM);
        if (stack == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 1.125, z + 0.5);

        GL11.glRotatef(tile.getRepairTime(), 0.0F, 1.0F, 0.0F);

        renderItemStack(stack);

        GlStateManager.popMatrix();
    }

    public static void renderItemStack(ItemStack stack) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        float s = 1F;
        GlStateManager.scale(s, s, s);
        mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
        GlStateManager.scale(1F / s, 1F / s, 1F / s);
        GlStateManager.popMatrix();
    }

    private void drawBar(double x, double y, double z, float percent, String label, Color fillColor) {

        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        float viewerYaw = renderManager.playerViewY;
        float viewerPitch = renderManager.playerViewX;
        boolean isThirdPersonFrontal = renderManager.options.thirdPersonView == 2;
        FontRenderer fontRendererIn = renderManager.getFontRenderer();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();

//        VertexBuffer vertexbuffer = tessellator.getBuffer();
//        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
//        vertexbuffer.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
//        vertexbuffer.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
//        vertexbuffer.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
//        vertexbuffer.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
//        tessellator.draw();

        int labelWidth = fontRendererIn.getStringWidth(label);

        int xOffset = labelWidth / 2;

        int xStart = -(BAR_WIDTH + LABEL_OFFSET + labelWidth) / 2 - xOffset;
        int yStart = -(BAR_HEIGHT / 2);

        int textOffset = labelWidth + LABEL_OFFSET;

        float filledBarWidth = percent * BAR_WIDTH;
        float emptyBarWidth = (1 - percent) * BAR_WIDTH;

        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos(xStart + textOffset, yStart, 0.0D).color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), fillColor.getAlpha()).endVertex();
        vertexbuffer.pos(xStart + textOffset, yStart + BAR_HEIGHT, 0.0D).color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), fillColor.getAlpha()).endVertex();
        vertexbuffer.pos(xStart + textOffset + filledBarWidth, yStart + BAR_HEIGHT, 0.0D).color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), fillColor.getAlpha()).endVertex();
        vertexbuffer.pos(xStart + textOffset + filledBarWidth, yStart, 0.0D).color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), fillColor.getAlpha()).endVertex();
        tessellator.draw();

        VertexBuffer vertexbuffer2 = tessellator.getBuffer();
        vertexbuffer2.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer2.pos(xStart + textOffset + filledBarWidth, yStart, 0.0D).color(EMPTY_COLOR.getRed(), EMPTY_COLOR.getGreen(), EMPTY_COLOR.getBlue(), EMPTY_COLOR.getAlpha()).endVertex();
        vertexbuffer2.pos(xStart + textOffset + filledBarWidth, yStart + BAR_HEIGHT, 0.0D).color(EMPTY_COLOR.getRed(), EMPTY_COLOR.getGreen(), EMPTY_COLOR.getBlue(), EMPTY_COLOR.getAlpha()).endVertex();
        vertexbuffer2.pos(xStart + textOffset + BAR_WIDTH, yStart + BAR_HEIGHT, 0.0D).color(EMPTY_COLOR.getRed(), EMPTY_COLOR.getGreen(), EMPTY_COLOR.getBlue(), EMPTY_COLOR.getAlpha()).endVertex();
        vertexbuffer2.pos(xStart + textOffset + BAR_WIDTH, yStart, 0.0D).color(EMPTY_COLOR.getRed(), EMPTY_COLOR.getGreen(), EMPTY_COLOR.getBlue(), EMPTY_COLOR.getAlpha()).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();

        GlStateManager.depthMask(true);
        fontRendererIn.drawString(label, xStart, yStart + 1, -1);

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}

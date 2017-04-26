package be.ephys.utilitas.client.renderer;

import be.ephys.utilitas.common.tileentity.TileEntityInterface;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityInterfaceRenderer extends TileEntitySpecialRenderer<TileEntityInterface> {

    @Override
    public void renderTileEntityAt(TileEntityInterface tile, double x, double y, double z, float partialTicks, int destroyStage) {
        super.renderTileEntityAt(tile, x, y, z, partialTicks, destroyStage);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);

        final float scale = 0.4375F;
        GL11.glScalef(scale, scale, scale);

        tile.getAdapter().renderInventory(tile.tick, x, y, z, tickTime);
        GL11.glPopMatrix();
    }
}

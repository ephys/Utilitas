package nf.fr.ephys.playerproxies.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityInterfaceRenderer extends TileEntitySpecialRenderer {
	public static final RenderBlocks renderBlocksInstance = new RenderBlocks();

	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tickTime) {
		TileEntityInterface tile = (TileEntityInterface) te;
		
		if (tile.getInterface() == null) return;
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);

		//GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslatef(0.0F, 0.5F, 0.0F);
		
		final float scale = 0.4375F;
		GL11.glScalef(scale, scale, scale);
		
		tile.getInterface().renderInventory(tile.tick++, x, y, z, tickTime);
		
		GL11.glPopMatrix();
	}
}

package nf.fr.ephys.playerproxies.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityBlockInterfaceRenderer extends TileEntitySpecialRenderer {
	private static final RenderBlocks renderBlocksInstance = new RenderBlocks();
	private static int tick = 0;

	public static void renderBlockInterface(TEBlockInterface bi, double par1, double par3, double par5, float par7) {
		final float scale = 0.4375F;
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslatef(0.0F, 0.5F, 0.0F);
		GL11.glRotatef(tick++, 0.0F, 1.0F, 0.0F);
		GL11.glScalef(scale, scale, scale);
		
		int invtype = bi.getCurrentInventoryType();
		
		if (invtype == TEBlockInterface.INVTYPE_TE || invtype == TEBlockInterface.INVTYPE_TURTLE) {
			GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);

			TileEntityBlockInterfaceRenderer.renderBlocksInstance.renderBlockAsItem(Block.chest, 0, 1.0F);
		} else if(invtype == TEBlockInterface.INVTYPE_PLAYER) {
			//EntityPlayer player = (bi.getLinkedPlayer() == null)?Minecraft.getMinecraft().thePlayer:bi.getLinkedPlayer(); // TODO: fix this too
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			
			RenderManager.instance.getEntityRenderObject(player).doRender(player, 0.0D, 0.5D, 0.0D, 1.0F, par7);
		}/* else {
			GL11.glScalef(1.4F, 1.4F, 1.4F);
			EntityItem entityitem = new EntityItem(bi.worldObj, 0.0D, 0.0D, 0.0D, new ItemStack(Item.enderPearl));
			
			RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
		}*/ // TODO solve entity's shaking
	}

	public void renderTileEntityAt(TileEntity te, double par2, double par4,
			double par6, float par8) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) par2 + 0.5F, (float) par4,
				(float) par6 + 0.5F);
		renderBlockInterface((TEBlockInterface) te, par2, par4, par6, par8);
		GL11.glPopMatrix();
	}
}

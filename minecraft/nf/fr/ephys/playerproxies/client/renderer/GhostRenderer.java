package nf.fr.ephys.playerproxies.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;
import nf.fr.ephys.playerproxies.common.entity.Ghost;

public class GhostRenderer {
	Minecraft mc = Minecraft.getMinecraft();

	@ForgeSubscribe
	public void PlayerPrerenderer(RenderPlayerEvent.Pre event) {
		if (event.entityPlayer instanceof Ghost) {
			System.out.println("2");
			Ghost ghost = (Ghost)event.entityPlayer;
			
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_NORMALIZE);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			RenderManager.instance.getEntityRenderObject(event.entityPlayer).doRender(event.entityPlayer, 0.0D, 0.5D, 0.0D, 1.0F, 0);
			
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glPopMatrix();
			
			event.setCanceled(true);
		}
	}
}

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
	@ForgeSubscribe
	public void PlayerPrerender(RenderPlayerEvent.Pre event) {
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
		
		GL11.glColor3f(2, 2, 2);
	}

	@ForgeSubscribe
	public void PlayerPrerender(RenderPlayerEvent.Specials.Pre e2) {
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
		
		GL11.glColor3f(2, 2, 2);
	}
	
	@ForgeSubscribe
	public void PlayerPostrender(RenderPlayerEvent.Post event) {
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
	}
	
	@ForgeSubscribe
	public void PlayerPostrender(RenderPlayerEvent.Specials.Post e2) {
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
	}
}
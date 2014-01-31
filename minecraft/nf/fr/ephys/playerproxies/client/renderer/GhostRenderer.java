package nf.fr.ephys.playerproxies.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import nf.fr.ephys.playerproxies.common.entity.Ghost;

public class GhostRenderer extends RenderPlayer {
	private ModelBiped modelBipedMain;
	private static final ResourceLocation steveTextures = new ResourceLocation(
			"textures/entity/steve.png");

	public GhostRenderer() {
		super();
		this.modelBipedMain = (ModelBiped) this.mainModel;
		setRenderPassModel(this.modelBipedMain);
	}

	private void renderGhost(Ghost ghost, double par2, double par4,
			double par6, float par8, float par9) {

		float f2 = 1.0F;
		GL11.glColor3f(f2, f2, f2);

		this.modelBipedMain.isSneak = ghost.isSneaking();
		double d3 = par4 - (double) ghost.yOffset;

		if (ghost.isSneaking()) {
			d3 -= 0.125D;
		}

		doRenderLiving(ghost, par2, d3, par6, par8, par9);
	}

	private float interpolateRotation(float par1, float par2, float par3) {
		float f3;

		for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F) {
			;
		}

		while (f3 >= 180.0F) {
			f3 -= 360.0F;
		}

		return par1 + par3 * f3;
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase,
			double par2, double par4, double par6, float par8, float par9) {

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);

		this.mainModel.onGround = this.renderSwingProgress(
				par1EntityLivingBase, par9);
		if (this.renderPassModel != null) {
			this.renderPassModel.onGround = this.mainModel.onGround;
		}

		this.mainModel.isRiding = false;
		if (this.renderPassModel != null) {
			this.renderPassModel.isRiding = false;
		}

		this.mainModel.isChild = false;
		if (this.renderPassModel != null) {
			this.renderPassModel.isChild = false;
		}

		try {
			float f2 = this.interpolateRotation(
					par1EntityLivingBase.prevRenderYawOffset,
					par1EntityLivingBase.renderYawOffset, par9);
			float f3 = par1EntityLivingBase.rotationYawHead;

			float f5 = par1EntityLivingBase.prevRotationPitch
					+ (par1EntityLivingBase.rotationPitch - par1EntityLivingBase.prevRotationPitch)
					* par9;

			this.renderLivingAt(par1EntityLivingBase, par2, par4, par6);

			float f4 = this.handleRotationFloat(par1EntityLivingBase, par9);
			this.rotateCorpse(par1EntityLivingBase, f4, f2, par9);

			float f6 = 0.0625F;
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glScalef(-1.0F, -1.0F, 1.0F);

			this.preRenderCallback(par1EntityLivingBase, par9);
			GL11.glTranslatef(0.0F, -24.0F * f6 - 0.0078125F, 0.0F);
			float f7 = par1EntityLivingBase.prevLimbSwingAmount
					+ (par1EntityLivingBase.limbSwingAmount - par1EntityLivingBase.prevLimbSwingAmount)
					* par9;
			float f8 = par1EntityLivingBase.limbSwing
					- par1EntityLivingBase.limbSwingAmount * (1.0F - par9);

			if (f7 > 1.0F) {
				f7 = 1.0F;
			}

			GL11.glEnable(GL11.GL_ALPHA_TEST);
			this.mainModel.setLivingAnimations(par1EntityLivingBase, f8, f7,
					par9);
			this.renderModel(par1EntityLivingBase, f8, f7, f4, f3 - f2, f5, f6);

			GL11.glDepthMask(true);
			this.renderEquippedItems(par1EntityLivingBase, par9);
			float f14 = par1EntityLivingBase.getBrightness(par9);
			int i = this.getColorMultiplier(par1EntityLivingBase, f14, par9);
			OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

			if ((i >> 24 & 255) > 0 || par1EntityLivingBase.hurtTime > 0
					|| par1EntityLivingBase.deathTime > 0) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glDepthFunc(GL11.GL_EQUAL);

				if (par1EntityLivingBase.hurtTime > 0
						|| par1EntityLivingBase.deathTime > 0) {
					GL11.glColor4f(f14, 0.0F, 0.0F, 0.4F);
					this.mainModel.render(par1EntityLivingBase, f8, f7, f4, f3
							- f2, f5, f6);

					for (int l = 0; l < 4; ++l) {
						if (this.inheritRenderPass(par1EntityLivingBase, l,
								par9) >= 0) {
							GL11.glColor4f(f14, 0.0F, 0.0F, 0.4F);
							this.renderPassModel.render(par1EntityLivingBase,
									f8, f7, f4, f3 - f2, f5, f6);
						}
					}
				}

				if ((i >> 24 & 255) > 0) {
					float f9 = (float) (i >> 16 & 255) / 255.0F;
					float f10 = (float) (i >> 8 & 255) / 255.0F;
					float f15 = (float) (i & 255) / 255.0F;
					float f11 = (float) (i >> 24 & 255) / 255.0F;
					GL11.glColor4f(f9, f10, f15, f11);
					this.mainModel.render(par1EntityLivingBase, f8, f7, f4, f3
							- f2, f5, f6);

					for (int i1 = 0; i1 < 4; ++i1) {
						if (this.inheritRenderPass(par1EntityLivingBase, i1,
								par9) >= 0) {
							GL11.glColor4f(f9, f10, f15, f11);
							this.renderPassModel.render(par1EntityLivingBase,
									f8, f7, f4, f3 - f2, f5, f6);
						}
					}
				}

				GL11.glDepthFunc(GL11.GL_LEQUAL);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
		this.passSpecialRender(par1EntityLivingBase, par2, par4, par6);
	}

	protected void renderModel(EntityLivingBase par1EntityLivingBase,
			float par2, float par3, float par4, float par5, float par6,
			float par7) {
		this.bindEntityTexture(par1EntityLivingBase);
		
		GL11.glPushMatrix();
		GL11.glColor4f(0.8F, 0.8F, 0.8F, 0.35F);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
		this.mainModel.render(par1EntityLivingBase, par2, par3, par4, par5, par6, par7);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
	}

	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase,
			int j, float par3) {
		return j;
	}

	public void doRender(Entity par1Entity, double par2, double par4,
			double par6, float par8, float par9) {

		this.renderGhost((Ghost) par1Entity, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity entity) {
		ResourceLocation skin = ((Ghost) entity).getLocationSkin();
		return skin == null ? steveTextures : skin;
	}

	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase,
			float par2) {
		float scale = 0.9375F;
		GL11.glScalef(scale, scale, scale);
	}

	protected void renderLivingAt(EntityLivingBase entity, double x, double y,
			double z) {
		
		GL11.glTranslatef((float) x,
				(float) y + ((Ghost) entity).getNextHoveringFloat(), (float) z);
	}

	protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase,
			float par2) {
	}

	protected void rotateCorpse(EntityLivingBase par1EntityLivingBase,
			float par2, float par3, float par4) {
		GL11.glRotatef(180.0F - par3, 0.0F, 1.0F, 0.0F);
	}

	protected void func_96449_a(EntityLivingBase par1EntityLivingBase,
			double par2, double par4, double par6, String par8Str, float par9,
			double par10) {
	}
}
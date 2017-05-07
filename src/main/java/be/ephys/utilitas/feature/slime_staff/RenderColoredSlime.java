package be.ephys.utilitas.feature.slime_staff;

import be.ephys.utilitas.Utilitas;
import be.ephys.utilitas.base.helpers.RenderHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderColoredSlime extends RenderSlime {

    private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation(Utilitas.MODID + ":textures/entity/rainbow_slime.png");
    private static final ModelBase SLIME_MODEL = new ModelSlime(16);

    public RenderColoredSlime(RenderManager renderManagerIn) {
        super(renderManagerIn, SLIME_MODEL, 0.25f);
    }

    protected ResourceLocation getEntityTexture(EntitySlime entity) {
        return SLIME_TEXTURES;
    }

    @Override
    protected void preRenderCallback(EntitySlime entitylivingbaseIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);

        if (entitylivingbaseIn instanceof EntityColoredSlime) {
            int color = ((EntityColoredSlime) entitylivingbaseIn).getSlimeColor();
            RenderHelper.glColorRgb(color);
        }
    }
}

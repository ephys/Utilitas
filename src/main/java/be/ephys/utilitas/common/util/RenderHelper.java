package be.ephys.utilitas.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHelper {

    public static final ModelBiped MODEL_BIPED = new ModelBiped(0.0F);

    public static void renderSimpleBiped(ResourceLocation skin, float tickTime) {
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(skin);

        MODEL_BIPED.bipedHead.render(tickTime);
        MODEL_BIPED.bipedBody.render(tickTime);
        MODEL_BIPED.bipedRightArm.render(tickTime);
        MODEL_BIPED.bipedLeftArm.render(tickTime);
        MODEL_BIPED.bipedRightLeg.render(tickTime);
        MODEL_BIPED.bipedLeftLeg.render(tickTime);
        MODEL_BIPED.bipedHeadwear.render(tickTime);
    }
}

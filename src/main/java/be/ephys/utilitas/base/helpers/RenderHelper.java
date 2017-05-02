package be.ephys.utilitas.base.helpers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class RenderHelper {

    private static final ResourceLocation TEXTURE_STEVE = DefaultPlayerSkin.getDefaultSkinLegacy();

    public static final ModelBiped MODEL_STEVE = new ModelPlayer(0.0F, false);
    public static final ModelBiped MODEL_ALEX = new ModelPlayer(0.0F, true);

    public static void renderPlayer(SkinData skinData, float tickTime) {
        if (!skinData.isReady()) {
            renderSimpleBiped(TEXTURE_STEVE, MODEL_STEVE, tickTime);
            return;
        }

        if (skinData.skinType == SkinType.ALEX) {
            renderSimpleBiped(skinData.skin, MODEL_ALEX, tickTime);
        } else {
            renderSimpleBiped(skinData.skin, MODEL_STEVE, tickTime);
        }
    }

    public static SkinData getPlayerSkin(GameProfile gameProfile) {
        return new SkinData(gameProfile);
    }

    public static void renderSimpleBiped(ResourceLocation skin, ModelBiped model, float tickTime) {
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(skin);

        model.bipedHead.render(tickTime);
        model.bipedBody.render(tickTime);
        model.bipedRightArm.render(tickTime);
        model.bipedLeftArm.render(tickTime);
        model.bipedRightLeg.render(tickTime);
        model.bipedLeftLeg.render(tickTime);
        model.bipedHeadwear.render(tickTime);
    }

    public static TextureAtlasSprite getFluidTexture(FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        ResourceLocation fluidStillResourceLoc = fluid.getStill(fluidStack);

        if (fluidStillResourceLoc == null) {
            return null;
        }

        TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
        return textureMapBlocks.getTextureExtry(fluidStillResourceLoc.toString());
    }

    public static TextureAtlasSprite getFluidTexture(Fluid fluid) {
        ResourceLocation fluidStillResourceLoc = fluid.getStill();

        if (fluidStillResourceLoc == null) {
            return null;
        }

        TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
        return textureMapBlocks.getTextureExtry(fluidStillResourceLoc.toString());
    }

    public enum SkinType {
        ALEX("slim"),
        STEVE("default");

        public final String type;

        SkinType(String s) {
            this.type = s;
        }

        private static SkinType from(String skinType) {
            if (skinType == null) {
                return STEVE;
            }

            if (ALEX.type.equals(skinType)) {
                return ALEX;
            }

            return STEVE;
        }
    }

    public static class SkinData {

        public final GameProfile gameProfile;
        private ResourceLocation skin;
        private SkinType skinType = null;

        public SkinData(GameProfile gameProfile) {
            this.gameProfile = gameProfile;

            this.load();
        }

        public boolean isReady() {
            return this.skinType != null;
        }

        private void load() {
            Minecraft minecraft = Minecraft.getMinecraft();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(gameProfile);

            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                MinecraftProfileTexture texture = map.get(MinecraftProfileTexture.Type.SKIN);
                this.skin = minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
                this.skinType = SkinType.from(texture.getMetadata("model"));
            } else {
                UUID uuid = EntityPlayer.getUUID(gameProfile);
                this.skin = DefaultPlayerSkin.getDefaultSkin(uuid);
                this.skinType = SkinType.from(DefaultPlayerSkin.getSkinType(uuid));
            }
        }
    }
}

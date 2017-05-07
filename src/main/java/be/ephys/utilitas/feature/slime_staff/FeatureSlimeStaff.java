package be.ephys.utilitas.feature.slime_staff;

import be.ephys.utilitas.Utilitas;
import be.ephys.utilitas.base.feature.Feature;
import be.ephys.utilitas.base.feature.FeatureInstance;
import be.ephys.utilitas.base.feature.FeatureMeta;
import be.ephys.utilitas.feature.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@FeatureMeta(
    name = "Slime Staff",
    description = "A cute slime familiar which follows you around"
)
public class FeatureSlimeStaff extends Feature {

    @FeatureInstance
    public static FeatureSlimeStaff INSTANCE;

    private final ItemSlimeStaff slimeStaff = new ItemSlimeStaff();
    public final ItemRainbowSlimeBall rainbowSlimeBall = new ItemRainbowSlimeBall();

    @Override
    public void registerContents(FMLPreInitializationEvent event) {
        GameRegistry.register(slimeStaff);
        slimeStaff.setCreativeTab(Utilitas.CREATIVE_TAB);

        GameRegistry.register(rainbowSlimeBall);
        rainbowSlimeBall.setCreativeTab(Utilitas.CREATIVE_TAB);

        EntityRegistry.registerModEntity(
            EntityRainbowSlime.class,
            EntityRainbowSlime.ENTITY_NAME,
            ModEntities.RAINBOW_SLIME,
            Utilitas.instance,
            80,
            3,
            true,
            0xf0a5a2,
            0x7ebf6e
        );

        LootTableList.register(new ResourceLocation(Utilitas.MODID,"entities/" + EntityRainbowSlime.ENTITY_NAME));

        EntityRegistry.registerModEntity(
            EntitySlimeFamiliar.class,
            EntitySlimeFamiliar.ENTITY_NAME,
            ModEntities.SLIME_FAMILIAR,
            Utilitas.instance,
            80,
            3,
            true
        );
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void preInitClient(FMLPreInitializationEvent event) {
        ModelLoader.setCustomModelResourceLocation(slimeStaff, ItemSlimeStaff.META_CLEAN, new ModelResourceLocation(slimeStaff.getRegistryName() + "-clean", "inventory"));
        ModelLoader.setCustomModelResourceLocation(slimeStaff, ItemSlimeStaff.META_DIRTY, new ModelResourceLocation(slimeStaff.getRegistryName() + "-dirty", "inventory"));

        ModelLoader.setCustomModelResourceLocation(rainbowSlimeBall, 0, new ModelResourceLocation(rainbowSlimeBall.getRegistryName(), "inventory"));

        RenderingRegistry.registerEntityRenderingHandler(EntityRainbowSlime.class, RenderColoredSlime::new);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(FMLInitializationEvent event) {
        ItemColors colorRegistry = Minecraft.getMinecraft().getItemColors();

        colorRegistry.registerItemColorHandler(slimeStaff.getColorHandler(), slimeStaff);
        colorRegistry.registerItemColorHandler(rainbowSlimeBall.getColorHandler(), rainbowSlimeBall);
    }
}

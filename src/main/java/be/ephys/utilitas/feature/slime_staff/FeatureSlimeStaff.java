package be.ephys.utilitas.feature.slime_staff;

import be.ephys.utilitas.Utilitas;
import be.ephys.utilitas.base.feature.Feature;
import be.ephys.utilitas.base.feature.FeatureMeta;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@FeatureMeta(
    name = "Slime Staff",
    description = "A cute slime familiar which follows you around"
)
public class FeatureSlimeStaff extends Feature {

    private final ItemSlimeStaff slimeStaff = new ItemSlimeStaff();

    @Override
    public void registerContents(FMLPreInitializationEvent event) {
        GameRegistry.register(slimeStaff);

        slimeStaff.setCreativeTab(Utilitas.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void preInitClient(FMLPreInitializationEvent event) {
        ModelLoader.setCustomModelResourceLocation(slimeStaff, ItemSlimeStaff.META_CLEAN, new ModelResourceLocation(slimeStaff.getRegistryName() + "-clean", "inventory"));
        ModelLoader.setCustomModelResourceLocation(slimeStaff, ItemSlimeStaff.META_DIRTY, new ModelResourceLocation(slimeStaff.getRegistryName() + "-dirty", "inventory"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(FMLInitializationEvent event) {
        ItemColors colorRegistry = Minecraft.getMinecraft().getItemColors();
        colorRegistry.registerItemColorHandler(slimeStaff.getColorHandler(), slimeStaff);
    }
}

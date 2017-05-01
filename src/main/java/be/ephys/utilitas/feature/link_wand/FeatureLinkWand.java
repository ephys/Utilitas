package be.ephys.utilitas.feature.link_wand;

import be.ephys.utilitas.Utilitas;
import be.ephys.utilitas.base.feature.Feature;
import be.ephys.utilitas.base.feature.FeatureMeta;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@FeatureMeta(
    name = "Link Wand",
    description = "A wand which links blocks and entities"
)
public class FeatureLinkWand extends Feature {

    public static final ItemLinker LINK_WAND = new ItemLinker();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.register(LINK_WAND);
        MinecraftForge.EVENT_BUS.register(LINK_WAND);

        LINK_WAND.setCreativeTab(Utilitas.CREATIVE_TAB);
        Utilitas.CREATIVE_TAB.setIconItem(LINK_WAND);
    }

    @Override
    public void preInitClient(FMLPreInitializationEvent event) {
        ModelLoader.setCustomModelResourceLocation(LINK_WAND, 0, new ModelResourceLocation(LINK_WAND.getRegistryName(), "inventory"));
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        GameRegistry.addRecipe(
            new ItemStack(LINK_WAND),
            " il", " si", "s  ",
            'l', new ItemStack(Items.END_CRYSTAL),
            'i', new ItemStack(Items.IRON_INGOT),
            's', new ItemStack(Items.STICK)
        );
    }
}

package be.ephys.utilitas.feature.reconditioner;

import be.ephys.utilitas.Utilitas;
import be.ephys.utilitas.base.feature.Config;
import be.ephys.utilitas.base.feature.Feature;
import be.ephys.utilitas.base.feature.FeatureInstance;
import be.ephys.utilitas.base.feature.FeatureMeta;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@FeatureMeta(
    name = "Reconditioner",
    description = "Applies the Mending enchantment"
)
public class FeatureReconditioner extends Feature {

    @FeatureInstance
    public static FeatureReconditioner INSTANCE;

    @Config(description = "How much RF/mana (botania) are required per tick of repairing")
    public int powerUsage = 0;

    @Config(description = "The type of power to consume")
    public PowerType powerType = PowerType.FORGE_ENERGY;

    public final BlockReconditioner reconditioner = new BlockReconditioner();

    public final ItemBlock reconditionerItemBlock = new ItemBlock(reconditioner);

    @Override
    public void registerContents(FMLPreInitializationEvent event) {
        GameRegistry.register(reconditioner);

        reconditionerItemBlock.setRegistryName(reconditioner.getRegistryName());
        GameRegistry.register(reconditionerItemBlock);
        GameRegistry.registerTileEntity(TileEntityReconditioner.class, reconditioner.getRegistryName().toString());

        reconditioner.setCreativeTab(Utilitas.CREATIVE_TAB);
    }

    @Override
    public void preInitClient(FMLPreInitializationEvent event) {
        ModelLoader.setCustomModelResourceLocation(reconditionerItemBlock, 0, new ModelResourceLocation(reconditionerItemBlock.getRegistryName(), "inventory"));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityReconditioner.class, new TesrReconditioner());
    }

    public enum PowerType {
        BOTANIA_MANA, FORGE_ENERGY
    }
}

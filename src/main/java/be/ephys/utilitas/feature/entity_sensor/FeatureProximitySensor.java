package be.ephys.utilitas.feature.entity_sensor;

import be.ephys.utilitas.base.feature.Feature;
import be.ephys.utilitas.base.feature.FeatureInstance;
import be.ephys.utilitas.base.feature.FeatureMeta;
import be.ephys.utilitas.feature.link_wand.FeatureLinkWand;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@FeatureMeta(
    name = "Proximity Sensor",
    description = "Emits a redstone signal when a given entity is nearby",
    dependencies = FeatureLinkWand.class
)
public class FeatureProximitySensor extends Feature {

    @FeatureInstance
    public static FeatureProximitySensor INSTANCE;

    public final BlockProximitySensor proximitySensor = new BlockProximitySensor(Material.IRON);

    private final ItemBlock proximitySensorItemBlock = new ItemBlock(proximitySensor);

    @Override
    public void registerContents(FMLPreInitializationEvent event) {

        GameRegistry.register(proximitySensor);
        proximitySensorItemBlock.setRegistryName(proximitySensor.getRegistryName());
        GameRegistry.register(proximitySensorItemBlock);

        GameRegistry.registerTileEntity(TileEntityProximitySensor.class, "proximity_sensor");
    }

    @Override
    public void registerCrafts(FMLInitializationEvent event) {
        GameRegistry.addRecipe(
            new ItemStack(proximitySensor),
            "hhh", "hlh", "hrh",
            'h', new ItemStack(Blocks.STONE),
            'l', new ItemStack(Items.END_CRYSTAL),
            'r', new ItemStack(Items.REDSTONE)
        );
    }
}

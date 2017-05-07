package be.ephys.utilitas.feature.material;

import be.ephys.utilitas.Utilitas;
import be.ephys.utilitas.base.feature.Feature;
import be.ephys.utilitas.base.feature.FeatureInstance;
import be.ephys.utilitas.base.feature.FeatureMeta;
import be.ephys.utilitas.base.helpers.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@FeatureMeta(name = "Material", description = "Base mod materials")
public class FeatureMaterial extends Feature{

    @FeatureInstance
    public static FeatureMaterial INSTANCE;

    public Block hardenedStone = new Block(Material.ROCK, MapColor.OBSIDIAN);
    public ItemBlock hardenedStoneItemBlock = new ItemBlock(hardenedStone);

    @Override
    public void registerContents(FMLPreInitializationEvent event) {
        ItemHelper.name(hardenedStone, "hardened_stone");

        hardenedStone.setHardness(2F)
            .setResistance(2000.0F)
            .setCreativeTab(Utilitas.CREATIVE_TAB);

        GameRegistry.register(hardenedStone);

        hardenedStoneItemBlock.setRegistryName(hardenedStone.getRegistryName());

        GameRegistry.register(hardenedStoneItemBlock);
    }

    @Override
    public void registerCrafts(FMLInitializationEvent event) {
        super.registerCrafts(event);

        GameRegistry.addRecipe(
            new ItemStack(hardenedStone, 16),
            "sis",
            "sis",
            "sis",
            'i', new ItemStack(Items.IRON_INGOT),
            's', new ItemStack(Blocks.STONE)
        );
    }

    @Override
    public void preInitClient(FMLPreInitializationEvent event) {
        ModelLoader.setCustomModelResourceLocation(hardenedStoneItemBlock, 0, new ModelResourceLocation(hardenedStoneItemBlock.getRegistryName(), "inventory"));
    }
}

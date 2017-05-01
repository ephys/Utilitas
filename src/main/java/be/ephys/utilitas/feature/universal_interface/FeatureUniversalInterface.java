package be.ephys.utilitas.feature.universal_interface;

import be.ephys.utilitas.Utilitas;
import be.ephys.utilitas.api.registry.UniversalInterfaceAdapter;
import be.ephys.utilitas.api.registry.UniversalInterfaceRegistry;
import be.ephys.utilitas.base.feature.Feature;
import be.ephys.utilitas.base.feature.FeatureMeta;
import be.ephys.utilitas.base.item.MultiItemBlock;
import be.ephys.utilitas.feature.link_wand.FeatureLinkWand;
import be.ephys.utilitas.feature.universal_interface.interface_adapters.*;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

@FeatureMeta(
    name = "Universal Interface",
    description = "A remote interface with support for too much",
    dependencies = FeatureLinkWand.class
)
public class FeatureUniversalInterface extends Feature {

    public static final BlockShinyGlass SHINY_GLASS = new BlockShinyGlass();
    public static final ItemInterfaceUpgrade UPGRADE = new ItemInterfaceUpgrade();

    private ItemBlock shinyGlassItemBlock;

    @Override
    public void registerContents(FMLPreInitializationEvent event) {
        GameRegistry.register(SHINY_GLASS);
        GameRegistry.register(UPGRADE);

        UPGRADE.setCreativeTab(Utilitas.CREATIVE_TAB);
        SHINY_GLASS.setCreativeTab(Utilitas.CREATIVE_TAB);

        shinyGlassItemBlock = new MultiItemBlock(SHINY_GLASS);
        shinyGlassItemBlock.setRegistryName(SHINY_GLASS.getRegistryName());
        GameRegistry.register(shinyGlassItemBlock);

        Utilitas.registerTile(TileEntityInterface.class, "universal_interface");
    }

    @Override
    public void registerCrafts(FMLInitializationEvent event) {
        GameRegistry.addRecipe(
            new ItemStack(SHINY_GLASS, 12, BlockShinyGlass.METADATA_GLASS),
            "ggg", "gdg", "ggg",
            'd', new ItemStack(Items.DIAMOND),
            'g', new ItemStack(Blocks.GLASS)
        );

        GameRegistry.addRecipe(
            new ItemStack(SHINY_GLASS, 1, BlockShinyGlass.METADATA_INTERFACE),
            "dld", "geg", "dgd",
            'd', new ItemStack(Items.DIAMOND),
            'l', new ItemStack(Items.END_CRYSTAL),
            'g', new ItemStack(SHINY_GLASS, BlockShinyGlass.METADATA_INTERFACE),
            'e', new ItemStack(Blocks.ENDER_CHEST)
        );

        GameRegistry.addRecipe(new ShapedOreRecipe(
            new ItemStack(UPGRADE, 1, ItemInterfaceUpgrade.CROSS_DIM),
            "dnd",
            "ppp",
            'p', Items.PAPER,
            'n', Items.EMERALD,
            'd', "dyeBlue")
        );

        GameRegistry.addRecipe(new ShapedOreRecipe(
            new ItemStack(UPGRADE, 1, ItemInterfaceUpgrade.WIRELESS),
            "ppp",
            "dnd",
            "ppp",
            'p', Items.PAPER,
            'n', Items.NETHER_STAR,
            'd', "dyeOrange")
        );

        GameRegistry.addRecipe(new ShapedOreRecipe(
            new ItemStack(UPGRADE, 1, ItemInterfaceUpgrade.FLUID_HANDLER),
            "ppp",
            "dnd",
            "ppp",
            'p', Items.PAPER,
            'n', Items.BUCKET,
            'd', "dyeCyan")
        );
    }

    @Override
    public void preInitClient(FMLPreInitializationEvent event) {
        ModelLoader.setCustomModelResourceLocation(UPGRADE, ItemInterfaceUpgrade.CROSS_DIM, new ModelResourceLocation(UPGRADE.getRegistryName() + "-cross_dim", "inventory"));
        ModelLoader.setCustomModelResourceLocation(UPGRADE, ItemInterfaceUpgrade.WIRELESS, new ModelResourceLocation(UPGRADE.getRegistryName() + "-wireless", "inventory"));
        ModelLoader.setCustomModelResourceLocation(UPGRADE, ItemInterfaceUpgrade.FLUID_HANDLER, new ModelResourceLocation(UPGRADE.getRegistryName() + "-fluid_handler", "inventory"));

        ModelLoader.setCustomModelResourceLocation(shinyGlassItemBlock, 0, new ModelResourceLocation(SHINY_GLASS.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(shinyGlassItemBlock, 1, new ModelResourceLocation(SHINY_GLASS.getRegistryName(), "inventory"));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInterface.class, new TesrInterface());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PlayerInventoryRegistry.EventHandler());

        UniversalInterfaceRegistry.addInterface(InterfaceTileEntity.class, TileEntity.class);
        UniversalInterfaceRegistry.addInterface(InterfacePlayer.class, EntityPlayer.class);
        UniversalInterfaceRegistry.addInterface(InterfaceJukebox.class, BlockJukebox.TileEntityJukebox.class);
        UniversalInterfaceRegistry.addInterface(InterfaceMinecart.class, EntityMinecartContainer.class);
        UniversalInterfaceRegistry.addInterface(InterfaceMinecartFurnace.class, EntityMinecartFurnace.class);
        UniversalInterfaceRegistry.addInterface(InterfaceItemFrame.class, EntityItemFrame.class);
    }

    @Override
    public void handleImc(FMLInterModComms.IMCMessage message, String key) {
        if (!message.key.equalsIgnoreCase("add-interface-handler")) {
            Utilitas.getLogger().warn("Unknown IMC key " + key);
            return;
        }

        if (!message.isStringMessage()) {
            Utilitas.getLogger().warn("Wrong call to add-interface-handler from " + message.getSender() + ". Format is (string) 'handlerClass:targetClass'");
            return;
        }

        String[] classes = message.getStringValue().split(":");
        if (classes.length != 2) {
            Utilitas.getLogger().warn("Wrong call to add-interface-handler from " + message.getSender() + ". Format is (string) 'handlerClass:targetClass', got " + message.getStringValue());
            return;
        }

        Class handler, target;
        try {
            handler = Class.forName(classes[0]);
            target = Class.forName(classes[1]);
        } catch (ClassNotFoundException e) {
            Utilitas.getLogger().warn("Wrong call to add-interface-handler from " + message.getSender() + ". Could not find handler or target class");
            e.printStackTrace();
            return;
        }

        if (!UniversalInterfaceAdapter.class.isAssignableFrom(handler)) {
            Utilitas.getLogger().warn("Wrong call to add-interface-handler from " + message.getSender() + ". Handler should be an instance of " + UniversalInterfaceAdapter.class);
            return;
        }

        // noinspection unchecked
        UniversalInterfaceRegistry.addInterface((Class<? extends UniversalInterfaceAdapter<Object>>) handler, target);
    }
}

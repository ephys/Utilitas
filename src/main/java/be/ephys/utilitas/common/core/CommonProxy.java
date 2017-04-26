package be.ephys.utilitas.common.core;

import be.ephys.utilitas.common.Utilitas;
import be.ephys.utilitas.common.block.BlockHomeShield;
import be.ephys.utilitas.common.registry.UniversalInterfaceRegistry;
import be.ephys.utilitas.common.registry.interface_adapters.*;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        Items.POTIONITEM.setContainerItem(Items.GLASS_BOTTLE);
    }

    public void init(FMLInitializationEvent event) {
        register();
        registerPacket();

        IMCHandler.sendMessages();
        NetworkRegistry.INSTANCE.registerGuiHandler(Utilitas.instance, new GuiHandler());
    }

    public void postInit(FMLPostInitializationEvent event) {
        registerHandlers();
        registerCrafts();

        if (BlockHomeShield.requiresTwilightForest) {
            BlockHomeShield.register();
        }

//        if (Loader.isModLoaded("ComputerCraft"))
//            UniversalInterfaceRegistry.addInterface(InterfaceTurtle.class, ITurtleTile.class);

        UniversalInterfaceRegistry.addInterface(InterfaceTileEntity.class, TileEntity.class);
        UniversalInterfaceRegistry.addInterface(InterfacePlayer.class, EntityPlayer.class);
        UniversalInterfaceRegistry.addInterface(InterfaceJukebox.class, BlockJukebox.TileEntityJukebox.class);
        UniversalInterfaceRegistry.addInterface(InterfaceMinecart.class, EntityMinecartContainer.class);
        UniversalInterfaceRegistry.addInterface(InterfaceMinecartFurnace.class, EntityMinecartFurnace.class);
        UniversalInterfaceRegistry.addInterface(InterfaceItemFrame.class, EntityItemFrame.class);
    }

    public void registerPacket() {
    }

    private void register() {
        Utilitas.Blocks.hardenedStone = new BlockCompressed(MapColor.blackColor);
        Utilitas.Blocks.hardenedStone.setBlockName("hardened_stone")
                .setHardness(2.5F)
                .setResistance(5000.0F)
                .setCreativeTab(Utilitas.creativeTab)
                .setBlockTextureName("ephys.pp:hardened_stone");

        GameRegistry.registerBlock(Utilitas.Blocks.hardenedStone, Utilitas.Blocks.hardenedStone.getUnlocalizedName());

        BlockFluidDiffuser.register();
        BlockParticleGenerator.register();
        BlockBaseShineyGlass.register();
        BlockProximitySensor.register();
//        BlockItemTicker.register();
        BlockHomeShield.register();
        BlockFluidHopper.register();

        ItemInterfaceUpgrade.register();
        ItemLinker.register();
        ItemPotionDiffuser.register();
        ItemUnemptyingBucket.register();

//        ItemDragonHoe.register();
//        ItemDragonPickaxe.register();

        Utilitas.Items.linkFocus = new Item();
        Utilitas.Items.linkFocus.setUnlocalizedName("link_focus")
                .setMaxStackSize(64)
                .setCreativeTab(Utilitas.creativeTab)
                .setTextureName(":link_focus");

        GameRegistry.registerItem(Utilitas.Items.linkFocus, Utilitas.Items.linkFocus.getUnlocalizedName());
    }

    public void registerCrafts() {
        if (Loader.isModLoaded("IC2")) {
            GameRegistry.addRecipe(new ItemStack(Utilitas.Blocks.hardenedStone, 8),
                    "ioi", "oso", "ioi",
                    'i', ic2.api.item.Items.getItem("advancedAlloy"),
                    's', ic2.api.item.Items.getItem("reinforcedStone"),
                    'o', new ItemStack(Blocks.obsidian));
        } else {
            GameRegistry.addRecipe(new ItemStack(Utilitas.Blocks.hardenedStone, 6),
                    "ioi", "oso", "ioi",
                    'i', Items.iron_ingot,
                    's', Blocks.stone,
                    'o', Blocks.obsidian);
        }

        BlockFluidDiffuser.registerCraft();
        BlockParticleGenerator.registerCraft();
        BlockBaseShineyGlass.registerCraft();
        BlockProximitySensor.registerCraft();
        BlockBiomeScanner.registerCraft();
        BlockToughwoodPlank.registerCraft();
        BlockItemTicker.registerCraft();
        BlockGravitationalField.registerCraft();
        BlockHomeShield.registerCraft();
        BlockBeaconTierII.registerCraft();
        BlockFluidHopper.registerCraft();

        ItemInterfaceUpgrade.registerCraft();
        ItemPotionDiffuser.registerCraft();
        ItemLinker.registerCraft();
        ItemBiomeStorage.registerCraft();

        ItemUnemptyingBucket.registerCraft();

        //BlockDragonscale.registerCraft();
        ItemDragonScale.registerCraft();
        ItemDragonHoe.registerCraft();
        ItemDragonPickaxe.registerCraft();
        //ItemDragonWand.registerCraft();

        GameRegistry.addRecipe(new ItemStack(Utilitas.Items.linkFocus), "ipi", "qeq", "ipi", 'e', new ItemStack(Items.emerald), 'p', new ItemStack(Items.ender_pearl), 'q', new ItemStack(Items.ender_eye), 'i', new ItemStack(Items.blaze_powder));

    }

    private void registerHandlers() {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerInventoryRegistry());
        MinecraftForge.EVENT_BUS.register(new CommandNickname.Events());

        FMLCommonHandler.instance().bus().register(Utilitas.getConfig());

		/* if (Loader.isModLoaded("OpenPeripheralCore")) {
            AdaptorGravitationalField.register();
			AdaptorProximitySensor.register();
			AdaptorBiomeScanner.register();
		} */
    }

    public void onConfigChanges(ConfigHandler configHandler) {
        Blocks.dragon_egg.setCreativeTab(configHandler.addDragonEggTab() ? CreativeTabs.tabDecorations : null);
    }

    public void serverStarting(FMLServerStartingEvent event) {
        if (CommandNickname.enabled) {
            CommandNickname command = new CommandNickname();
            event.registerServerCommand(command);
        }
    }
}

package nf.fr.ephys.playerproxies.common.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import dan200.computercraft.shared.turtle.blocks.ITurtleTile;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.*;
import nf.fr.ephys.playerproxies.common.item.ItemBiomeStorage;
import nf.fr.ephys.playerproxies.common.item.ItemDragonPickaxe;
import nf.fr.ephys.playerproxies.common.item.ItemDragonScale;
import nf.fr.ephys.playerproxies.common.item.ItemLinker;
import nf.fr.ephys.playerproxies.common.network.PacketSetBiomeHandler;
import nf.fr.ephys.playerproxies.common.network.PacketSetNicknameHandler;
import nf.fr.ephys.playerproxies.common.network.PacketSpawnParticleHandler;
import nf.fr.ephys.playerproxies.common.registry.PlayerInventoryRegistry;
import nf.fr.ephys.playerproxies.common.registry.UniversalInterfaceRegistry;
import nf.fr.ephys.playerproxies.common.registry.uniterface.InterfacePlayer;
import nf.fr.ephys.playerproxies.common.registry.uniterface.InterfaceTileEntity;
import nf.fr.ephys.playerproxies.common.registry.uniterface.InterfaceTurtle;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {}

	public void init(FMLInitializationEvent event) {
		registerBlocks();
		registerItems();

		registerPacket();

		NetworkRegistry.INSTANCE.registerGuiHandler(PlayerProxies.instance, new GuiHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {
		registerHandlers();
		registerCrafts();

		if (BlockHomeShield.requiresTwilightForest)
			BlockHomeShield.register();

		if (Loader.isModLoaded("ComputerCraft"))
			UniversalInterfaceRegistry.addInterface(InterfaceTurtle.class, ITurtleTile.class);

		UniversalInterfaceRegistry.addInterface(InterfaceTileEntity.class, TileEntity.class);
		UniversalInterfaceRegistry.addInterface(InterfacePlayer.class, EntityPlayer.class);
	}

	private void registerPacket() {
		PlayerProxies.getNetHandler().registerMessage(PacketSetNicknameHandler.class, PacketSetNicknameHandler.PacketSetNickname.class, 0, Side.SERVER);
		PlayerProxies.getNetHandler().registerMessage(PacketSpawnParticleHandler.class, PacketSpawnParticleHandler.PacketSpawnParticle.class, 1, Side.SERVER);
		PlayerProxies.getNetHandler().registerMessage(PacketSetBiomeHandler.class, PacketSetBiomeHandler.PacketSetBiome.class, 1, Side.SERVER);
	}

	private void registerBlocks() {
		BlockHardenedStone.register();
		BlockParticleGenerator.register();
		BlockBaseShineyGlass.register();
		BlockProximitySensor.register();
		BlockBiomeScanner.register();
		BlockToughwoodPlank.register();
		BlockItemTicker.register();
		BlockGravitationalField.register();
		BlockHomeShield.register();
		BlockBeaconTierII.register();

		BlockEnderDragonSpawner.register();

		if (PlayerProxies.getConfig().addDragonEggTab())
			Blocks.dragon_egg.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public void registerCrafts() {
		BlockHardenedStone.registerCraft();
		BlockParticleGenerator.registerCraft();
		BlockBaseShineyGlass.registerCraft();
		BlockProximitySensor.registerCraft();
		BlockBiomeScanner.registerCraft();
		BlockToughwoodPlank.registerCraft();
		BlockItemTicker.registerCraft();
		BlockGravitationalField.registerCraft();
		BlockHomeShield.registerCraft();
		BlockBeaconTierII.registerCraft();

		ItemLinker.registerCraft();

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Items.linkFocus),
				"ipi", "qeq", "ipi",
				'e', new ItemStack(Items.emerald),
				'p', new ItemStack(Items.ender_pearl),
				'q', new ItemStack(Items.ender_eye),
				'i', new ItemStack(Items.blaze_powder));
	}

	private void registerItems() {
		ItemLinker.register();
		ItemBiomeStorage.register();

		ItemDragonScale.register();
		ItemDragonPickaxe.register();

		PlayerProxies.Items.linkFocus = new Item();
		PlayerProxies.Items.linkFocus.setUnlocalizedName("PP_LinkFocus")
				.setMaxStackSize(64)
				.setCreativeTab(PlayerProxies.creativeTab)
				.setTextureName("ephys.pp:linkFocus");

		GameRegistry.registerItem(PlayerProxies.Items.linkFocus, PlayerProxies.Items.linkFocus.getUnlocalizedName());
	}

	private void registerHandlers() {
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(new PlayerInventoryRegistry());

		FMLCommonHandler.instance().bus().register(PlayerProxies.getConfig());

		/* if (Loader.isModLoaded("OpenPeripheralCore")) {
			AdaptorGravitationalField.register();
			AdaptorProximitySensor.register();
			AdaptorBiomeScanner.register();
		} */
	}
}

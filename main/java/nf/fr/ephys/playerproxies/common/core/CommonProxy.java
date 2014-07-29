package nf.fr.ephys.playerproxies.common.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import dan200.computercraft.shared.turtle.blocks.ITurtleTile;
import net.minecraft.block.BlockCompressed;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.*;
import nf.fr.ephys.playerproxies.common.command.CommandNickname;
import nf.fr.ephys.playerproxies.common.item.*;
import nf.fr.ephys.playerproxies.common.network.PacketSetBiomeHandler;
import nf.fr.ephys.playerproxies.common.network.PacketSetNicknameHandler;
import nf.fr.ephys.playerproxies.common.network.PacketSpawnParticleHandler;
import nf.fr.ephys.playerproxies.common.registry.PlayerInventoryRegistry;
import nf.fr.ephys.playerproxies.common.registry.UniversalInterfaceRegistry;
import nf.fr.ephys.playerproxies.common.registry.uniterface.*;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBeaconTierII;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		BlockBeaconTierII.register();

		Items.potionitem.setContainerItem(Items.glass_bottle);
	}

	public void init(FMLInitializationEvent event) {
		register();

		registerPacket();

		IMCHandler.sendMessages();

		NetworkRegistry.INSTANCE.registerGuiHandler(PlayerProxies.instance, new GuiHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {
		registerHandlers();
		registerCrafts();

		if (BlockHomeShield.requiresTwilightForest)
			BlockHomeShield.register();

		if (Loader.isModLoaded("ComputerCraft"))
			UniversalInterfaceRegistry.addInterface(InterfaceTurtle.class, ITurtleTile.class);

		if (Loader.isModLoaded("Botania")) {
			try {
				TileEntityBeaconTierII.doppleganger = Class.forName("vazkii.botania.common.entity.EntityDoppleganger");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		UniversalInterfaceRegistry.addInterface(InterfaceTileEntity.class, TileEntity.class);
		UniversalInterfaceRegistry.addInterface(InterfacePlayer.class, EntityPlayer.class);
		UniversalInterfaceRegistry.addInterface(InterfaceJukebox.class, BlockJukebox.TileEntityJukebox.class);
		UniversalInterfaceRegistry.addInterface(InterfaceMinecart.class, EntityMinecartContainer.class);
		UniversalInterfaceRegistry.addInterface(InterfaceMinecartFurnace.class, EntityMinecartFurnace.class);
	}

	public void registerPacket() {
		PlayerProxies.getNetHandler().registerMessage(PacketSetNicknameHandler.class, PacketSetNicknameHandler.PacketSetNickname.class, 0, Side.CLIENT);
		PlayerProxies.getNetHandler().registerMessage(PacketSpawnParticleHandler.class, PacketSpawnParticleHandler.PacketSpawnParticle.class, 1, Side.CLIENT);
		PlayerProxies.getNetHandler().registerMessage(PacketSetBiomeHandler.class, PacketSetBiomeHandler.PacketSetBiome.class, 2, Side.CLIENT);
	}

	private void register() {
		PlayerProxies.Blocks.hardenedStone = new BlockCompressed(MapColor.blackColor);
		PlayerProxies.Blocks.hardenedStone.setBlockName("PP_HardenedStone")
				.setHardness(2.5F)
				.setResistance(5000.0F)
				.setCreativeTab(PlayerProxies.creativeTab)
				.setBlockTextureName("ephys.pp:hardenedStone");

		GameRegistry.registerBlock(PlayerProxies.Blocks.hardenedStone, PlayerProxies.Blocks.hardenedStone.getUnlocalizedName());

		BlockFluidDiffuser.register();
		BlockParticleGenerator.register();
		BlockBaseShineyGlass.register();
		BlockProximitySensor.register();
		BlockBiomeScanner.register();
		BlockToughwoodPlank.register();
		BlockItemTicker.register();
		BlockGravitationalField.register();
		BlockHomeShield.register();
		BlockFluidHopper.register();

		//BlockDragonscale.register();
		//BlockEnderDragonSpawner.register();

		ItemLinker.register();
		ItemBiomeStorage.register();
		ItemPotionDiffuser.register();
		ItemUnemptyingBucket.register();

		ItemDragonHoe.register();
		ItemDragonPickaxe.register();

		ItemDragonScale.register();

		PlayerProxies.Items.linkFocus = new Item();
		PlayerProxies.Items.linkFocus.setUnlocalizedName("PP_LinkFocus")
				.setMaxStackSize(64)
				.setCreativeTab(PlayerProxies.creativeTab)
				.setTextureName("ephys.pp:linkFocus");

		GameRegistry.registerItem(PlayerProxies.Items.linkFocus, PlayerProxies.Items.linkFocus.getUnlocalizedName());
	}

	public void registerCrafts() {
		if(Loader.isModLoaded("IC2")) {
			GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.hardenedStone, 8),
					"ioi", "oso", "ioi",
					'i', ic2.api.item.Items.getItem("advancedAlloy"),
					's', ic2.api.item.Items.getItem("reinforcedStone"),
					'o', new ItemStack(Blocks.obsidian));
		} else {
			GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.hardenedStone, 6),
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

		ItemPotionDiffuser.registerCraft();
		ItemLinker.registerCraft();
		ItemBiomeStorage.registerCraft();

		ItemUnemptyingBucket.registerCraft();

		//BlockDragonscale.registerCraft();
		ItemDragonScale.registerCraft();
		ItemDragonHoe.registerCraft();
		ItemDragonPickaxe.registerCraft();

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Items.linkFocus), "ipi", "qeq", "ipi", 'e', new ItemStack(Items.emerald), 'p', new ItemStack(Items.ender_pearl), 'q', new ItemStack(Items.ender_eye), 'i', new ItemStack(Items.blaze_powder));

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

	public void onConfigChanges(ConfigHandler configHandler) {
		Blocks.dragon_egg.setCreativeTab(configHandler.addDragonEggTab() ? CreativeTabs.tabDecorations : null);
	}

	public void serverStarting(FMLServerStartingEvent event) {
		if (CommandNickname.enabled) {
			CommandNickname command = new CommandNickname();
			MinecraftForge.EVENT_BUS.register(command);
			event.registerServerCommand(command);
		}
	}
}

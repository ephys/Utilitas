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
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.block.*;
import nf.fr.ephys.playerproxies.common.item.*;
import nf.fr.ephys.playerproxies.common.network.PacketSetBiomeHandler;
import nf.fr.ephys.playerproxies.common.network.PacketSetNicknameHandler;
import nf.fr.ephys.playerproxies.common.network.PacketSpawnParticleHandler;
import nf.fr.ephys.playerproxies.common.registry.PlayerInventoryRegistry;
import nf.fr.ephys.playerproxies.common.registry.UniversalInterfaceRegistry;
import nf.fr.ephys.playerproxies.common.registry.uniterface.InterfacePlayer;
import nf.fr.ephys.playerproxies.common.registry.uniterface.InterfaceTileEntity;
import nf.fr.ephys.playerproxies.common.registry.uniterface.InterfaceTurtle;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBeaconTierII;

import java.lang.reflect.Field;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		BlockBeaconTierII.register();
	}

	public void init(FMLInitializationEvent event) {
		registerBlocks();
		registerItems();

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

		listPotionEffect();
	}

	private void listPotionEffect() {
		Class<Potion> clazz = Potion.class;

		Field isBadEffectField = null;

		// and this is because I can't get "isBadEffect" as I don't know which name the obfuscated field name will be
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getType().equals(boolean.class)) {
				isBadEffectField = field;
				break;
			}
		}

		if (isBadEffectField == null) {
			PlayerProxies.getLogger().warn("Could not find field isBadEffect in class Potion, the new beacon will not work well ! (report this thx)");
		} else {
			isBadEffectField.setAccessible(true);

			for (int i = 0; i < Potion.potionTypes.length; i++) {
				if (Potion.potionTypes[i] == null) {
					continue;
				}

				try {
					TileEntityBeaconTierII.badPotionEffects[i] = isBadEffectField.getBoolean(Potion.potionTypes[i]);
				} catch (IllegalAccessException e) {
					e.printStackTrace();

					PlayerProxies.getLogger().warn("Failed to retrieve isBadEffect, the new beacon will not work well ! (report this thx)");
					break;
				}

				//if (PlayerProxies.DEV_MODE)
				PlayerProxies.getLogger().info("Potion "+Potion.potionTypes[i].getName()+" is " + (TileEntityBeaconTierII.badPotionEffects[i] ? "bad" : "good"));
			}
		}
	}

	private void registerPacket() {
		PlayerProxies.getNetHandler().registerMessage(PacketSetNicknameHandler.class, PacketSetNicknameHandler.PacketSetNickname.class, 0, Side.CLIENT);
		PlayerProxies.getNetHandler().registerMessage(PacketSpawnParticleHandler.class, PacketSpawnParticleHandler.PacketSpawnParticle.class, 1, Side.CLIENT);
		PlayerProxies.getNetHandler().registerMessage(PacketSetBiomeHandler.class, PacketSetBiomeHandler.PacketSetBiome.class, 2, Side.CLIENT);
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

		BlockDragonscale.register();
		BlockEnderDragonSpawner.register();
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
		ItemPotionDiffuser.registerCraft();
		ItemLinker.registerCraft();

		ItemUnemptyingBucket.registerCraft();

		BlockDragonscale.registerCraft();
		ItemDragonScale.registerCraft();
		ItemDragonHoe.registerCraft();
		ItemDragonPickaxe.registerCraft();

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Items.linkFocus), "ipi", "qeq", "ipi", 'e', new ItemStack(Items.emerald), 'p', new ItemStack(Items.ender_pearl), 'q', new ItemStack(Items.ender_eye), 'i', new ItemStack(Items.blaze_powder));
	}

	private void registerItems() {
		ItemLinker.register();
		ItemBiomeStorage.register();
		ItemPotionDiffuser.register();
		ItemUnemptyingBucket.register();

		ItemDragonScale.register();
		ItemDragonHoe.register();
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

	public void onConfigChanges(ConfigHandler configHandler) {
		Blocks.dragon_egg.setCreativeTab(configHandler.addDragonEggTab() ? CreativeTabs.tabDecorations : null);
	}
}

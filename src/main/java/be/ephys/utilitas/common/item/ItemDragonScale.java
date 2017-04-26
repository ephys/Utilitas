package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import nf.fr.ephys.cookiecore.helpers.ChatHelper;
import nf.fr.ephys.playerproxies.client.registry.DragonColorRegistry;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.core.EventHandler;

public class ItemDragonScale extends Item {
	public final boolean isIngot;

	public ItemDragonScale(boolean isIngot) {
		this.isIngot = isIngot;
	}

	public static void register() {
		if (!enabled()) return;

		PlayerProxies.Items.dragonScale = new ItemDragonScale(false);
		PlayerProxies.Items.dragonScale
				.setTextureName("ephys.pp:dragonScale")
				.setUnlocalizedName("PP_DragonScale")
				.setCreativeTab(PlayerProxies.creativeTab);

		GameRegistry.registerItem(PlayerProxies.Items.dragonScale, PlayerProxies.Items.dragonScale.getUnlocalizedName());

		PlayerProxies.Items.dragonScaleIngot = new ItemDragonScale(true);
		PlayerProxies.Items.dragonScaleIngot
				.setTextureName("iron_ingot")
				.setUnlocalizedName("PP_DragonScaleIngot")
				.setCreativeTab(PlayerProxies.creativeTab);

		GameRegistry.registerItem(PlayerProxies.Items.dragonScaleIngot, PlayerProxies.Items.dragonScaleIngot.getUnlocalizedName());
	}

	public static void registerCraft() {
		if (!enabled()) return;

		GameRegistry.addShapelessRecipe(new ItemStack(PlayerProxies.Items.dragonScaleIngot), PlayerProxies.Items.dragonScale, Items.iron_ingot, Items.ender_pearl);

		MinecraftForge.EVENT_BUS.register(PlayerProxies.Items.dragonScale);
	}

	@SubscribeEvent
	public void onEntityDrop(LivingDropsEvent event) {
		if (event.entity.worldObj.isRemote)
			return;

		if (event.entity instanceof EntityDragon) {
			int nbStacks = MathHelper.getRandomIntegerInRange(EventHandler.random, 1, 8);

			for (int i = 0; i < nbStacks; i++) {
				event.drops.add(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(PlayerProxies.Items.dragonScale, 16)));
			}
		} else if (event.entity.worldObj.provider.dimensionId == 1) {
			if (event.entity instanceof EntityEnderman) {
				if (event.source.getEntity() instanceof EntityCreeper)
					event.drops.add(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(PlayerProxies.Items.dragonScale, MathHelper.getRandomIntegerInRange(EventHandler.random, 5, 19))));
				else if (!(event.source.getEntity() instanceof EntityPlayer) && EventHandler.random.nextFloat() < 0.125F * event.lootingLevel) {
					event.drops.add(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(PlayerProxies.Items.dragonScale, MathHelper.getRandomIntegerInRange(EventHandler.random, 0, 1 + event.lootingLevel))));
				}
			}
		}
	}

	public static boolean enabled() {
		return true;
		// return ItemDragonHoe.enabled || ItemDragonPickaxe.enabled;
	}

	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return EnumRarity.rare;
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int par2) {
		return DragonColorRegistry.getColor();
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
		// the dragonSpawner block is bugued, for now. Don't use it
		if (PlayerProxies.Blocks.dragonSpawner == null) return false;

		if (isIngot) return false;

		Block block = world.getBlock(x, y, z);

		if (!block.equals(Blocks.mob_spawner)) return false;

		if (world.isRemote) return true;

		World worldEnd = MinecraftServer.getServer().worldServerForDimension(1);
		Block endBlock = worldEnd.getBlock(0, 2, 0);

		if (endBlock.equals(PlayerProxies.Blocks.dragonSpawner)) {
			ChatHelper.sendChatMessage(player, "There is already a spawn in progress");
		} else {
			stack.stackSize--;

			world.setBlockToAir(x, y, z);

			if (player.worldObj.provider.dimensionId != 1)
				player.travelToDimension(1);

			worldEnd.setBlock(0, 2, 0, PlayerProxies.Blocks.dragonSpawner);

			ChatHelper.sendChatMessage(player, "An enderdragon is approaching");
		}

		return true;
	}
}
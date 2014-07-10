package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.registry.BeaconEffectsRegistry;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBeaconTierII;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;
import nf.fr.ephys.playerproxies.helpers.DebugHelper;

public class BlockBeaconTierII extends BlockBeacon {
	public static void register() {
		PlayerProxies.Blocks.betterBeacon = new BlockBeaconTierII();
		PlayerProxies.Blocks.betterBeacon.setBlockName("PP_BetterBeacon").setCreativeTab(PlayerProxies.creativeTab).setBlockTextureName("beacon").setLightLevel(1F);

		GameRegistry.registerBlock(PlayerProxies.Blocks.betterBeacon, PlayerProxies.Blocks.betterBeacon.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityBeaconTierII.class, PlayerProxies.Blocks.betterBeacon.getUnlocalizedName());

		// level 1 beacon
		BeaconEffectsRegistry.addEffect(Items.sugar, Potion.moveSpeed.getId(), 1, TileEntityBeaconTierII.MAX_LEVELS);
		BeaconEffectsRegistry.addEffect(Items.sugar, Potion.confusion.getId(), 1, TileEntityBeaconTierII.MAX_LEVELS);

		BeaconEffectsRegistry.addEffect(new ItemStack[]{ new ItemStack(Items.sugar), new ItemStack(Items.diamond_pickaxe), new ItemStack(Items.golden_apple) }, Potion.digSpeed.getId(), 1, TileEntityBeaconTierII.MAX_LEVELS);

		BeaconEffectsRegistry.addEffect(new ItemStack(Items.fish, 1, 3), Potion.moveSlowdown.getId(), 1, TileEntityBeaconTierII.MAX_LEVELS);
		BeaconEffectsRegistry.addEffect(new ItemStack(Items.fish, 1, 3), Potion.waterBreathing.getId(), 2, TileEntityBeaconTierII.MAX_LEVELS);

		BeaconEffectsRegistry.addEffect(Items.spider_eye, Potion.poison.getId(), 0, TileEntityBeaconTierII.MAX_LEVELS);
		BeaconEffectsRegistry.addEffect(Items.rotten_flesh, Potion.hunger.getId(), 3, TileEntityBeaconTierII.MAX_LEVELS);

		// level 2
		BeaconEffectsRegistry.addEffect(Items.coal, Potion.blindness.getId(), 2, TileEntityBeaconTierII.MAX_LEVELS);

		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.carrot), new ItemStack(Items.sugar) }, Potion.jump.getId(), 3, TileEntityBeaconTierII.MAX_LEVELS);
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.carrot), new ItemStack(Items.sugar) }, Potion.hunger.getId(), 3, TileEntityBeaconTierII.MAX_LEVELS);

		// level 3
		BeaconEffectsRegistry.addEffect(new ItemStack[]{ new ItemStack(Items.spider_eye), new ItemStack(Items.ender_eye) }, Potion.invisibility.getId(), 3, TileEntityBeaconTierII.MAX_LEVELS);
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.diamond_chestplate), new ItemStack(Items.experience_bottle) }, Potion.resistance.getId(), 6, 6);

		// level 4
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.nether_star), new ItemStack(Items.magma_cream) }, Potion.damageBoost.getId(), 2, TileEntityBeaconTierII.MAX_LEVELS);

		// level 5
		BeaconEffectsRegistry.addEffect(new ItemStack(Items.golden_apple, 1, 1), Potion.regeneration.getId(), 5, TileEntityBeaconTierII.MAX_LEVELS);
		BeaconEffectsRegistry.addEffect(new ItemStack(Items.golden_apple, 1, 1), Potion.weakness.getId(), 5, TileEntityBeaconTierII.MAX_LEVELS);

		// level 6
		BeaconEffectsRegistry.addEffect(Items.blaze_rod, Potion.fireResistance.getId(), 6, TileEntityBeaconTierII.MAX_LEVELS);
		BeaconEffectsRegistry.addEffect(Items.golden_carrot, Potion.nightVision.getId(), 6, TileEntityBeaconTierII.MAX_LEVELS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityBeaconTierII();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i4, float v, float v2, float v3) {
		if (world.isRemote)
			return true;

		TileEntityBeaconTierII te = (TileEntityBeaconTierII) world.getTileEntity(x, y, z);

		if (te != null) {
			ItemStack item = player.getHeldItem();

			int itemCount = te.getItemCount();

			if (item == null) {
				if (itemCount > 0) {
					ItemStack stack = te.getStackInSlotOnClosing(itemCount - 1);
					BlockHelper.dropItem(stack, player);
				}
			} else {
				int pos = te.getItemSlot(item);

				if (pos != -1) {
					ItemStack stack = te.getStackInSlotOnClosing(pos);
					BlockHelper.dropItem(stack, player);
				} else {
					ItemStack newStack = item.copy();
					newStack.stackSize = 1;

					if (BlockHelper.insert(te, newStack))
						item.stackSize--;
				}
			}
		}

		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int metadata) {
		TileEntityBeaconTierII te = (TileEntityBeaconTierII) world.getTileEntity(x, y, z);

		if (te != null)
			BlockHelper.dropContents(te, world, x, y, z);

		super.onBlockPreDestroy(world, x, y, z, metadata);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
		((TileEntityBeaconTierII) world.getTileEntity(x, y, z)).onBlockUpdate();
	}
}

package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockBeacon;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.registry.BeaconEffectsRegistry;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBeaconTierII;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityItemTicker;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;

public class BlockBeaconTierII extends BlockBeacon {
	public static void register() {
		PlayerProxies.Blocks.betterBeacon = new BlockBeaconTierII();
		PlayerProxies.Blocks.betterBeacon.setBlockName("PP_BetterBeacon").setCreativeTab(PlayerProxies.creativeTab);

		GameRegistry.registerBlock(PlayerProxies.Blocks.betterBeacon, PlayerProxies.Blocks.betterBeacon.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityBeaconTierII.class, PlayerProxies.Blocks.betterBeacon.getUnlocalizedName());

		// level 0 beacon
		BeaconEffectsRegistry.addEffect(Items.sugar, Potion.moveSpeed.getId(), 0, TileEntityBeaconTierII.MAX_LEVELS);

		// level 2
		BeaconEffectsRegistry.addEffect(Items.coal, Potion.blindness.getId(), 2, TileEntityBeaconTierII.MAX_LEVELS);

		BeaconEffectsRegistry.addEffect(Items.bone, Potion.blindness.getId(), 2, TileEntityBeaconTierII.MAX_LEVELS);
		BeaconEffectsRegistry.addEffect(Items.bone, Potion.nightVision.getId(), 2, TileEntityBeaconTierII.MAX_LEVELS);

		// level 3
		BeaconEffectsRegistry.addEffect(Items.spider_eye, Potion.poison.getId(), 3, TileEntityBeaconTierII.MAX_LEVELS);
		BeaconEffectsRegistry.addEffect(Items.spider_eye, Potion.weakness.getId(), 3, TileEntityBeaconTierII.MAX_LEVELS);

		BeaconEffectsRegistry.addEffect(Items.blaze_rod, Potion.fireResistance.getId(), 6, 6);

		// level 4
		BeaconEffectsRegistry.addEffect(new Item[]{ Items.sugar, Items.golden_apple }, Potion.digSpeed.getId(), 4, TileEntityBeaconTierII.MAX_LEVELS);

		/*
		 * slowness, mining fatigue, strenght, heal, instant damage, jump boost, nausea, regeneration, resistance,
		 * water breathing, invisibility, blindness, night vision, hunger, withering, health boost,
		 * absorbtion, saturation
		 */
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
				ItemStack stack = te.getStackInSlotOnClosing(itemCount - 1);
				BlockHelper.dropItem(stack, world, x, y, z);
			} else {
				int pos = te.getItemSlot(item);

				if (pos != -1) {
					ItemStack stack = te.getStackInSlotOnClosing(pos);
					BlockHelper.dropItem(stack, world, x, y, z);
				} else {
					if (te.getSizeInventory() != te.getItemCount()) {
						ItemStack newStack = item.copy();
						newStack.stackSize = 1;

						item.stackSize--;
						te.setInventorySlotContents(te.getItemCount(), newStack);
					}
				}
			}

			return true;
		}

		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int metadata) {
		TileEntityItemTicker te = (TileEntityItemTicker) world.getTileEntity(x, y, z);

		if (te != null)
			BlockHelper.dropContents(te, world, x, y, z);

		super.onBlockPreDestroy(world, x, y, z, metadata);
	}

	@Override
	public void onBlockPlacedBy(World paramWorld, int paramInt1, int paramInt2, int paramInt3, EntityLivingBase paramEntityLivingBase, ItemStack paramItemStack) {}
}

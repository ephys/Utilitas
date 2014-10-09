package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockBeacon;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;
import nf.fr.ephys.cookiecore.helpers.RegistryHelper;
import nf.fr.ephys.playerproxies.client.renderer.BlockBeaconTierIIRenderer;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.registry.BeaconEffectsRegistry;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBeaconTierII;

public class BlockBeaconTierII extends BlockBeacon {
	public static boolean overwrite = true;

	public static void register() {
		if (!overwrite) return;

		PlayerProxies.Blocks.betterBeacon = new BlockBeaconTierII();
		PlayerProxies.Blocks.betterBeacon.setBlockName("beacon").setCreativeTab(PlayerProxies.creativeTab).setBlockTextureName("beacon").setLightLevel(1F);

		RegistryHelper.overwriteBlock("minecraft:beacon", PlayerProxies.Blocks.betterBeacon);

		GameRegistry.registerTileEntity(TileEntityBeaconTierII.class, PlayerProxies.Blocks.betterBeacon.getUnlocalizedName());
	}

	public static void registerCraft() {
		if (!overwrite) return;

		RegistryHelper.removeItemRecipe(new ItemStack(Blocks.beacon));

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.betterBeacon),
				"ggg",
				"gsg",
				"ooo",

				'g', new ItemStack(PlayerProxies.Blocks.baseShineyGlass, 1, 0),
				'o', new ItemStack(Blocks.obsidian),
				's', new ItemStack(Items.nether_star)
		);

		// level 1 beacon
		BeaconEffectsRegistry.addEffect(new ItemStack[]{ new ItemStack(Items.sugar), new ItemStack(Items.redstone) }, Potion.moveSpeed.getId(), 1, -1);
		BeaconEffectsRegistry.addEffect(new ItemStack[]{ new ItemStack(Items.sugar), new ItemStack(Items.diamond_pickaxe), new ItemStack(Items.golden_apple) }, Potion.digSpeed.getId(), 1, -1);

		BeaconEffectsRegistry.addEffect(new ItemStack[]{ new ItemStack(Items.fish, 1, 0), new ItemStack(Items.fish, 1, 1), new ItemStack(Items.fish, 1, 2), new ItemStack(Items.fish, 1, 3) }, Potion.moveSlowdown.getId(), 1, -1);

		// level 2
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.carrot), new ItemStack(Items.sugar) }, Potion.jump.getId(), 2, -1);
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.fish, 1, 0), new ItemStack(Items.fish, 1, 1), new ItemStack(Items.fish, 1, 2), new ItemStack(Items.fish, 1, 3) }, Potion.waterBreathing.getId(), 2, 0);

		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.coal), new ItemStack(Items.fermented_spider_eye) }, Potion.blindness.getId(), 2, 0);
		BeaconEffectsRegistry.addEffect(Items.rotten_flesh, Potion.hunger.getId(), 2, 1);

		// level 3
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.spider_eye), new ItemStack(Items.ender_eye) }, Potion.invisibility.getId(), 3, 0);
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.diamond_chestplate), new ItemStack(Items.experience_bottle) }, Potion.resistance.getId(), 3, -1);

		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.spider_eye), new ItemStack(Items.poisonous_potato) }, Potion.poison.getId(), 3, 1);
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.sugar), new ItemStack(Items.cake) }, Potion.confusion.getId(), 3, 0);

		// level 4
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.nether_star), new ItemStack(Items.magma_cream) }, Potion.damageBoost.getId(), 4, -1);

		// level 5
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.golden_apple, 1, 1), new ItemStack(Items.ghast_tear) }, Potion.regeneration.getId(), 5, 0);
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.golden_apple, 1, 1), new ItemStack(Items.ghast_tear), new ItemStack(PlayerProxies.Items.dragonScale) }, Potion.regeneration.getId(), 5, -1);

		BeaconEffectsRegistry.addEffect(new ItemStack(Items.golden_apple, 1, 1), Potion.weakness.getId(), 5, -1);

		// level 6
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.diamond_chestplate), new ItemStack(Items.experience_bottle), new ItemStack(Items.blaze_rod) }, Potion.fireResistance.getId(), 6, 0);
		BeaconEffectsRegistry.addEffect(new ItemStack[] { new ItemStack(Items.golden_carrot), new ItemStack(Items.ender_eye) }, Potion.nightVision.getId(), 6, 0);
	}

	@Override
	public boolean equals(Object other) {
		return Blocks.beacon == other || this == other;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityBeaconTierII();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i4, float v, float v2, float v3) {
		if (world.isRemote)
			return true;

	/*	if (ExUHandler.instance.isLoaded() && player.getHeldItem().getItem() != null && player.getHeldItem().getItem().equals(ExUHandler.instance.xuDivisionSigil)) {
			if (ExUHandler.instance.startSiege(player))
				return true;
		}*/

		TileEntityBeaconTierII te = (TileEntityBeaconTierII) world.getTileEntity(x, y, z);

		if (te != null) {
			ItemStack item = player.getHeldItem();

			int itemCount = te.getItemCount();

			if (item == null) {
				if (itemCount > 0) {
					ItemStack stack = te.getStackInSlotOnClosing(itemCount - 1);
					InventoryHelper.dropItem(stack, player);
				}
			} else {
				int pos = te.getItemSlot(item);

				if (pos != -1) {
					ItemStack stack = te.getStackInSlotOnClosing(pos);
					InventoryHelper.dropItem(stack, player);
				} else {
					ItemStack newStack = item.copy();
					newStack.stackSize = 1;

					if (InventoryHelper.insertItem(te, newStack)) {
						item.stackSize--;

						return true;
					} else {
						System.out.println("failled to insert item");
					}

					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int metadata) {
		TileEntityBeaconTierII te = (TileEntityBeaconTierII) world.getTileEntity(x, y, z);

		if (te != null)
			InventoryHelper.dropContents(te, world, x, y, z);

		super.onBlockPreDestroy(world, x, y, z, metadata);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
		((TileEntityBeaconTierII) world.getTileEntity(x, y, z)).onBlockUpdate();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return BlockBeaconTierIIRenderer.RENDER_ID;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}
}

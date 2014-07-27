package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.playerproxies.client.registry.DragonColorRegistry;
import nf.fr.ephys.playerproxies.client.registry.FluidColorRegistry;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;
import nf.fr.ephys.playerproxies.helpers.ChatHelper;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

import java.util.List;

public class ItemUnemptyingBucket extends Item {
	public static final int METADATA_FILL = 0; // fill THE BUCKET
	public static final int METADATA_EMPTY = 1; // not the drum

	public static int range = 16;
	public static boolean crossDim = false;
	public static boolean enabled = true;

	public static void register() {
		if (!enabled) return;

		PlayerProxies.Items.unemptyingBucket = new ItemUnemptyingBucket();
		PlayerProxies.Items.unemptyingBucket.setUnlocalizedName("PP_UnemptyingBucket")
				.setMaxStackSize(1)
				.setCreativeTab(PlayerProxies.creativeTab)
				.setTextureName("bucket_empty");

		GameRegistry.registerItem(PlayerProxies.Items.unemptyingBucket, PlayerProxies.Items.unemptyingBucket.getUnlocalizedName());
	}

	public static void registerCraft() {
		if (!enabled) return;

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Items.unemptyingBucket),
				"i i", " i ", " l ",
				'i', PlayerProxies.Items.dragonScaleIngot,
				'l', PlayerProxies.Items.linkFocus);
	}

	private IIcon[] textures;

	@Override
	public void registerIcons(IIconRegister register) {
		textures = new IIcon[2];
		textures[0] = register.registerIcon("bucket_empty");
		textures[1] = register.registerIcon("ephys.pp:bucket_fluid");
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		if (pass == 1 && !NBTHelper.getNBT(stack).hasKey("fluid"))
			pass = 0;

		return textures[pass];
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		switch (renderPass) {
			case 0:
				return DragonColorRegistry.getColor();
			case 1:
				Fluid fluid = getLiquid(stack);
				if (fluid == null)
					return DragonColorRegistry.getColor();

				return FluidColorRegistry.getColorFromFluid(fluid);
		}

		return super.getColorFromItemStack(stack, renderPass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer player, List data, boolean unknown) {
		data.add("§5" + (stack.getItemDamage() == METADATA_EMPTY ? StatCollector.translateToLocal("pp_tooltip.bucket_mode_empty") : StatCollector.translateToLocal("pp_tooltip.bucket_mode_fill")));

		if (NBTHelper.getNBT(stack).hasKey("fluidHandler"))
			data.add("§5" + StatCollector.translateToLocal("pp_tooltip.bucket_bound"));
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		Fluid fluid = getLiquid(stack);

		if (fluid == null)
			return super.getItemStackDisplayName(stack);

		return String.format(
				StatCollector.translateToLocal("item.PP_UnemptyingBucket.filled.name"),
				ChatHelper.getDisplayName(fluid)
		);
	}

	public static void setLiquid(ItemStack stack, Fluid liquid) {
		NBTTagCompound nbt = NBTHelper.getNBT(stack);

		if (liquid == null)
			nbt.removeTag("fluid");
		else
			nbt.setInteger("fluid", FluidRegistry.getFluidID(liquid.getName()));
	}

	public static Fluid getLiquid(ItemStack stack) {
		NBTTagCompound nbt = NBTHelper.getNBT(stack);

		if (!nbt.hasKey("fluid")) return null;

		return FluidRegistry.getFluid(nbt.getInteger("fluid"));
	}

	public static boolean setFluidHandler(ItemStack stack, TileEntity te, int side) {
		if (te == null) {
			NBTHelper.getNBT(stack).removeTag("fluidHandler");

			return false;
		} else {
			NBTTagCompound nbt = NBTHelper.getNBT(stack);

			int[] newCoords = BlockHelper.getCoords(te);

			NBTTagCompound tileNBT;
			if (nbt.hasKey("fluidHandler")) {
				tileNBT = nbt.getCompoundTag("fluidHandler");

				if (tileNBT.getInteger("worldID") == te.getWorldObj().provider.dimensionId && tileNBT.getInteger("side") == side) {
					int[] oldCoords = tileNBT.getIntArray("coords");

					if (oldCoords[0] == newCoords[0] && oldCoords[1] == newCoords[1] && oldCoords[2] == newCoords[2]) {
						NBTHelper.getNBT(stack).removeTag("fluidHandler");

						return false;
					}
				}
			}

			tileNBT = new NBTTagCompound();
			tileNBT.setIntArray("coords", newCoords);
			tileNBT.setInteger("worldID", te.getWorldObj().provider.dimensionId);
			tileNBT.setInteger("side", side);

			NBTHelper.getNBT(stack).setTag("fluidHandler", tileNBT);

			return true;
		}
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) return false;

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IFluidHandler) {
			IFluidHandler fluidHandler = (IFluidHandler) te;

			Fluid fluid = getLiquid(stack);

			if (fluid == null) {
				attemptDrain(stack, fluidHandler, ForgeDirection.getOrientation(side), world);
			} else {
				attemptFill(stack, fluidHandler, ForgeDirection.getOrientation(side), fluid, world);
			}

			refill(stack, world, player);

			return !world.isRemote;
		}

		return false;
	}

	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return EnumRarity.rare;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		Fluid fluid = getLiquid(stack);
		boolean empty = fluid == null;

		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, empty);
		TileEntity te = mop == null ? null : world.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);

		if (te instanceof IFluidHandler) {
			if (!player.isSneaking()) return stack;

			if (setFluidHandler(stack, te, mop.sideHit)) {
				ChatHelper.sendChatMessage(player, String.format(StatCollector.translateToLocal("pp_messages.bucket_bound"), this.getItemStackDisplayName(stack), (new ItemStack(world.getBlock(mop.blockX, mop.blockY, mop.blockZ), 1, world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ))).getDisplayName(), ChatHelper.blockSideName(mop.sideHit)));
			} else {
				ChatHelper.sendChatMessage(player, String.format(StatCollector.translateToLocal("pp_messages.bucket_unbound"), this.getItemStackDisplayName(stack)));
			}

			refill(stack, world, player);

			return stack;
		}

		if (player.isSneaking()) {
			switchMode(stack, player);

			refill(stack, world, player);

			return stack;
		}

		if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return stack;

		if (world.canMineBlock(player, mop.blockX, mop.blockY, mop.blockZ)) {
			if (empty) {
				if (player.canPlayerEdit(mop.blockX, mop.blockY, mop.blockZ, mop.sideHit, stack)) {
					Block block = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
					int l = world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);

					Fluid targetFluid;
					// more hotfixing the broken vanilla fluid handling (fluid registry only has blocks for still water & still lava)
					if (block == Blocks.flowing_water)
						targetFluid = FluidRegistry.WATER;
					else if (block == Blocks.flowing_lava)
						targetFluid = FluidRegistry.LAVA;
					else
						targetFluid = FluidRegistry.lookupFluidForBlock(block);

					if (l == 0 && targetFluid != null) {
						setLiquid(stack, targetFluid);
						world.setBlockToAir(mop.blockX, mop.blockY, mop.blockZ);
					}
				}
			} else {
				int[] coords = BlockHelper.getAdjacentBlock(mop.blockX, mop.blockY, mop.blockZ, mop.sideHit);

				if (placeFluidInWorld(player, coords, mop.sideHit, stack, world, fluid))
					setLiquid(stack, null);
			}
		}

		refill(stack, world, player);

		return stack;
	}

	private void switchMode(ItemStack stack, EntityPlayer player) {
		if (stack.getItemDamage() == METADATA_FILL) {
			ChatHelper.sendChatMessage(player, "Switching to empty mode");
			stack.setItemDamage(METADATA_EMPTY);
		} else {
			ChatHelper.sendChatMessage(player, "Switching to fill mode");
			stack.setItemDamage(METADATA_FILL);
		}
	}

	private boolean placeFluidInWorld(EntityPlayer player, int[] coords, int side, ItemStack stack, World world, Fluid fluid) {
		if (!fluid.canBePlacedInWorld()) {
			ChatHelper.sendChatMessage(player, "Can't place this fluid in world. :(");

			return false;
		}

		if (player.canPlayerEdit(coords[0], coords[1], coords[2], side, stack)) {
			Block block = world.getBlock(coords[0], coords[1], coords[2]);
			Material material = block.getMaterial();

			if (material.isSolid()) return false;

			if (world.provider.isHellWorld && fluid == FluidRegistry.WATER) {
				world.playSoundEffect(coords[0] + 0.5D, coords[1] + 0.5D, coords[2] + 0.5D, "random.fizz", 0.5F, 2.6F + world.rand.nextFloat() - world.rand.nextFloat() * 0.8F);

				for (int l = 0; l < 8; ++l) {
					world.spawnParticle("largesmoke", coords[0] + Math.random(), coords[1] + Math.random(), coords[2] + Math.random(), 0.0D, 0.0D, 0.0D);
				}
			} else {
				if (!world.isRemote && !material.isLiquid()) {
					world.func_147480_a(coords[0], coords[1], coords[2], true);
				}

				Block fluidblock = fluid.getBlock();

				if (fluidblock == Blocks.water)
					fluidblock = Blocks.flowing_water;
				else if (fluidblock == Blocks.lava)
					fluidblock = Blocks.flowing_lava;

				world.setBlock(coords[0], coords[1], coords[2], fluidblock, 0, 3);
			}

			return true;
		}

		return false;
	}

	private void refill(ItemStack stack, World world, EntityPlayer player) {
		Fluid fluid = getLiquid(stack);

		int metadata = stack.getItemDamage();
		if (metadata == METADATA_EMPTY && fluid == null) return;
		if (metadata == METADATA_FILL && fluid != null) return;

		NBTTagCompound nbt = NBTHelper.getNBT(stack);

		if (!nbt.hasKey("fluidHandler"))  return;
		NBTTagCompound tileNBT = nbt.getCompoundTag("fluidHandler");

		int tileWorldID = tileNBT.getInteger("worldID");
		if (!crossDim && world.provider.dimensionId != tileWorldID) return;

		int[] tileCoords = tileNBT.getIntArray("coords");

		if (range != -1 && (Math.abs(tileCoords[0] - player.posX) > range
				|| Math.abs(tileCoords[1] - player.posY) > range
				|| Math.abs(tileCoords[2] - player.posZ) > range)) return;

		World tileWorld = MinecraftServer.getServer().worldServerForDimension(tileWorldID);
		TileEntity te = tileWorld.getTileEntity(tileCoords[0], tileCoords[1], tileCoords[2]);

		if (!(te instanceof IFluidHandler)) {
			setFluidHandler(stack, null, 0);

			return;
		}

		IFluidHandler fluidHandler = (IFluidHandler) te;

		int side = tileNBT.getInteger("side");

		ForgeDirection direction = ForgeDirection.getOrientation(side);
		switch (metadata) {
			case METADATA_EMPTY:
				attemptFill(stack, fluidHandler, direction, fluid, world);
				break;

			case METADATA_FILL:
				attemptDrain(stack, fluidHandler, direction, world);
		}
	}

	/**
	 * Attempt to fill a FluidHandler
	 */
	private void attemptFill(ItemStack stack, IFluidHandler fluidHandler, ForgeDirection direction, Fluid fluid, World world) {
		if (fluidHandler.canFill(direction, fluid)) {
			FluidStack fstack = new FluidStack(fluid, 1000);
			int filled = fluidHandler.fill(direction, fstack, false);

			if (filled != 1000) return;

			if (!world.isRemote)
				fluidHandler.fill(direction, fstack, true);
			setLiquid(stack, null);
		}
	}

	/**
	 * Attempt to drain a FluidHandler
	 */
	private void attemptDrain(ItemStack stack, IFluidHandler fluidHandler, ForgeDirection direction, World world) {
		FluidStack fstack = fluidHandler.drain(direction, 1000, false);
		if (fstack == null || fstack.amount != 1000) return;

		setLiquid(stack, fstack.getFluid());

		if (!world.isRemote)
			fluidHandler.drain(direction, 1000, true);
	}
}
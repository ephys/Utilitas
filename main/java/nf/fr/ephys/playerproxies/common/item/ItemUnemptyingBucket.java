package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.cookiecore.helpers.BlockHelper;
import nf.fr.ephys.cookiecore.helpers.ChatHelper;
import nf.fr.ephys.cookiecore.helpers.FluidHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;
import nf.fr.ephys.playerproxies.client.registry.DragonColorRegistry;
import nf.fr.ephys.playerproxies.client.registry.FluidColorRegistry;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

import java.util.List;

public class ItemUnemptyingBucket extends Item implements IFluidContainerItem {
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
		if (pass == 1 && !hasFluid(stack))
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
				FluidStack fluid = getFluid(stack);
				if (fluid == null)
					return DragonColorRegistry.getColor();

				return FluidColorRegistry.getColorFromFluid(fluid);
		}

		return super.getColorFromItemStack(stack, renderPass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer player, List data, boolean debug) {
		data.add(EnumChatFormatting.DARK_PURPLE + (stack.getItemDamage() == METADATA_EMPTY ? StatCollector.translateToLocal("pp_tooltip.bucket_mode_empty") : StatCollector.translateToLocal("pp_tooltip.bucket_mode_fill")));

		NBTTagCompound nbt = NBTHelper.getNBT(stack);
		if (nbt.hasKey("fluidStack")) {
			data.add(nbt.getCompoundTag("fluidStack").getInteger("Amount") + "mB");
		}

		if (nbt.hasKey("fluidHandler"))
			data.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("pp_tooltip.bucket_bound"));
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		FluidStack fluid = getFluid(stack);

		if (fluid == null)
			return super.getItemStackDisplayName(stack);

		return String.format(
				StatCollector.translateToLocal("item.PP_UnemptyingBucket.filled.name"),
				ChatHelper.getDisplayName(fluid)
		);
	}

	public static void setFluid(ItemStack stack, FluidStack liquid) {
		NBTTagCompound nbt = NBTHelper.getNBT(stack);

		if (liquid == null || liquid.amount == 0)
			nbt.removeTag("fluidStack");
		else {
			NBTTagCompound fluidNBT = new NBTTagCompound();
			liquid.writeToNBT(fluidNBT);

			nbt.setTag("fluidStack", fluidNBT);
		}
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

			FluidStack fluid = getFluid(stack);

			if (!attemptDrain(stack, fluidHandler, ForgeDirection.getOrientation(side), fluid, world)) {
				attemptFill(stack, fluidHandler, ForgeDirection.getOrientation(side), fluid, world);
			}

			refill(stack, world, player.posX, player.posY, player.posZ);

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
		FluidStack fluid = getFluid(stack);
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

			refill(stack, world, player.posX, player.posY, player.posZ);

			return stack;
		}

		if (player.isSneaking()) {
			switchMode(stack, player);

			refill(stack, world, player.posX, player.posY, player.posZ);

			return stack;
		}

		if (!empty && fluid.amount < 1000) return stack;

		if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return stack;

		if (world.canMineBlock(player, mop.blockX, mop.blockY, mop.blockZ)) {
			if (empty) {
				FluidStack placedFluid = FluidHelper.playerPickupFluid(player, world, new int[] {mop.blockX, mop.blockY, mop.blockZ}, mop.sideHit, stack);
				if (placedFluid != null) {
					setFluid(stack, placedFluid);
					world.setBlockToAir(mop.blockX, mop.blockY, mop.blockZ);
				}
			} else {
				int[] coords = BlockHelper.getAdjacentBlock(mop.blockX, mop.blockY, mop.blockZ, mop.sideHit);

				if (FluidHelper.playerPlaceFluid(player, coords, mop.sideHit, stack, world, fluid))
					setFluid(stack, null);
			}
		}

		refill(stack, world, player.posX, player.posY, player.posZ);

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

	private boolean refill(ItemStack stack, World world, double x, double y, double z) {
		return refill(stack, world, x, y, z, getFluid(stack));
	}

	private boolean refill(ItemStack stack, World world, double x, double y, double z, FluidStack currentFluid) {
		int mode = stack.getItemDamage();
		if (mode == METADATA_EMPTY && currentFluid == null) return false;
		if (mode == METADATA_FILL && currentFluid != null && currentFluid.amount == 1000) return false;

		NBTTagCompound nbt = NBTHelper.getNBT(stack);

		if (!nbt.hasKey("fluidHandler")) return false;
		NBTTagCompound tileNBT = nbt.getCompoundTag("fluidHandler");

		int tileWorldID = tileNBT.getInteger("worldID");
		int[] tileCoords = tileNBT.getIntArray("coords");

		if (world != null) {
			if (!crossDim && world.provider.dimensionId != tileWorldID)
				return false;

			if (range != -1 && (Math.abs(tileCoords[0] - x) > range || Math.abs(tileCoords[1] - y) > range || Math.abs(tileCoords[2] - z) > range))
				return false;
		}

		World tileWorld = MinecraftServer.getServer().worldServerForDimension(tileWorldID);
		if (world == null) world = tileWorld;

		TileEntity te = tileWorld.getTileEntity(tileCoords[0], tileCoords[1], tileCoords[2]);

		if (!(te instanceof IFluidHandler)) {
			setFluidHandler(stack, null, 0);

			return false;
		}

		IFluidHandler fluidHandler = (IFluidHandler) te;

		int side = tileNBT.getInteger("side");

		ForgeDirection direction = ForgeDirection.getOrientation(side);
		switch (mode) {
			case METADATA_EMPTY:
				return attemptFill(stack, fluidHandler, direction, currentFluid, world);

			case METADATA_FILL:
				return attemptDrain(stack, fluidHandler, direction, currentFluid, world);
		}

		return false;
	}

	/**
	 * Attempt to fill a FluidHandler
	 */
	private boolean attemptFill(ItemStack stack, IFluidHandler fluidHandler, ForgeDirection direction, FluidStack fluid, World world) {
		if (fluid == null) return false;

		if (!fluidHandler.canFill(direction, fluid.getFluid())) return false;

		int filled = fluidHandler.fill(direction, fluid, !world.isRemote);

		fluid.amount -= filled;

		setFluid(stack, fluid);

		return filled != 0;
	}

	/**
	 * Attempt to drain a FluidHandler
	 */
	private boolean attemptDrain(ItemStack stack, IFluidHandler fluidHandler, ForgeDirection direction, FluidStack currentFluid, World world) {
		FluidStack drained;
		if (currentFluid == null) {
			currentFluid = fluidHandler.drain(direction, 1000, !world.isRemote);

			if (currentFluid == null || currentFluid.amount == 0)
				return false;
		} else {
			FluidStack toDrain = currentFluid.copy();
			toDrain.amount = 1000 - currentFluid.amount;

			drained = fluidHandler.drain(direction, toDrain, !world.isRemote);

			if (drained == null || drained.amount == 0) return false;

			currentFluid.amount += drained.amount;
		}

		setFluid(stack, currentFluid);

		return true;
	}

	public boolean hasFluid(ItemStack container) {
		NBTTagCompound nbt = NBTHelper.getNBT(container);

		return nbt.hasKey("fluidStack");
	}

	@Override
	public FluidStack getFluid(ItemStack container) {
		NBTTagCompound nbt = NBTHelper.getNBT(container);

		if (nbt.hasKey("fluidStack")) return FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fluidStack"));

		return null;
	}

	@Override
	public int getCapacity(ItemStack container) {
		return 1000;
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
		if (resource == null) return 0;

		FluidStack currentFluid = getFluid(container);

		if (currentFluid != null && !resource.isFluidEqual(currentFluid)) return 0;

		int toFill = Math.min(resource.amount, currentFluid == null ? 1000 : (1000 - currentFluid.amount));

		if (doFill) {
			if (currentFluid == null) {
				currentFluid = resource.copy();
				currentFluid.amount = toFill;
			} else {
				currentFluid.amount += toFill;
			}

			setFluid(container, currentFluid);

			refill(container, null, 0, 0, 0);
		}

		return toFill;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
		FluidStack currentFluid = getFluid(container);

		if (currentFluid == null) return null;

		int toDrain = Math.min(maxDrain, currentFluid.amount);

		if (doDrain) {
			currentFluid.amount -= toDrain;

			setFluid(container, currentFluid);

			refill(container, null, 0, 0, 0, currentFluid);
		}

		currentFluid.amount = toDrain;

		return currentFluid;
	}
}
package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import nf.fr.ephys.cookiecore.helpers.BlockHelper;
import nf.fr.ephys.cookiecore.helpers.FluidHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;

public class TileEntityFluidHopper extends TileEntity implements IFluidHandler, IInventory {
	private FluidStack[] fluidStacks = new FluidStack[5];
	private ItemStack[] bucketStacks = new ItemStack[fluidStacks.length + 1];

	public static final int MAX_STACK_SIZE = 2500;
	public static final int RATE = 1000;

	private int cooldown = 0;

	@Override
	public void updateEntity() {
		if (this.worldObj == null)
			return;

		if (this.worldObj.isRemote)
			return;

		if (!BlockHopper.func_149917_c(this.getBlockMetadata()))
			return;

		if (cooldown != 0) {
			cooldown--;

			return;
		}

		cooldown = 20;

		for (int i = 0; i < bucketStacks.length - 1; i++)
			fillBucket(i);

		attemptDrain();
		attemptFill();
	}

	private boolean attemptFill() {
		int orientation = BlockHopper.getDirectionFromMetadata(this.getBlockMetadata());

		int coords[] = BlockHelper.getAdjacentBlock(xCoord, yCoord, zCoord, orientation);

		TileEntity te = worldObj.getTileEntity(coords[0], coords[1], coords[2]);

		if (te instanceof IFluidHandler) {
			IFluidHandler target = (IFluidHandler) te;

			for (int i = 0; i < fluidStacks.length; i++) {
				FluidStack fluidStack = fluidStacks[i];
				ForgeDirection direction = ForgeDirection.getOrientation(orientation);

				if (fluidStack == null || !target.canFill(direction, fluidStack.getFluid()))
					continue;

				FluidStack clone = fluidStack.copy();
				clone.amount = Math.min(RATE, clone.amount);

				int filled = target.fill(direction, clone, true);

				if (filled > 0) {
					fluidStack.amount -= filled;

					if (fluidStack.amount <= 0)
						fluidStacks[i] = null;

					sendUpdate();

					return true;
				}
			}
		}

		return false;
	}

	private boolean attemptDrain() {
		TileEntity te = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);

		if (te instanceof IFluidHandler) {
			IFluidHandler source = (IFluidHandler) te;

			FluidStack drain = source.drain(ForgeDirection.DOWN, RATE, false);

			if (drain == null)
				return false;

			int filled = this.fill(ForgeDirection.UP, drain, true);

			source.drain(ForgeDirection.DOWN, filled, true);

			return true;
		}

		return attemptBlockSuckUp();
	}

	private boolean attemptBlockSuckUp() {
		if (worldObj.getTileEntity(xCoord, yCoord + 1, zCoord) != null) return false;

		Block block = worldObj.getBlock(xCoord, yCoord + 1, zCoord);

		Fluid fluid = FluidHelper.getFluidForBlock(block);

		if (fluid == null)
			return false;

		boolean isWater = fluid == FluidRegistry.WATER;
		int metadata = worldObj.getBlockMetadata(xCoord, yCoord + 1, zCoord);

		if (isWater) {
			FluidStack stack = new FluidStack(fluid, 1000 / (metadata + 1));

			fill(ForgeDirection.UP, stack, true);
		} else if (metadata == 0) {
			FluidStack stack = new FluidStack(fluid, 1000);

			int filled = fill(ForgeDirection.UP, stack, false);

			if (filled != 1000)
				return false;

			fill(ForgeDirection.UP, stack, true);

			worldObj.setBlockToAir(xCoord, yCoord + 1, zCoord);
		}

		return true;
	}

	private void fillBucket(int slot) {
		ItemStack input = bucketStacks[slot];

		if (input == null)
			return;

		if (input.getItem() instanceof IFluidContainerItem) {
			IFluidContainerItem fluidContainer = (IFluidContainerItem) input.getItem();
			ItemStack output = input.copy();

			FluidStack itemFluidContents = fluidContainer.drain(output, fluidContainer.getCapacity(output), false);

			if (itemFluidContents != null) {
				// drain
				if (fill(ForgeDirection.UNKNOWN, itemFluidContents, false) != itemFluidContents.amount)
					return;

				fluidContainer.drain(output, fluidContainer.getCapacity(output), true);

				if (!insertToOutput(output))
					return;

				fill(ForgeDirection.UNKNOWN, itemFluidContents, true);
			} else {
				// fill
				FluidStack localFluid = fluidStacks[slot];

				if (localFluid == null) return;

				int filled = fluidContainer.fill(output, localFluid, true);

				if (!insertToOutput(output))
					return;

				localFluid.amount -= filled;

				if (localFluid.amount <= 0)
					fluidStacks[slot] = null;
			}
		} else if (FluidContainerRegistry.isFilledContainer(input)) {
			// empty container
			FluidStack outputFluid = FluidContainerRegistry.getFluidForFilledItem(input);
			ItemStack outputStack = input.getItem().getContainerItem(input);

			int filled = fill(ForgeDirection.UNKNOWN, outputFluid, false);

			if (filled != outputFluid.amount)
				return;

			if (outputStack != null && !insertToOutput(outputStack))
				return;

			fill(ForgeDirection.UNKNOWN, outputFluid, true);
		} else {
			// drain container
			FluidStack stack = fluidStacks[slot];

			if (stack == null || stack.amount < FluidContainerRegistry.BUCKET_VOLUME)
				return;

			ItemStack output = FluidContainerRegistry.fillFluidContainer(stack, input);

			if (output == null || !insertToOutput(output))
				return;

			stack.amount -= 1000;

			if (stack.amount <= 0)
				fluidStacks[slot] = null;
		}

		decrStackSize(slot, 1);
	}

	private boolean insertToOutput(ItemStack output) {
		ItemStack outputSlot = bucketStacks[bucketStacks.length - 1];

		if (outputSlot != null) {
			if (!ItemStack.areItemStacksEqual(outputSlot, output))
				return false;

			if (outputSlot.stackSize + 1 >= outputSlot.getMaxStackSize())
				return false;

			outputSlot.stackSize++;
		} else {
			bucketStacks[bucketStacks.length - 1] = output;
		}

		return true;
	}

	private void writeFluidsToNBT(NBTTagCompound nbt, boolean writeIfEmpty) {
		NBTTagCompound fluidStackNBT = new NBTTagCompound();

		int nbSet = 0;
		for (int i = 0; i < fluidStacks.length; i++) {
			if (fluidStacks[i] != null) {
				NBTTagCompound fluidNBT = new NBTTagCompound();
				fluidStacks[i].writeToNBT(fluidNBT);

				fluidStackNBT.setTag(Integer.toString(i), fluidNBT);

				nbSet++;
			}
		}

		if (nbSet != 0 || writeIfEmpty) {
			nbt.setTag("fluidStacks", fluidStackNBT);
		}
	}

	private void readFluidsFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("fluidStacks")) {
			NBTTagCompound fluidStacksNBT = nbt.getCompoundTag("fluidStacks");

			for (int i = 0; i < fluidStacks.length; i++) {
				if (fluidStacksNBT.hasKey(Integer.toString(i))) {
					fluidStacks[i] = FluidStack.loadFluidStackFromNBT(fluidStacksNBT.getCompoundTag(Integer.toString(i)));
				} else {
					fluidStacks[i] = null;
				}
			}
		}
	}

	private void writeInventoryToNBT(NBTTagCompound nbt) {
		NBTTagCompound stacksNBT = new NBTTagCompound();
		for (int i = 0; i < bucketStacks.length; i++) {
			if (bucketStacks[i] == null) continue;

			NBTTagCompound stackNBT = new NBTTagCompound();
			bucketStacks[i].writeToNBT(stackNBT);

			stackNBT.setTag(Integer.toString(i), stackNBT);
		}

		nbt.setTag("inventory", stacksNBT);
	}

	private void readInventoryFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("inventory")) {
			NBTTagCompound stacksNBT = nbt.getCompoundTag("inventory");

			for (int i = 0; i < bucketStacks.length; i++) {
				if (stacksNBT.hasKey(Integer.toString(i)))
					bucketStacks[i] = ItemStack.loadItemStackFromNBT(stacksNBT.getCompoundTag(Integer.toString(i)));
				else
					bucketStacks[i] = null;
			}
		}
	}

	public void getFluidsFromStack(ItemStack stack) {
		readFluidsFromNBT(NBTHelper.getNBT(stack));
	}

	public void setFluidsToStack(ItemStack stack) {
		writeFluidsToNBT(NBTHelper.getNBT(stack), false);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFluidsFromNBT(pkt.func_148857_g());
		readInventoryFromNBT(pkt.func_148857_g());
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeFluidsToNBT(nbt, true);
		this.writeInventoryToNBT(nbt);

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		writeFluidsToNBT(nbt, false);
		writeInventoryToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		readFluidsFromNBT(nbt);
		writeInventoryToNBT(nbt);
	}

	public int getComparatorInput() {
		return 0;
	}

	private int getSlotForFluid(FluidStack fluid) {
		int firstEmptySlot = -1;

		for (int i = fluidStacks.length - 1; i >= 0; i--) {
			if (fluidStacks[i] == null) {
				firstEmptySlot = i;
			} else {
				if (fluidStacks[i].amount < MAX_STACK_SIZE && fluidStacks[i].isFluidEqual(fluid)) {
					return i;
				}
			}
		}

		return firstEmptySlot;
	}

	private int getFirstFilledSlot() {
		for (int i = 0; i < fluidStacks.length; i++) {
			if (fluidStacks[i] != null)
				return i;
		}

		return -1;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		int slot = getSlotForFluid(resource);

		if (slot == -1)
			return 0;

		int[] filledPerSlot = new int[fluidStacks.length];
		int filledTotal = 0;

		// start by the slot containing the fluid
		int toFill = Math.min(fluidStacks[slot] == null ? MAX_STACK_SIZE : MAX_STACK_SIZE - fluidStacks[slot].amount, resource.amount);
		filledPerSlot[slot] = toFill;
		filledTotal += toFill;

		// then check the other slots
		for (int i = 0; i < fluidStacks.length && filledTotal < resource.amount; i++) {
			if (i == slot) continue;

			int availableSpace;
			if (fluidStacks[i] != null) {
				if (!fluidStacks[i].isFluidEqual(resource)) continue;

				availableSpace = MAX_STACK_SIZE - fluidStacks[i].amount;
			} else {
				availableSpace = MAX_STACK_SIZE;
			}

			toFill = Math.min(resource.amount - filledTotal, availableSpace);

			filledPerSlot[i] = toFill;
			filledTotal += toFill;
		}

		if (doFill) {
			for (int i = 0; i < fluidStacks.length; i++) {
				if (filledPerSlot[i] == 0) continue;

				if (fluidStacks[i] == null)
					fluidStacks[i] = new FluidStack(resource.getFluid().getID(), filledPerSlot[i], resource.tag);
				else
					fluidStacks[i].amount += filledPerSlot[i];
			}

			sendUpdate();
		}

		return filledTotal;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		int slot = getSlotForFluid(resource);

		if (slot == -1 || fluidStacks[slot] == null)
			return null;

		return drain(slot, resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		int slot = getFirstFilledSlot();

		if (slot == -1)
			return null;

		return drain(slot, maxDrain, doDrain);
	}

	private FluidStack drain(int slot, int amount, boolean doDrain) {
		FluidStack stack = fluidStacks[slot];

		int drained = Math.min(Math.min(amount, RATE), stack.amount);

		if (doDrain) {
			stack.amount -= drained;

			if (stack.amount <= 0)
				fluidStacks[slot] = null;

			sendUpdate();
		}

		return new FluidStack(stack.getFluid().getID(), drained, stack.tag);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		FluidTankInfo[] tanks = new FluidTankInfo[fluidStacks.length];

		for (int i = 0; i < fluidStacks.length; i++) {
			if (fluidStacks[i] == null)
				continue;

			tanks[i] = new FluidTankInfo(fluidStacks[i], MAX_STACK_SIZE);
		}

		return tanks;
	}

	private void sendUpdate() {
		markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return oldBlock != newBlock;
	}

	public void onBlockUpdate(int tileX, int tileY, int tileZ) {
		attemptBlockSuckUp();
	}

	@Override
	public int getSizeInventory() {
		return bucketStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return bucketStacks[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int nbItems) {
		ItemStack stack = bucketStacks[slot];

		if (stack == null)
			return null;

		nbItems = Math.min(nbItems, stack.stackSize);

		ItemStack clone = stack.copy();
		clone.stackSize = nbItems;

		bucketStacks[slot].stackSize -= nbItems;
		if (bucketStacks[slot].stackSize <= 0)
			bucketStacks[slot] = null;

		sendUpdate();

		return clone;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);

		if (stack != null) {
			setInventorySlotContents(slot, null);
		}

		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();

		bucketStacks[slot] = stack;

		if (slot < bucketStacks.length - 1)
			fillBucket(slot);

		sendUpdate();
	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return slot < bucketStacks.length - 1 && (stack == null || FluidContainerRegistry.isContainer(stack) || stack.getItem() instanceof IFluidContainerItem);
	}
}
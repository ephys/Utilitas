package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.util.cofh.TileEnergyHandler;

import java.util.List;

public class TileEntityPotionDiffuser extends TileEnergyHandler implements IFluidHandler {
	private FluidTank tank = new FluidTank(20000);

	//private List<PotionEffect> potionEffects = new ArrayList<>();

	//private ItemStack[] inventorySlots = new ItemStack[2];

	//public static final int SLOT_POTION = 1;
	//public static final int SLOT_BOTTLE = 0;

	public static final int RANGE = 8;
	public static final int ENERGY_CONSUMPTION = 10;
	public static final int INTERVAL = 30;

/*	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound potions = new NBTTagCompound();

		int i = 0;
		for (PotionEffect potion : potionEffects) {
			NBTTagCompound potionNBT = new NBTTagCompound();

			potion.writeCustomPotionEffectToNBT(potionNBT);

			potions.setTag(String.valueOf(i++), potionNBT);
		}

		nbt.setTag("potionEffects", potions);

		NBTHelper.setWritable(nbt, "slot0", this.inventorySlots[0]);
		NBTHelper.setWritable(nbt, "slot1", this.inventorySlots[1]);

		super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.inventorySlots[0] = NBTHelper.getItemStack(nbt, "slot0", null);
		this.inventorySlots[1] = NBTHelper.getItemStack(nbt, "slot1", null);

		if (nbt.hasKey("potionEffects")) {
			NBTTagCompound potions = nbt.getCompoundTag("potionEffects");

			this.potionEffects = new ArrayList<>();

			for (int i = 0; potions.hasKey(String.valueOf(i)); i++) {
				PotionEffect potion = PotionEffect.readCustomPotionEffectFromNBT(potions.getCompoundTag(String.valueOf(i)));

				potionEffects.add(potion);
			}
		}

		super.readFromNBT(nbt);
	} */

	@Override
	public int fill(ForgeDirection forgeDirection, FluidStack fluidStack, boolean b) {
		return this.tank.fill(fluidStack, b);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource != null && tank.getFluid().equals(resource))
			return drain(from, resource.amount, doDrain);

		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return this.tank.drain(maxDrain, doDrain);
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
		return new FluidTankInfo[] { this.tank.getInfo() };
	}

	@Override
	public void updateEntity() {
		if (worldObj.getTotalWorldTime() % INTERVAL != 0) return;

		if (PlayerProxies.getConfig().requiresPower()) {
			if (storage.getEnergyStored() < ENERGY_CONSUMPTION) return;

			storage.extractEnergy(ENERGY_CONSUMPTION, false);
		}

		if (this.tank.getFluidAmount() >= INTERVAL) {
			@SuppressWarnings("unchecked")
			List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(
				this.xCoord, this.yCoord, this.zCoord,
				this.xCoord, this.yCoord, this.zCoord
			).expand(RANGE, RANGE, RANGE));

			for (EntityLivingBase entity : entities) {
				Block block = this.tank.getFluid().getFluid().getBlock();

				// TODO: add a new fluid that will remove the effects
				if (block.equals(Blocks.water)) {
					entity.clearActivePotions();
				} else if (block.equals(Blocks.lava)) {
					if (!entity.isImmuneToFire()) {
						entity.setFire(INTERVAL);
					}
				} else {
					block.onEntityCollidedWithBlock(worldObj, xCoord, yCoord, zCoord, entity);
					block.onEntityWalking(worldObj, xCoord, yCoord, zCoord, entity);
				}
			}

			this.tank.drain(INTERVAL, true);
		}

		/* if (this.potionEffects.isEmpty() && this.inventorySlots[SLOT_POTION] != null &&
				(this.inventorySlots[SLOT_BOTTLE] == null ||
						(this.inventorySlots[SLOT_BOTTLE].getItem().equals(Items.glass_bottle)) && this.inventorySlots[SLOT_BOTTLE].stackSize < 64)) {
			ItemPotion potion = (ItemPotion) this.inventorySlots[SLOT_POTION].getItem();

			this.potionEffects = new ArrayList(potion.getEffects(this.inventorySlots[SLOT_POTION]));

			this.setInventorySlotContents(SLOT_POTION, null);

			if (this.inventorySlots[SLOT_BOTTLE] == null)
				this.setInventorySlotContents(SLOT_BOTTLE, new ItemStack(Items.glass_bottle, 0));

			inventorySlots[SLOT_BOTTLE].stackSize++;
		}

		// activate potion effect
		for (int i = 0; i < potionEffects.size(); i++) {
			PotionEffect potion = potionEffects.get(i);

			int duration = Math.min(potion.getDuration(), INTERVAL + 40);

			PotionEffect activePotion = new PotionEffect(potion.getPotionID(), duration, potion.getAmplifier(), true);

			potion = new PotionEffect(potion.getPotionID(), potion.getDuration() - INTERVAL, potion.getAmplifier(), potion.getIsAmbient());

			if (potion.getDuration() <= 0)
				potionEffects.remove(i);
			else
				potionEffects.set(i, potion);

			for (EntityLivingBase entity : entities) {
				entity.addPotionEffect(activePotion);
			}
		} */

		super.updateEntity();
	}
/*
	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return i < 2 ? this.inventorySlots[i] : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);

		if (stack != null && amount > 0) {
			setInventorySlotContents(slot, null);
		}

		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack stack = getStackInSlot(i);

		if (stack != null) {
			setInventorySlotContents(i, null);
		}

		return stack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		if (!isItemValidForSlot(i, stack))
			return;

		if (stack != null) {
			if (stack.stackSize > getInventoryStackLimit())
				stack.stackSize = getInventoryStackLimit();
		}

		this.inventorySlots[i] = stack;
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
		return 1;
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
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return itemstack == null ||
				(slot == SLOT_BOTTLE && itemstack.getItem().equals(Items.glass_bottle)) ||
				(slot == SLOT_POTION && itemstack.getItem().equals(Items.potionitem));
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { SLOT_BOTTLE, SLOT_POTION };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		return slot == SLOT_POTION && (itemstack == null || itemstack.getItem().equals(Items.potionitem));
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		return slot == SLOT_BOTTLE && (itemstack == null || itemstack.getItem().equals(Items.glass_bottle));
	} */
}
package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;
import nf.fr.ephys.playerproxies.util.cofh.TileEnergyHandler;

import java.util.ArrayList;
import java.util.List;

public class TileEntityPotionDiffuser extends TileEnergyHandler implements ISidedInventory, IFluidHandler {
	private FluidTank tank = new FluidTank(20000);

	private List<PotionEffect> potionEffects = new ArrayList<>();

	private ItemStack[] inventorySlots = new ItemStack[2];

	public static final int SLOT_POTION = 1;
	public static final int SLOT_BOTTLE = 0;

	public static final int RANGE = 8;
	public static final int ENERGY_CONSUMPTION = 25;
	public static final int INTERVAL = 10;

	@Override
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

		this.storage.writeToNBT(nbt);

		super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.storage.readFromNBT(nbt);

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
	}

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
		if (storage.getEnergyStored() < ENERGY_CONSUMPTION) return;

		storage.extractEnergy(ENERGY_CONSUMPTION, false);

		if (this.worldObj.getTotalWorldTime() % INTERVAL == 0) {
			List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(
				this.xCoord - RANGE, this.yCoord - RANGE, this.zCoord - RANGE,
				this.xCoord + RANGE, this.yCoord + RANGE, this.zCoord + RANGE
			));

			if (this.tank.getFluidAmount() >= INTERVAL) {
				for (EntityLivingBase entity : entities) {
					Block block = this.tank.getFluid().getFluid().getBlock();

					// TODO: add a new fluid that will remove the effects
					if (block.getMaterial().equals(Material.water)) {
						entity.clearActivePotions();
					} else if (block.getMaterial().equals(Material.lava)) {
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

			if (this.potionEffects.isEmpty() && this.inventorySlots[SLOT_POTION] != null && this.inventorySlots[SLOT_BOTTLE] == null) {
				ItemPotion potion = (ItemPotion) this.inventorySlots[SLOT_POTION].getItem();

				this.potionEffects = potion.getEffects(this.inventorySlots[SLOT_POTION]);

				this.setInventorySlotContents(SLOT_POTION, null);

				this.setInventorySlotContents(SLOT_BOTTLE, new ItemStack(Items.glass_bottle));
			}

			// activate potion effect
			for (int i = 0; i < potionEffects.size(); i++) {
				PotionEffect potionEffect = potionEffects.get(i);

				int duration = Math.min(potionEffect.getDuration(), INTERVAL+5);

				PotionEffect activePotion = new PotionEffect(potionEffect.getPotionID(), duration, potionEffect.getAmplifier(), true);
				potionEffect = new PotionEffect(potionEffect.getPotionID(), potionEffect.getDuration() - duration, potionEffect.getAmplifier(), potionEffect.getIsAmbient());

				if (potionEffect.getDuration() <= 0)
					potionEffects.remove(i);
				else
					potionEffects.set(i, potionEffect);

				for (EntityLivingBase entity : entities) {
					entity.addPotionEffect(activePotion);
				}
			}
		}

		super.updateEntity();
	}

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
		switch(slot) {
			case SLOT_BOTTLE: return itemstack == null || (itemstack.getItem().equals(Items.glass_bottle) && itemstack.stackSize <= getInventoryStackLimit());
			case SLOT_POTION: return itemstack == null || (itemstack.getItem().equals(Items.potionitem) && itemstack.stackSize <= getInventoryStackLimit());
			default: return false;
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return side < 2 ? new int[] { side } : new int[] {};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		return side < 2 && side == slot && isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		return canInsertItem(slot, itemstack, side);
	}
}

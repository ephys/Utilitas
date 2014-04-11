package nf.fr.ephys.playerproxies.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;
import nf.fr.ephys.playerproxies.util.cofh.TileEnergyHandler;

public class TileEntityPotionDiffuser extends TileEnergyHandler implements ISidedInventory, IFluidHandler {
	private FluidTank tank = new FluidTank(20000);

	private List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
	
	private ItemStack[] inventorySlots = new ItemStack[2];
	
	public static final int SLOT_POTION = 1;
	public static final int SLOT_BOTTLE = 0;

	public static final int RANGE = 8;
	public static final int ENERGY_CONSUMPTION = 25;
	public static final int INTERVAL = 10;

	private int tick = INTERVAL;
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound potions = new NBTTagCompound();
		
		int i = 0;
		for (PotionEffect potion : potionEffects) {
			NBTTagCompound potionNBT = new NBTTagCompound();
			
			potion.writeCustomPotionEffectToNBT(potionNBT);
			
			potions.setCompoundTag(String.valueOf(i++), potionNBT);
		}

		nbt.setCompoundTag("potionEffects", potions);

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
			
			this.potionEffects = new ArrayList<PotionEffect>();
			
			for (int i = 0; potions.hasKey(String.valueOf(i)); i++) {
				PotionEffect potion = PotionEffect.readCustomPotionEffectFromNBT(potions.getCompoundTag(String.valueOf(i)));

				potionEffects.add(potion);
			}
		}
		
		super.readFromNBT(nbt);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return this.tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource != null && tank.getFluid().equals(resource))
			return drain(from, resource.amount, doDrain);

		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		FluidStack stack = this.tank.drain(maxDrain, doDrain);

		return stack;
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

		if (tick-- == 0) {
			tick = INTERVAL;

			List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(
				this.xCoord - RANGE, this.yCoord - RANGE, this.zCoord - RANGE, 
				this.xCoord + RANGE, this.yCoord + RANGE, this.zCoord + RANGE
			));

			if (this.tank.getFluidAmount() >= INTERVAL) {
				for (EntityLivingBase entity : entities) {
					Block block = Block.blocksList[this.tank.getFluid().getFluid().getBlockID()];
	
					block.onEntityCollidedWithBlock(worldObj, xCoord, yCoord, zCoord, entity);
					block.onEntityWalking(worldObj, xCoord, yCoord, zCoord, entity);
				}
	
				this.tank.drain(INTERVAL, true);
			}

			if (this.potionEffects.isEmpty() && this.inventorySlots[SLOT_POTION] != null && this.inventorySlots[SLOT_BOTTLE] == null) {
				ItemPotion potion = (ItemPotion) this.inventorySlots[SLOT_POTION].getItem();
				
				this.potionEffects = potion.getEffects(this.inventorySlots[SLOT_POTION]);
				
				this.setInventorySlotContents(SLOT_POTION, null);
				
				this.setInventorySlotContents(SLOT_BOTTLE, new ItemStack(Item.glassBottle));
			}

			// activate potion effect
			for (int i = 0; i < potionEffects.size(); i++) {
				PotionEffect potionEffect = potionEffects.get(i);
				PotionEffect activePotion = new PotionEffect(potionEffect);

				int duration = Math.min(potionEffect.duration, INTERVAL+5);

				activePotion.duration = duration;
				potionEffect.duration -= duration;

				if (potionEffect.duration <= 0)
					potionEffects.remove(i);

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

		onInventoryChanged();
	}
	
	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
	}

	@Override
	public String getInvName() {
		return "ephys.pp.tileEntityPotionDiffuser";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5, yCoord + 0.5,
						zCoord + 0.5) < 64;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		switch(slot) {
			case SLOT_BOTTLE: return itemstack == null || (itemstack.itemID == Item.glassBottle.itemID && itemstack.stackSize == getInventoryStackLimit());
			case SLOT_POTION: return itemstack == null || (itemstack.itemID == Item.potion.itemID && itemstack.stackSize == getInventoryStackLimit());
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

package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import nf.fr.ephys.playerproxies.common.registry.BeaconEffectsRegistry;

import java.util.List;

public class TileEntityBeaconTierII extends TileEntity implements IInventory {
	private int level = 0;
	private boolean isTierTwo = false;
	private int totalBlocks = 0;
	private int totalPositive = 0;
	private int totalNegative = 0;

	public static final int MAX_LEVELS = 8;
	public static final int TIERII_LEVEL = 4;
	public static final int MAX_ITEMS = 4;

	private Item[] containedItems = new Item[MAX_ITEMS];

	@Override
	public void updateEntity() {
		if (this.worldObj.getTotalWorldTime() % 200L == 0L) {
			validateStructure();
			addEffects();
		}
	}

	private void validateStructure() {
		level = 0;

		isTierTwo = this.worldObj.getBlock(xCoord, yCoord + 1, zCoord).equals(Blocks.dragon_egg);

		if (isTierTwo || this.worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord)) {
			totalNegative = totalPositive = totalBlocks = 0;

			for (int i = 0; i < MAX_LEVELS; i++) {
				if (!isValidLevel(i + 1))
					break;

				level++;
			}
		}
	}

	public boolean isValidLevel(int level) {
		for (int x = -level; x <= level; x++) {
			for (int z = -level; z <= level; z++) {
				Block block = this.worldObj.getBlock(this.xCoord + x, this.yCoord - level, this.zCoord + z);
				if (block.equals(Blocks.coal_block))
					totalNegative++;

				if (block.equals(Blocks.diamond_block))
					totalPositive++;

				if (block.equals(Blocks.coal_block) || !block.isBeaconBase(worldObj, this.xCoord + x, this.yCoord - level, this.zCoord + z, xCoord, yCoord, zCoord))
					return false;

				totalBlocks++;
			}
		}

		return true;
	}

	public boolean isActive() {
		return level > 0;
	}

	private void addEffects() {
		if (worldObj.isRemote || !isActive()) return;

		if (level >= 4) {
			AxisAlignedBB area = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord - level, this.zCoord).expand(10.0D, 5.0D, 10.0D);
			for (Object player : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, area)) {
				((EntityPlayer) player).triggerAchievement(AchievementList.field_150965_K);
			}
		}

		int[] effects = BeaconEffectsRegistry.getEffects(containedItems, level);

		if (effects.length != 0) {
			double range = (this.level * 15) + 10;

			AxisAlignedBB area = AxisAlignedBB.getBoundingBox(this.xCoord + 1, this.yCoord + 1, this.zCoord + 1, this.xCoord, this.yCoord - level, this.zCoord).expand(range, range, range);
			List players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, area);

			for (int effect : effects) {
				if (shouldSkipEffect(effect)) continue;

				for (Object player : players) {
					((EntityPlayer) player).addPotionEffect(new PotionEffect(effect, isTierTwo ? 500 : 250, isTierTwo ? 0 : 1, true));
				}
			}
		}
	}

	private boolean shouldSkipEffect(int effectID) {
		if (Potion.potionTypes[effectID].isBadEffect())
			return Math.random() < totalPositive / totalBlocks;

		return Math.random() < totalNegative / totalBlocks;
	}

	@Override
	public int getSizeInventory() {
		return MAX_ITEMS;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return new ItemStack(containedItems[i], 1);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (containedItems[i] == null) return null;

		ItemStack stack = new ItemStack(containedItems[i]);

		setInventorySlotContents(i, null);

		return stack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {
		if (!isItemValidForSlot(i, itemStack)) return;

		this.containedItems[i] = itemStack.getItem();
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);

		setInventorySlotContents(slot, null);

		return stack;
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
	public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemStack) {
		if (itemStack == null) return true;

		for (Item item : containedItems) {
			if (itemStack.getItem().equals(item)) return false;
		}

		return BeaconEffectsRegistry.hasItem(itemStack.getItem());
	}

	public int getItemSlot(ItemStack itemstack) {
		Item item = itemstack.getItem();

		for (int i = 0; i < containedItems.length; i++) {
			if (item.equals(containedItems[i]))
				return i;
		}

		return -1;
	}

	public int getItemCount() {
		for (int i = 0; i < containedItems.length; i++) {
			if (containedItems[i] == null)
				return i;
		}

		return containedItems.length;
	}
}
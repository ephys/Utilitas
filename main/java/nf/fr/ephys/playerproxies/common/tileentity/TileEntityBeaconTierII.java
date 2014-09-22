package nf.fr.ephys.playerproxies.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.AxisAlignedBB;
import nf.fr.ephys.cookiecore.helpers.MathHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.registry.BeaconEffectsRegistry;

import java.awt.*;
import java.util.List;

public class TileEntityBeaconTierII extends TileEntityBeacon {
	private int level = 0;
	private int tier = 1;
	private int totalBlocks = 0;
	private int totalNegative = 0;

	// render stuff
	private long lastWorldTime = 0;
	private float rotation = 0;
	private Color beaconColor = Color.white;
	public static final Color BAD_COLOR = new Color(140, 14, 36);
	public static final Color COLOR_GAIABOSS = new Color(255, 0, 98);
	public static final Color COLOR_ENDSIEGE = new Color(21, 21, 21);

	public int displayTick = 0;

	public static final int MAX_LEVELS = 6;
	public static final int TIERII_LEVEL = 4;
	public static final int MAX_ITEMS = 4;

	private int nbItems = 0;
	private ItemStack[] containedItems = new ItemStack[MAX_ITEMS];
	private List<BeaconEffectsRegistry.Effect> currentEffects;

	// Botania special code o/
	public static Class<?> doppleganger = null;

	@Override
	public int getLevels() {
		return level;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		level = NBTHelper.getInt(nbt, "level", 0);
		tier = NBTHelper.getInt(nbt, "tier", 1);
		totalBlocks = NBTHelper.getInt(nbt, "totalBlocks", 0);
		totalNegative = NBTHelper.getInt(nbt, "totalNegative", 0);

		containedItems = new ItemStack[MAX_ITEMS];
		nbItems = 0;

		for (int i = 0; i < containedItems.length; i++) {
			if (!nbt.hasKey("item_"+i))
				continue;

			containedItems[i] = NBTHelper.getItemStack(nbt, "item_"+i);
			nbItems++;
		}

		super.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("level", level);
		nbt.setInteger("tier", tier);
		nbt.setInteger("totalBlocks", totalBlocks);
		nbt.setInteger("totalNegative", totalNegative);

		for (int i = 0; i < containedItems.length; i++) {
			if (containedItems[i] == null) continue;

			NBTHelper.setWritable(nbt, "item_"+i, containedItems[i]);
		}

		super.writeToNBT(nbt);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);

		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
	}

	@Override
	public void updateEntity() {
		if (worldObj.getTotalWorldTime() % 80L == 0L) {
			validateStructure();
			addEffects();
		}
	}

	public void onBlockUpdate() {
		this.validateStructure();
	}

	private boolean isNearBeacon() {
		return PlayerProxies.Blocks.betterBeacon.equals(worldObj.getBlock(xCoord - 1, yCoord, zCoord)) ||
				PlayerProxies.Blocks.betterBeacon.equals(worldObj.getBlock(xCoord + 1, yCoord, zCoord)) ||
				PlayerProxies.Blocks.betterBeacon.equals(worldObj.getBlock(xCoord, yCoord, zCoord - 1)) ||
				PlayerProxies.Blocks.betterBeacon.equals(worldObj.getBlock(xCoord, yCoord, zCoord + 1));
	}

	private void validateStructure() {
		level = 0;

		if (!isNearBeacon() && this.worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord)) {
			totalNegative = totalBlocks = 0;

			for (int i = 0; i < MAX_LEVELS; i++) {
				if (!isValidLevel(i + 1))
					break;

				level++;
			}

			boolean hasDragonEgg = this.worldObj.getBlock(xCoord, yCoord + 1, zCoord).equals(Blocks.dragon_egg);

			tier = 1;
			if (hasDragonEgg)
				tier++;

			if (level >= TIERII_LEVEL)
				tier++;

			boolean hasDoppleganger = doppleganger != null && !worldObj.getEntitiesWithinAABB(doppleganger, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord, yCoord + 1, zCoord).expand(32F, 32F, 32F)).isEmpty();

			if (hasDoppleganger)
				totalNegative = totalBlocks;

			if (worldObj.isRemote) {
				if (hasDoppleganger) {
					beaconColor = COLOR_GAIABOSS;
				} else {
					beaconColor = MathHelper.gradient(Color.white, BAD_COLOR, negativity());

					if (hasDragonEgg)
						beaconColor = MathHelper.gradient(beaconColor, Color.magenta, 0.5F);
				}
			}
		}
	}

	public static boolean isBlockNegative(Block block) {
		return block.equals(Blocks.coal_block);
	}

	@SideOnly(Side.CLIENT)
	public Color getBeaconColor() { return beaconColor; }

	public boolean isValidLevel(int level) {
		for (int x = -level; x <= level; x++) {
			for (int z = -level; z <= level; z++) {
				Block block = this.worldObj.getBlock(this.xCoord + x, this.yCoord - level, this.zCoord + z);

				if (!block.equals(Blocks.coal_block) && !block.isBeaconBase(worldObj, this.xCoord + x, this.yCoord - level, this.zCoord + z, xCoord, yCoord, zCoord))
					return false;

				if (isBlockNegative(block))
					totalNegative++;

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

		if (currentEffects == null)
			currentEffects =  BeaconEffectsRegistry.getEffects(containedItems, level);

		if (currentEffects.size() != 0) {
			double range = ((this.level * 15) + 10) / (1 + this.negativity());

			AxisAlignedBB area = AxisAlignedBB.getBoundingBox(this.xCoord + 1, this.yCoord + 1, this.zCoord + 1, this.xCoord, this.yCoord - level, this.zCoord).expand(range, range, range);
			List entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, area);

			for (BeaconEffectsRegistry.Effect effectObj : currentEffects) {
				int currentEffect = effectObj.getPotionEffect();
				if (shouldSkipEffect(currentEffect)) continue;

				for (Object o : entities) {
					EntityLivingBase entity = (EntityLivingBase) o;

					int effect = currentEffect;
					if (entity.isEntityUndead()) {
						if (effect == Potion.regeneration.getId())
							effect = Potion.poison.getId();
						else if (effect == Potion.poison.getId())
							effect = Potion.regeneration.getId();
						else if (effect == Potion.heal.getId())
							effect = Potion.harm.getId();
						else if (effect == Potion.harm.getId())
							effect = Potion.heal.getId();
					}

					int tier;
					if (effectObj.getMaxTier() < 0)
						tier = this.tier - 1;
					else
						tier = Math.min(effectObj.getMaxTier(), this.tier - 1);

					if (Potion.potionTypes[effect].isInstant() && Math.random() < 0.1)
						entity.addPotionEffect(new PotionEffect(effect, 1, tier, true));
					else
						entity.addPotionEffect(new PotionEffect(effect, 250 * this.tier, tier, true));
				}
			}
		}
	}

	public float negativity() {
		return (float) totalNegative / totalBlocks;
	}

	private boolean shouldSkipEffect(int effectID) {
		if (Potion.potionTypes[effectID].isBadEffect)
			return Math.random() > negativity();

		return Math.random() < negativity();
	}

	@Override
	public int getSizeInventory() {
		return MAX_ITEMS;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return containedItems[i];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (containedItems[i] == null) return null;

		ItemStack stack = containedItems[i];

		setInventorySlotContents(i, null);

		return stack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {
		if (!isItemValidForSlot(i, itemStack)) return;

		if (this.containedItems[i] != null && itemStack == null)
			nbItems--;
		else if (this.containedItems[i] == null && itemStack != null)
			nbItems++;

		this.containedItems[i] = itemStack;

		this.currentEffects = null;

		markDirty();
	}

	@Override
	public void markDirty() {
		super.markDirty();

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		return itemStack == null || (containedItems[i] == null && getItemSlot(itemStack) == -1 && BeaconEffectsRegistry.hasItem(itemStack));
	}

	@SideOnly(Side.CLIENT)
	public float getRotation() {
		if (!isActive())
			return 0.0F;

		int i = (int)(worldObj.getTotalWorldTime() - lastWorldTime);
		lastWorldTime = worldObj.getTotalWorldTime();

		if (i > 1) {
			rotation -= i / 40.0F;
			if (rotation < 0.0F) {
				rotation = 0.0F;
			}
		}

		rotation += 0.025F;
		if (rotation > 1.0F) {
			rotation = 1.0F;
		}

		return rotation;
	}

	public int getItemSlot(ItemStack itemstack) {
		if (itemstack == null) return -1;

		for (int i = 0; i < containedItems.length; i++) {
			if (containedItems[i] == null) continue;

			if (itemstack.isItemEqual(containedItems[i]))
				return i;
		}

		return -1;
	}

	@Override
	public int getSecondaryEffect() {
		return currentEffects.size() > 1 ? currentEffects.get(1).getPotionEffect() : 0;
	}

	@Override
	public int getPrimaryEffect() {
		return currentEffects.size() > 0 ? currentEffects.get(0).getPotionEffect() : 0;
	}

	public int getItemCount() {
		return nbItems;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
}
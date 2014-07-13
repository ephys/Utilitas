package nf.fr.ephys.playerproxies.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
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
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.registry.BeaconEffectsRegistry;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

import java.awt.*;
import java.util.List;

public class TileEntityBeaconTierII extends TileEntityBeacon {
	private int level = 0;
	private int tier = 1;
	private int totalBlocks = 0;
	private int totalPositive = 0;
	private int totalNegative = 0;

	// render stuff
	private long lastWorldTime = 0;
	private float rotation = 0;
	private Color beaconColor = Color.white;
	public int displayTick = 0;

	public static final int MAX_LEVELS = 6;
	public static final int TIERII_LEVEL = 4;
	public static final int MAX_ITEMS = 4;

	private ItemStack[] containedItems = new ItemStack[MAX_ITEMS];
	private int nbItems = 0;

	// this is because Potion.isBadEffect() is SideOnly(Side.CLIENT) (but getLiquidColor isn't, I'm not sure I get the logic behind this)
	public static boolean[] badPotionEffects = new boolean[Potion.potionTypes.length];

	// endfix

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		level = NBTHelper.getInt(nbt, "level", 0);
		tier = NBTHelper.getInt(nbt, "tier", 1);
		totalBlocks = NBTHelper.getInt(nbt, "totalBlocks", 0);
		totalPositive = NBTHelper.getInt(nbt, "totalPositive", 0);
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
		nbt.setInteger("totalPositive", totalPositive);
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
		return worldObj.getBlock(xCoord - 1, yCoord, zCoord).equals(PlayerProxies.Blocks.betterBeacon) ||
				worldObj.getBlock(xCoord + 1, yCoord, zCoord).equals(PlayerProxies.Blocks.betterBeacon) ||
				worldObj.getBlock(xCoord, yCoord, zCoord - 1).equals(PlayerProxies.Blocks.betterBeacon) ||
				worldObj.getBlock(xCoord, yCoord, zCoord + 1).equals(PlayerProxies.Blocks.betterBeacon);
	}

	private void validateStructure() {
		level = 0;

		if (!isNearBeacon() && this.worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord)) {
			totalNegative = totalPositive = totalBlocks = 0;

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

			if (worldObj.isRemote) {
				//Color colorNeg = nf.fr.ephys.playerproxies.helpers.MathHelper.gradient(Color.white, Color.red, negativity());
				//Color colorPos = nf.fr.ephys.playerproxies.helpers.MathHelper.gradient(Color.white, Color.green, positivity());

				beaconColor = nf.fr.ephys.playerproxies.helpers.MathHelper.gradient(Color.white, Color.red, negativity());

				//beaconColor = nf.fr.ephys.playerproxies.helpers.MathHelper.gradient(colorNeg, colorPos, 0.5F);

				if (hasDragonEgg)
					beaconColor = nf.fr.ephys.playerproxies.helpers.MathHelper.gradient(beaconColor, Color.magenta, 0.5F);
			}
		}
	}

	public static boolean isBlockNegative(Block block) {
		return block.equals(Blocks.coal_block);
	}

	//public static boolean isBlockPositive(Block block) {
	//	return block.equals(Blocks.emerald_block) || block.equals(Blocks.diamond_block);
	//}

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
				else
					totalPositive++;
				//if (isBlockPositive(block))

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

		List<Integer> effects = BeaconEffectsRegistry.getEffects(containedItems, level);

		if (effects.size() != 0) {
			double range = (this.level * 15) + 10;

			AxisAlignedBB area = AxisAlignedBB.getBoundingBox(this.xCoord + 1, this.yCoord + 1, this.zCoord + 1, this.xCoord, this.yCoord - level, this.zCoord).expand(range, range, range);
			List players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, area);

			for (int effect : effects) {
				if (shouldSkipEffect(effect)) continue;

				for (Object player : players) {
					if (Potion.potionTypes[effect].isInstant() && Math.random() < 0.1)
						((EntityPlayer) player).addPotionEffect(new PotionEffect(effect, 1, tier - 1, true));
					else
						((EntityPlayer) player).addPotionEffect(new PotionEffect(effect, 250 * tier, tier - 1, true));
				}
			}
		}
	}

//	public float positivity() {
//		return (float) totalPositive / totalBlocks;
//	}

	public float negativity() {
		return (float) totalNegative / totalBlocks;
	}

	private boolean shouldSkipEffect(int effectID) {
		//if (Potion.potionTypes[effectID].isBadEffect())

		// bad potion effects are skipped if the beacon is not negative enough
		if (badPotionEffects[effectID])
			return Math.random() > negativity();

		// good potion effects are skipped if the beacon is too negative
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
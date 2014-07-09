package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.AxisAlignedBB;
import nf.fr.ephys.playerproxies.common.registry.BeaconEffectsRegistry;

import java.util.List;

public class TileEntityBeaconTierII extends TileEntityBeacon {
	private int level = 0;
	private boolean isTierTwo = false;
	private int effectTypes = BeaconEffectsRegistry.TYPE_NEGATIVE + BeaconEffectsRegistry.TYPE_POSITIVE;

	public static final int MAX_LEVELS = 8;
	public static final int TIERII_LEVEL = 4;
	public static final int MAX_ITEMS = 4;

	private Item[] containedItems = new Item[MAX_ITEMS];

	private void validateStructure() {
		level = 0;

		isTierTwo = this.worldObj.getBlock(xCoord, yCoord + 1, zCoord).equals(Blocks.dragon_egg);

		if (isTierTwo || this.worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord)) {
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
				if (!this.worldObj.getBlock(this.xCoord + x, this.yCoord - level, this.zCoord + z).isBeaconBase(worldObj, this.xCoord + x, this.yCoord - level, this.zCoord + z, xCoord, yCoord, zCoord))
					return false;
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

		int[] effects = BeaconEffectsRegistry.getEffects(containedItems, level, effectTypes);

		if (effects.length != 0) {
			double range = (this.level * 15) + 10;

			AxisAlignedBB area = AxisAlignedBB.getBoundingBox(this.xCoord + 1, this.yCoord + 1, this.zCoord + 1, this.xCoord, this.yCoord - level, this.zCoord).expand(range, range, range);
			List players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, area);

			for (int effect : effects) {
				for (Object player : players) {
					((EntityPlayer) player).addPotionEffect(new PotionEffect(effect, 180, isTierTwo ? 0 : 1, true));
				}
			}
		}
	}

	@Override
	public void updateEntity() {
		if (this.worldObj.getTotalWorldTime() % 180L == 0L)
		{
			validateStructure();
			addEffects();
		}
	}
}

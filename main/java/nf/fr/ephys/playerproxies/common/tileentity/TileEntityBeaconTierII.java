package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.AxisAlignedBB;

import java.util.Iterator;
import java.util.List;

public class TileEntityBeaconTierII extends TileEntityBeacon {
	private void func_146000_x()
	{
		if ((this.field_146015_k) && (this.levels > 0) && (!this.worldObj.isRemote) && (this.primaryEffect > 0))
		{
			double d0 = this.levels * 10 + 10;
			byte b0 = 0;
			if ((this.levels >= 4) && (this.primaryEffect == this.secondaryEffect)) {
				b0 = 1;
			}
			AxisAlignedBB axisalignedbb = AxisAlignedBB.getAABBPool().getAABB(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1).expand(d0, d0, d0);
			axisalignedbb.maxY = this.worldObj.getHeight();
			List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
			Iterator iterator = list.iterator();
			while (iterator.hasNext())
			{
				EntityPlayer entityplayer = (EntityPlayer)iterator.next();
				entityplayer.addPotionEffect(new PotionEffect(this.primaryEffect, 180, b0, true));
			}
			if ((this.levels >= 4) && (this.primaryEffect != this.secondaryEffect) && (this.secondaryEffect > 0))
			{
				iterator = list.iterator();
				while (iterator.hasNext())
				{
					EntityPlayer entityplayer = (EntityPlayer)iterator.next();
					entityplayer.addPotionEffect(new PotionEffect(this.secondaryEffect, 180, 0, true));
				}
			}
		}
	}

	private void func_146003_y()
	{
		int i = this.levels;
		if (!this.worldObj.canBlockSeeTheSky(this.xCoord, this.yCoord + 1, this.zCoord))
		{
			this.field_146015_k = false;
			this.levels = 0;
		}
		else
		{
			this.field_146015_k = true;
			this.levels = 0;
			for (int j = 1; j <= 4; this.levels = (j++))
			{
				int k = this.yCoord - j;
				if (k < 0) {
					break;
				}
				boolean flag = true;
				for (int l = this.xCoord - j; (l <= this.xCoord + j) && (flag); l++) {
					for (int i1 = this.zCoord - j; i1 <= this.zCoord + j; i1++)
					{
						Block block = this.worldObj.getBlock(l, k, i1);
						if (!block.isBeaconBase(this.worldObj, l, k, i1, this.xCoord, this.yCoord, this.zCoord))
						{
							flag = false;
							break;
						}
					}
				}
				if (!flag) {
					break;
				}
			}
			if (this.levels == 0) {
				this.field_146015_k = false;
			}
		}
		if ((!this.worldObj.isRemote) && (this.levels == 4) && (i < this.levels))
		{
			Iterator iterator = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord - 4, this.zCoord).expand(10.0D, 5.0D, 10.0D)).iterator();
			while (iterator.hasNext())
			{
				EntityPlayer entityplayer = (EntityPlayer)iterator.next();
				entityplayer.triggerAchievement(AchievementList.field_150965_K);
			}
		}
	}
}

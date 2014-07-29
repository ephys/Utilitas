package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import nf.fr.ephys.cookiecore.helpers.ChatHelper;
import nf.fr.ephys.cookiecore.helpers.MathHelper;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

import java.util.List;
import java.util.Random;

public class BlockEnderDragonSpawner extends Block {
	public static boolean enabled = false;

	public static void register() {
		if (!enabled) return;

		PlayerProxies.Blocks.dragonSpawner = new BlockEnderDragonSpawner(Blocks.mob_spawner.getMaterial());
		PlayerProxies.Blocks.dragonSpawner.setBlockName("PP_DragonSpawner")
				.setBlockTextureName("mob_spawner")
				.setHardness(-1F)
				.setStepSound(soundTypeMetal)
				.setTickRandomly(true);

		GameRegistry.registerBlock(PlayerProxies.Blocks.dragonSpawner, PlayerProxies.Blocks.dragonSpawner.getUnlocalizedName());
	}

	public BlockEnderDragonSpawner(Material material) {
		super(material);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int tickRate(World world) {
		return 100;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.provider.dimensionId != 1) {
			world.setBlockToAir(x, y, z);
			return;
		}

		List players = world.playerEntities;

		if (players.size() == 0) return;

		int stage = world.getBlockMetadata(x, y, z);

		if (stage <= 7) {
			if (stage != 7) {
				for (Object o : players) {
					//if (o instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) o;

					if (player.capabilities.isCreativeMode)
						continue;

					int nbMobs = net.minecraft.util.MathHelper.getRandomIntegerInRange(rand, 5, 5 * stage);
					for (int i = 0; i < nbMobs; i++) {
						EntityCreature mob = getMobForStage(stage, world, rand);

						mob.setTarget(player);
						mob.setAttackTarget(player);

						mob.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(50D);

						double posX = player.posX - 32 + rand.nextInt(64);
						double posZ = player.posZ - 32 + rand.nextInt(64);

						int posY = world.getTopSolidOrLiquidBlock((int) posX, (int) posZ) + 1;

						mob.setPosition(posX, posY, posZ);

						world.spawnEntityInWorld(mob);

						mob.spawnExplosionParticle();

						mob.playSound("mob.endermen.portal", 1, 1);
					}
					//}
				}
			} else {
				for (Object o : world.getLoadedEntityList()) {
					if (o instanceof EntityEnderman) {
						EntityEnderman mob = (EntityEnderman) o;

						mob.addPotionEffect(new PotionEffect(Potion.moveSpeed.getId(), Integer.MAX_VALUE, 1 + 2 + 3));
						mob.addPotionEffect(new PotionEffect(Potion.harm.getId(), Integer.MAX_VALUE, 1));

						mob.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(50D);

						EntityLiving target = (EntityLiving) MathHelper.getRandom(world.getLoadedEntityList());
						mob.setAttackTarget(target);
						mob.setTarget(target);
					}
				}
			}

			stage++;

			for (Object o : players) {
				if (o instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) o;

					ChatHelper.sendChatMessage(player, "The dragon is approaching (" + stage + " / 8)");
				}
			}

			world.setBlockMetadataWithNotify(x, y, z, stage, 0);
			world.scheduleBlockUpdate(x, y, z, this, 600);
		} else {
			for (Object o : players) {
				if (o instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) o;

					ChatHelper.sendChatMessage(player, "The dragon has arrived");
				}
			}

			EntityDragon dragon = new EntityDragon(world);
			dragon.setPosition(x - 32 + rand.nextInt(64), 250, z - 32 + rand.nextInt(64));
			dragon.setAttackTarget((EntityPlayer) nf.fr.ephys.cookiecore.helpers.MathHelper.getRandom(players));

			world.spawnEntityInWorld(dragon);

			world.setBlockToAir(x, y, z);
		}
	}

	private EntityCreature getMobForStage(int stage, World world, Random rand) {
		switch (stage) {
			case 0:
				EntityZombie zombie = new EntityZombie(world);
				zombie.setChild(rand.nextFloat() < 0.2);

				return zombie;

			case 1:
				EntitySkeleton skeleton = new EntitySkeleton(world);
				skeleton.setSkeletonType(rand.nextInt(1));

				return skeleton;

			case 2:
				EntityCaveSpider spider = new EntityCaveSpider(world);
				spider.addPotionEffect(new PotionEffect(Potion.moveSpeed.getId(), Integer.MAX_VALUE, 4));

				return spider;

			case 3:
				EntitySilverfish silverfish = new EntitySilverfish(world);

				silverfish.addPotionEffect(new PotionEffect(Potion.moveSpeed.getId(), Integer.MAX_VALUE, 4));
				silverfish.addPotionEffect(new PotionEffect(Potion.harm.getId(), Integer.MAX_VALUE, 2));
				silverfish.addPotionEffect(new PotionEffect(Potion.resistance.getId(), Integer.MAX_VALUE, 2));

				return silverfish;

			case 4:
				if (rand.nextBoolean())
					return new EntityWitch(world);

				EntityWolf wolf = new EntityWolf(world);
				wolf.setAngry(true);

				return wolf;

			case 5:
				return rand.nextBoolean() ? new EntitySilverfish(world) : new EntityBlaze(world);

			case 6:
				EntitySnowman golem = new EntitySnowman(world);
				golem.addPotionEffect(new PotionEffect(Potion.harm.getId(), Integer.MAX_VALUE, 4));

				return golem;
		}

		return null;
	}
}

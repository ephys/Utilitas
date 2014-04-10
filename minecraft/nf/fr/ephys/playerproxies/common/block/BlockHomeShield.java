package nf.fr.ephys.playerproxies.common.block;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Upgrade to the Twilight Forest stronghold shield
 * 
 * @author ephys
 */
public class BlockHomeShield extends Block {
	public static boolean requiresTwilightForest = false;
	public static int BLOCK_ID = 811;

	private Icon accessible;
	private Icon unaccessible;
	private Icon unbreakable;

	/*
	 * Metadata 
	 * 0 - 5 breakable 
	 * 6 - 11 unbreakable
	 */
	public BlockHomeShield(int id, Material material) {
		super(id, material);

		setResistance(2000.0F);
		setBlockUnbreakable();
		setStepSound(Block.soundMetalFootstep);
		setCreativeTab(PlayerProxies.creativeTab);
	}
	
	@Override
	public boolean canEntityDestroy(World world, int x, int y, int z, Entity entity) {
		return entity instanceof EntityPlayer;
	}

	public static void register() {
		requiresTwilightForest = true;
		if (requiresTwilightForest) {
			if (!Loader.isModLoaded("TwilightForest")) {
				PlayerProxies.getLogger().info("BlockHomeShield set to require mod Twilight Forest. TF not found, disabling block.");
				return;
			}
			
			int tfShieldID;
			PlayerProxies.getLogger().info("Twilight Forest found. PP will try to overwrite the TF shield block...");
			try {
				Class<?> tfBlocks = Class.forName("twilightforest.block.TFBlocks");
				Block tfShield = (Block) ReflectionHelper.findField(tfBlocks, "shield").get(null);
				
				tfShieldID = tfShield.blockID;
			} catch (Exception e) {
				PlayerProxies.getLogger().warning("Error while accessing the TF Shield block. Disabling block.");
				e.printStackTrace();
				
				if (PlayerProxies.DEV_MODE) {
					PlayerProxies.getLogger().severe("Exciting loading because an error occured in dev mode.");
					System.exit(1);
				}

				return;
			}

			Block.blocksList[tfShieldID] = null;
			PlayerProxies.blockHomeShield = new BlockHomeShield(tfShieldID, Material.rock);
			//Block.blocksList[tfShieldID] = PlayerProxies.blockHomeShield;

			Item.itemsList[tfShieldID] = null;
			ItemBlock shieldItem = new ItemBlock(tfShieldID - 256);
			//Item.itemsList[tfShieldID] = shieldItem;
		} else {
			PlayerProxies.blockHomeShield = new BlockHomeShield(BLOCK_ID, Material.rock);
			GameRegistry.registerBlock(PlayerProxies.blockHomeShield, "PP_HomeShield");
		}
		
		PlayerProxies.blockHomeShield.setUnlocalizedName("PP_HomeShield");
		LanguageRegistry.instance().addName(PlayerProxies.blockHomeShield, requiresTwilightForest ? "Stronghold Shield" : "Shield");
	}
	
	public static void registerCraft() {
		if (requiresTwilightForest) return;
	}

	public Icon getIcon(int side, int metadata) {
		if (isUnbreakable(metadata)) {
			if (side == metadata - 6)
				return unaccessible;
		} else if(side == metadata) 
			return accessible;

		return unbreakable;
	}

	public int getRenderBlockPass() {
		return 0;
	}

	public boolean isOpaqueCube() {
		return true;
	}

	public boolean renderAsNormalBlock() {
		return true;
	}

	public int quantityDropped(Random random) {
		return 1;
	}

	public int toggleBreakable(World world, int x, int y, int z, int metadata) {
		if (isUnbreakable(metadata))
			metadata -= 6;
		else
			metadata += 6;

		world.setBlockMetadataWithNotify(x, y, z, metadata, 1 + 2);

		return metadata;
	}

	public boolean isUnbreakable(World world, int x, int y, int z) {
		return isUnbreakable(world.getBlockMetadata(x, y, z));
	}

	public boolean isUnbreakable(int metadata) {
		return metadata >= 6;
	}

	public boolean isSideBreakable(int side, int metadata) {
		return isUnbreakable(metadata) ? side == (metadata - 6) : side == metadata;
	}

	public int damageDropped(int metadata) {
		return 0;
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack) {
		int metadata = BlockPistonBase.determineOrientation(world, x, y, z, entity);

		world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister register) {
		if (requiresTwilightForest) {
			accessible = register.registerIcon("TwilightForest:shield_inside");
			unbreakable = register.registerIcon("TwilightForest:shield_outside");
			unaccessible = register.registerIcon("ephys.pp:strongholdshield_unaccessible");
		} else {
			accessible = register.registerIcon("ephys.pp:homeshield_accessible");
			unbreakable = register.registerIcon("ephys.pp:homeshield_unbreakable");
			unaccessible = register.registerIcon("ephys.pp:homeshield_unaccessible");
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9) {
		int metadata = world.getBlockMetadata(x, y, z);

		if (isSideBreakable(side, metadata)) {
			if (!world.isRemote)
				toggleBreakable(world, x, y, z, metadata);

			return true;
		}

		return false;
	}

	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);

		if (!isUnbreakable(metadata)) {
			MovingObjectPosition mop = getPlayerPointVec(world, player, 6.0D);
	
			int facing = mop != null ? mop.sideHit : -1;

			if (facing == metadata) {
				return player.getCurrentPlayerStrVsBlock(Block.stone, false, 0) / 1.5F / 100.0F;
			}
		}

		return super.getPlayerRelativeBlockHardness(player, world, x, y, z);
	}

	private MovingObjectPosition getPlayerPointVec(World worldObj, EntityPlayer player, double range) {
		Vec3 position = worldObj.getWorldVec3Pool().getVecFromPool(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3 look = player.getLook(1.0F);
		Vec3 dest = position.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);

		return worldObj.clip(position, dest);
	}
}
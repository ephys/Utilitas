package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.client.registry.DragonColorRegistry;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

import java.util.List;

public class ItemDragonHoe extends ItemHoe {
	public static boolean enabled = true;

	public static void register() {
		if (!enabled) return;

		PlayerProxies.Items.dragonHoe = new ItemDragonHoe();
		PlayerProxies.Items.dragonHoe.setUnlocalizedName("PP_DragonHoe")
				.setMaxStackSize(1)
				.setCreativeTab(PlayerProxies.creativeTab);

		GameRegistry.registerItem(PlayerProxies.Items.dragonHoe, PlayerProxies.Items.dragonHoe.getUnlocalizedName());
	}

	public static void registerCraft() {
		if (!enabled) return;

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Items.dragonHoe),
				"ii ", " s ", " s ",
				'i', new ItemStack(PlayerProxies.Items.dragonScaleIngot),
				's', new ItemStack(Items.stick));
	}

	public ItemDragonHoe() {
		super(PlayerProxies.Items.matDragonScale);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer player, List data, boolean p_77624_4_) {
		data.add(StatCollector.translateToLocal("pp_tooltip.dragon_hoe"));
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!player.canPlayerEdit(x, y, z, side, stack))
			return false;

		Block block = world.getBlock(x, y, z);

		if (side == 1 && (block == Blocks.grass || (block == Blocks.dirt && world.getBlockMetadata(x, y, z) != 1) || block == Blocks.mycelium || block == Blocks.farmland)) {
			Block.SoundType sound = Blocks.dirt.stepSound;

			world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F,
					sound.getStepResourcePath(),
					(sound.getVolume() + 1.0F) / 2.0F,
					sound.getPitch() * 0.8F);

			if (!world.isRemote) {
				world.setBlock(x, y, z, Blocks.dirt, 1, 3);

				stack.damageItem(1, player);
			}

			return true;
		}

		return false;
	}

	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return EnumRarity.rare;
	}

	private IIcon[] icons = new IIcon[2];

	@Override
	public void registerIcons(IIconRegister register) {
		icons[1] = register.registerIcon("ephys.pp:dragonHoe_scale");
		icons[0] = register.registerIcon("ephys.pp:dragonHoe_wand");
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass) {
		return icons[renderPass];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return pass == 1 ? DragonColorRegistry.getColor() : super.getColorFromItemStack(stack, pass);
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
}
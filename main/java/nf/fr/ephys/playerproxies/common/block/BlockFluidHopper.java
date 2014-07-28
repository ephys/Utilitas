package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.item.ItemBlockTooltipped;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityFluidHopper;
import nf.fr.ephys.playerproxies.helpers.BlockHelper;
import nf.fr.ephys.playerproxies.helpers.ChatHelper;
import nf.fr.ephys.playerproxies.helpers.InputHelper;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

import java.util.ArrayList;
import java.util.List;

public class BlockFluidHopper extends BlockHopper implements IToolTipped {
	public static boolean enabled = true;

	private IIcon textureTop;
	private IIcon textureSide;
	private IIcon textureInside;

	public static void register() {
		if (!enabled) return;

		PlayerProxies.Blocks.fluidHopper = new BlockFluidHopper();
		PlayerProxies.Blocks.fluidHopper.setBlockName("PP_FluidHopper")
				.setCreativeTab(PlayerProxies.creativeTab)
				.setHardness(3.0F)
				.setResistance(8.0F)
				.setStepSound(soundTypeWood);

		GameRegistry.registerBlock(PlayerProxies.Blocks.fluidHopper, ItemBlockTooltipped.class, PlayerProxies.Blocks.fluidHopper.getUnlocalizedName());

		GameRegistry.registerTileEntity(TileEntityFluidHopper.class, "PP_FluidHopper");
	}

	public static void registerCraft() {
		if (!enabled) return;

		GameRegistry.addRecipe(new ItemStack(PlayerProxies.Blocks.fluidHopper),
				"l l", "lhl", " l ",
				'l', new ItemStack(Items.dye, 1, 4),
				'h', Blocks.hopper);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityFluidHopper();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, entity, stack);

		TileEntityFluidHopper te = (TileEntityFluidHopper) world.getTileEntity(x, y, z);

		te.getFluidsFromStack(stack);
	}

	@Override
	public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_) {
		super.onBlockAdded(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
		this.updateRedstoneState(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;

		TileEntityFluidHopper te = (TileEntityFluidHopper) world.getTileEntity(x, y, z);

		if (te != null) {
			player.openGui(PlayerProxies.instance, PlayerProxies.GUI_FLUID_HOPPER, world, x, y, z);
		}

		return true;
	}

	@Override
	public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_) {
		this.updateRedstoneState(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_);
	}

	private void updateRedstoneState(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int direction = getDirectionFromMetadata(metadata);
		boolean isPowered = !world.isBlockIndirectlyGettingPowered(x, y, z);
		boolean wasPowered = func_149917_c(metadata);

		if (isPowered ^ wasPowered) {
			world.setBlockMetadataWithNotify(x, y, z, direction | (isPowered ? 0 : 8), 4 + 2);
		}
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
		TileEntityFluidHopper tile = (TileEntityFluidHopper) world.getTileEntity(x, y, z);

		if (tile != null) tile.onBlockUpdate(tileX, tileY, tileZ);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ItemStack stack = getCustomItemStack(world, x, y, z);

		if (stack == null) return new ArrayList<>(0);

		ArrayList<ItemStack> stacks = new ArrayList<>(1);
		stacks.add(stack);

		return stacks;
	}

	private ItemStack getCustomItemStack(World world, int x, int y, int z) {
		TileEntityFluidHopper te = (TileEntityFluidHopper) world.getTileEntity(x, y, z);

		if (te == null) return null;

		ItemStack hopper = new ItemStack(PlayerProxies.Blocks.fluidHopper);
		te.setFluidsToStack(hopper);

		return hopper;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return getCustomItemStack(world, x, y, z);
	}

	@Override
	public int getRenderType() {
		return 38;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata) {
		return side == 1 ? textureTop : textureSide;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		return ((TileEntityFluidHopper) world.getTileEntity(x, y, z)).getComparatorInput();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		textureSide = register.registerIcon("ephys.pp:fluidhopper_outside");
		textureTop = register.registerIcon("ephys.pp:fluidhopper_top");
		textureInside = register.registerIcon("ephys.pp:fluidhopper_inside");
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		if (player != null && !player.capabilities.isCreativeMode) {
			ItemStack stack = getCustomItemStack(world, x, y, z);
			dropBlockAsItem(world, x, y, z, stack);
		}

		return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
		BlockHelper.dropContents((IInventory) world.getTileEntity(x, y, z), world, x, y, z);
		world.removeTileEntity(x, y, z);
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getHopperIcon(String name) {
		return name.equals("hopper_outside") ? PlayerProxies.Blocks.fluidHopper.textureSide : (name.equals("hopper_inside") ? PlayerProxies.Blocks.fluidHopper.textureInside : null);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName() {
		return "ephys.pp:fluidhopper";
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> data, boolean debug) {
		data.add("Trivializing liquids even more !");

		if (!InputHelper.isShiftPressed())
			data.add(EnumChatFormatting.LIGHT_PURPLE + "Press shift to show contents");
		else {
			data.add(EnumChatFormatting.LIGHT_PURPLE + "Contains:");

			NBTTagCompound fluidStackNBTs = NBTHelper.getNBT(stack).getCompoundTag("fluidStacks");

			boolean color = true;
			for (int i = 0; i < TileEntityFluidHopper.MAX_STACK_SIZE; i++) {
				if (fluidStackNBTs.hasKey(Integer.toString(i))) {
					FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluidStackNBTs.getCompoundTag(Integer.toString(i)));

					data.add((color ? EnumChatFormatting.DARK_AQUA : EnumChatFormatting.AQUA) + ChatHelper.getDisplayName(fluid) + " (" + fluid.amount + "mb)");

					color = !color;
				}
			}
		}
	}
}
package nf.fr.ephys.playerproxies.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import nf.fr.ephys.playerproxies.client.registry.DragonColorRegistry;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

public class ItemDragonPickaxe extends ItemPickaxe {
	public static void register() {
		PlayerProxies.Items.dragonPickaxe = new ItemDragonPickaxe();
		PlayerProxies.Items.dragonPickaxe.setUnlocalizedName("PP_DragonPick")
				.setMaxStackSize(1)
				.setCreativeTab(PlayerProxies.creativeTab)
				.setHarvestLevel("pickaxe", 4);

		GameRegistry.registerItem(PlayerProxies.Items.dragonPickaxe, PlayerProxies.Items.dragonPickaxe.getUnlocalizedName());
	}

	public static void registerCraft() {

	}

	public ItemDragonPickaxe() {
		super(PlayerProxies.Items.matDragonScale);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return pass == 0 ? DragonColorRegistry.getColor() : super.getColorFromItemStack(stack, pass);
	}

	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return EnumRarity.uncommon;
	}

	@Override
	public boolean isItemTool(ItemStack par1ItemStack) {
		return true;
	}

	@Override
	public float func_150893_a(ItemStack stack, Block block) {
		//if (block.equals(Blocks.bedrock))
			return Float.MAX_VALUE;

		//return super.func_150893_a(stack, block);
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		//if (block.equals(Blocks.bedrock))
			return Float.MAX_VALUE;

		//return super.getDigSpeed(stack, block, meta);
	}

/*	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
		World world = player.worldObj;
		Material mat = world.getBlock(x, y, z).getMaterial();

		world.setBlock(x, y, z, Blocks.air);

		return false;
	}*/

	@Override
	public boolean func_150897_b(Block block) {
		return true; //(block.equals(Blocks.bedrock)) || super.func_150897_b(block);
	}

	private IIcon[] icons = new IIcon[2];

	@Override
	public void registerIcons(IIconRegister register) {
		icons[0] = register.registerIcon("ephys.pp:dragonPick_2");
		icons[1] = register.registerIcon("ephys.pp:dragonPick_1");
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass) {
		return icons[renderPass];
	}
}
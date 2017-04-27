package be.ephys.utilitas.common.block;

import be.ephys.utilitas.common.Utilitas;
import be.ephys.utilitas.common.tileentity.TileEntityInterface;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.BlockChest;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockShinyGlass extends BlockBreakable implements ITileEntityProvider {

  public static boolean interfaceEnabled = true;

  public static final int METADATA_GLASS = 0;
  public static final int METADATA_INTERFACE = 1;

  public static final IProperty<Integer> TYPE = PropertyInteger.create("type", METADATA_GLASS, METADATA_INTERFACE);

  protected BlockShinyGlass(Material material, boolean transparent) {
    super(material, transparent);

    this.isBlockContainer = true;

    this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, METADATA_GLASS));
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, TYPE);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(Item unknown, CreativeTabs tab, List<ItemStack> subItems) {
    subItems.add(new ItemStack(this, 1, METADATA_GLASS));

    if (interfaceEnabled) {
      subItems.add(new ItemStack(this, 1, METADATA_INTERFACE));
    }
  }

  public static void registerCraft() {
    GameRegistry.addRecipe(
      new ItemStack(Utilitas.Blocks.shinyGlass, 12, METADATA_GLASS),
      "ggg", "gdg", "ggg",
      'd', new ItemStack(Items.DIAMOND),
      'g', new ItemStack(Blocks.GLASS)
    );

    if (interfaceEnabled) {
      GameRegistry.addRecipe(
        new ItemStack(Utilitas.Blocks.shinyGlass, 1, METADATA_INTERFACE),
        "dld", "geg", "dgd",
        'd', new ItemStack(Items.DIAMOND),
        'l', new ItemStack(Utilitas.Items.linkFocus),
        'g', new ItemStack(Utilitas.Blocks.shinyGlass, METADATA_GLASS),
        'e', new ItemStack(Blocks.ENDER_CHEST)
      );
    }
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

    if (state.getValue(TYPE) != METADATA_INTERFACE) {
      return false;
    }

    if (world.isRemote) {
      return true;
    }

    TileEntityInterface tile = (TileEntityInterface) world.getTileEntity(pos);
    if (tile == null) {
      return true;
    }

    if (heldItem == null || heldItem.getItem() == Utilitas.Items.linkDevice) {
      tile.link(player);
    } else {
      tile.addUpgrade(heldItem, player);
    }

    return true;
  }

  @Override
  public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
    TileEntity te = world.getTileEntity(pos);

    if (te instanceof TileEntityInterface) {
      ((TileEntityInterface) te).onBlockUpdate();
    }

    super.onNeighborChange(world, pos, neighbor);
  }

  @Override
  public boolean hasTileEntity(IBlockState state) {
    return state.getValue(TYPE) == METADATA_INTERFACE;
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    switch (metadata) {
      case METADATA_INTERFACE:
        return new TileEntityInterface();
      default:
        return null;
    }
  }

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state) {
    super.breakBlock(world, pos, state);

    if (state.getValue(TYPE) != METADATA_INTERFACE) {
      return;
    }

    TileEntityInterface te = (TileEntityInterface) world.getTileEntity(pos);

    if (te == null || te.upgrades == null) {
      return;
    }

    for (int i = 0; i < te.upgrades.length; i++) {
      if (te.upgrades[i] != null) {
        InventoryHelper.dropItem(te.upgrades[i], world, x, y, z);
      }
    }
  }

  @Override
  public boolean onBlockEventReceived(World paramWorld, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super.onBlockEventReceived(paramWorld, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    TileEntity localTileEntity = paramWorld.getTileEntity(paramInt1, paramInt2, paramInt3);

    return localTileEntity != null && localTileEntity.receiveClientEvent(paramInt4, paramInt5);
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getRenderBlockPass() {
    return 1;
  }

  public static void register() {
    BlockShinyGlass instance = new BlockShinyGlass(Material.GLASS, false);
    Utilitas.Blocks.shinyGlass = instance;

    instance.setUnlocalizedName("shiny_glass");
    instance.setSoundType(SoundType.GLASS)
      .setLightLevel(1.0F)
      .setHardness(1.0F)
      .setCreativeTab(Utilitas.creativeTab);

    GameRegistry.register(instance);
//        GameRegistry.registerBlock(instance, MultitemBlock.class, instance.getUnlocalizedName());

    if (interfaceEnabled) {
      GameRegistry.registerTileEntity(TileEntityInterface.class, "universal_interface");
    }
  }
}

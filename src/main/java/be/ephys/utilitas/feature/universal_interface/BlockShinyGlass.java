package be.ephys.utilitas.feature.universal_interface;

import be.ephys.utilitas.api.IInterfaceUpgrade;
import be.ephys.utilitas.base.helpers.InventoryHelper;
import be.ephys.utilitas.base.helpers.ItemHelper;
import be.ephys.utilitas.feature.link_wand.FeatureLinkWand;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockShinyGlass extends BlockBreakable implements ITileEntityProvider {

    public static boolean interfaceEnabled = true;

    public static final int METADATA_GLASS = 0;
    public static final int METADATA_INTERFACE = 1;

    public static final IProperty<Integer> TYPE = PropertyInteger.create("type", METADATA_GLASS, METADATA_INTERFACE);

    protected BlockShinyGlass() {
        super(Material.GLASS, true);

        this.isBlockContainer = true;

        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, METADATA_GLASS));

        ItemHelper.name(this, "shiny_glass");

        this.setSoundType(SoundType.GLASS)
            .setLightLevel(1.0F)
            .setHardness(1.0F);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
        return this.getDefaultState().withProperty(TYPE, stack.getMetadata());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item unknown, CreativeTabs tab, List<ItemStack> subItems) {
        subItems.add(new ItemStack(this, 1, METADATA_GLASS));

        if (interfaceEnabled) {
            subItems.add(new ItemStack(this, 1, METADATA_INTERFACE));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, meta);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

        if (state.getValue(TYPE) != METADATA_INTERFACE) {
            return false;
        }

        if (!world.isRemote) {
            TileEntityInterface tile = (TileEntityInterface) world.getTileEntity(pos);
            if (tile == null) {
                return false;
            }

            if (heldItem != null && heldItem.getItem() instanceof IInterfaceUpgrade) {
                tile.addUpgrade(heldItem, player);
                return true;
            }
        }

        return false;
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
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        if (metadata == METADATA_INTERFACE) {
            return new TileEntityInterface();
        }


        return null;
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
                InventoryHelper.dropItem(te.upgrades[i], world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        super.eventReceived(state, world, pos, id, param);

        TileEntity localTileEntity = world.getTileEntity(pos);

        return localTileEntity != null && localTileEntity.receiveClientEvent(id, param);
    }
}

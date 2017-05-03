package be.ephys.utilitas.feature.entity_sensor;

import be.ephys.utilitas.Utilitas;
import be.ephys.utilitas.base.helpers.ItemHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockProximitySensor extends BlockContainer {

    public static final IProperty<Boolean> POWERED = BlockRedstoneComparator.POWERED;

    public BlockProximitySensor(Material material) {
        super(material);

        ItemHelper.name(this, "proximity_sensor");

        this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, false));

        this.setHardness(2F)
            .setResistance(500.0F)
            .setCreativeTab(Utilitas.CREATIVE_TAB);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWERED);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWERED) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(POWERED, meta == 1);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (heldItem != null) {
            return false;
        }

        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);

            if (te instanceof TileEntityProximitySensor) {
                ((TileEntityProximitySensor) te).updateRadius(side, player);
            }
        }

        return true;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.DOWN ? blockState.getWeakPower(blockAccess, pos, side) : 0;
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        TileEntity te = blockAccess.getTileEntity(pos);

        if (te instanceof TileEntityProximitySensor) {
            return Math.min(((TileEntityProximitySensor) te).getEntityCount(), 15);
        }

        return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityProximitySensor();
    }
}

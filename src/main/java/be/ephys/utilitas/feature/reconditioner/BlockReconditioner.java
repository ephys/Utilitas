package be.ephys.utilitas.feature.reconditioner;

import be.ephys.utilitas.base.helpers.ItemHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class BlockReconditioner extends BlockContainer {

    public static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);

    public BlockReconditioner() {
        super(Material.ROCK, MapColor.RED);

        this.setLightOpacity(0);

        ItemHelper.name(this, "reconditioner");
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityReconditioner();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

        TileEntityReconditioner te = (TileEntityReconditioner) world.getTileEntity(pos);

        if (te == null) {
            return false;
        }

        if (world.isRemote) {
            return true;
        }

        IItemHandler capability = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);

        if (heldItem == null) {
            ItemStack stack = capability.extractItem(TileEntityReconditioner.SLOT_REPAIRABLE_ITEM, 1, false);
            if (stack == null) {
                stack = capability.extractItem(TileEntityReconditioner.SLOT_ENCHANTED_BOOK, 1, false);
            }

            playerIn.setHeldItem(hand, stack);
        } else {
            ItemStack stack = capability.insertItem(TileEntityReconditioner.SLOT_REPAIRABLE_ITEM, heldItem, false);

            if (stack == heldItem) {
                stack = capability.insertItem(TileEntityReconditioner.SLOT_ENCHANTED_BOOK, heldItem, false);
            }

            playerIn.setHeldItem(hand, stack);
        }

        return true;
    }
}

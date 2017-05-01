//package be.ephys.utilitas.feature.fluid_diffuser;
//
//import net.minecraft.block.Block;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.world.IBlockAccess;
//import net.minecraftforge.fluids.Fluid;
//import net.minecraftforge.fluids.FluidStack;
//import be.ephys.utilitas.feature.fluid_diffuser.TileEntityPotionDiffuser;
//
//public class BlockFluidDiffuserRenderer implements ISimpleBlockRenderingHandler {
//    public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
//
//    @Override
//    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
//        RenderHelper.renderInventoryBlock(block, metadata, renderer);
//    }
//
//    @Override
//    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
//        renderer.renderStandardBlock(PlayerProxies.Blocks.fluidDiffuser, x, y, z);
//
//        TileEntity te = world.getTileEntity(x, y, z);
//
//        if (!(te instanceof TileEntityPotionDiffuser)) return false;
//
//        FluidStack fluidStack = ((TileEntityPotionDiffuser) te).getFluid();
//
//        if (fluidStack != null && fluidStack.getFluid() != null) {
//            Fluid fluid = fluidStack.getFluid();
//
//            int color = FluidColorRegistry.getColorFromFluid(fluid);
//
//            float r = (color >> 16 & 0xFF) / 255.0F;
//            float g = (color >> 8 & 0xFF) / 255.0F;
//            float b = (color & 0xFF) / 255.0F;
//
//            Tessellator tessellator = Tessellator.instance;
//
//            tessellator.setColorOpaque_F(r, g, b);
//
//            IIcon icon = PlayerProxies.Blocks.fluidDiffuser.getDynamicTextureSide();
//
//            renderer.renderFaceXNeg(block, x, y, z, icon);
//            renderer.renderFaceXPos(block, x, y, z, icon);
//            renderer.renderFaceZPos(block, x, y, z, icon);
//
//            tessellator.setColorOpaque_F(1F, 1F, 1F);
//
//            IIcon fluidIcon = RenderHelper.getFluidTexture(fluidStack);
//
//            if (fluidIcon != null) {
//                renderer.renderFaceYPos(block, x, y, z, fluidIcon);
//                renderer.renderFaceYPos(block, x, y, z, PlayerProxies.Blocks.fluidDiffuser.getDynamicTextureTop());
//            }
//        }
//
//        return false;
//    }
//
//    @Override
//    public boolean shouldRender3DInInventory(int modelId) {
//        return true;
//    }
//
//    @Override
//    public int getRenderId() {
//        return RENDER_ID;
//    }
//}

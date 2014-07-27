package nf.fr.ephys.playerproxies.client.renderer;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BlockBeaconTierIIRenderer implements ISimpleBlockRenderingHandler {
	public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;

		for (int k = 0; k < 3; k++) {
			if (k == 0) {
				renderer.setRenderBounds(0.125D, 0.0D, 0.125D, 0.875D, 0.1875D, 0.875D);
				renderer.setOverrideBlockTexture(renderer.getBlockIcon(Blocks.obsidian));
			} else if (k == 1) {
				renderer.setRenderBounds(0.1875D, 0.1875D, 0.1875D, 0.8125D, 0.875D, 0.8125D);
				renderer.setOverrideBlockTexture(renderer.getBlockIcon(Blocks.beacon));
			} else if (k == 2) {
				renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
				renderer.setOverrideBlockTexture(renderer.getBlockIcon(PlayerProxies.Blocks.baseShineyGlass));
			}
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
			tessellator.draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}

		renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		renderer.clearOverrideBlockTexture();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderBlocks) {
		float f = 0.1875F;
		renderBlocks.setOverrideBlockTexture(renderBlocks.getBlockIcon(PlayerProxies.Blocks.baseShineyGlass));

		renderBlocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		renderBlocks.renderStandardBlock(block, x, y, z);
		renderBlocks.renderAllFaces = true;
		renderBlocks.setOverrideBlockTexture(renderBlocks.getBlockIcon(Blocks.obsidian));
		renderBlocks.setRenderBounds(0.125D, 0.0062500000931322575D, 0.125D, 0.875D, f, 0.875D);
		renderBlocks.renderStandardBlock(block, x, y, z);
		renderBlocks.setOverrideBlockTexture(renderBlocks.getBlockIcon(Blocks.beacon));
		renderBlocks.setRenderBounds(0.1875D, f, 0.1875D, 0.8125D, 0.875D, 0.8125D);
		renderBlocks.renderStandardBlock(block, x, y, z);
		renderBlocks.renderAllFaces = false;
		renderBlocks.clearOverrideBlockTexture();

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int i) {
		return true;
	}

	@Override
	public int getRenderId() {
		return RENDER_ID;
	}
}

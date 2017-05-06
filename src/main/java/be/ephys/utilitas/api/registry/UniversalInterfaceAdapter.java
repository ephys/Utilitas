package be.ephys.utilitas.api.registry;

import be.ephys.utilitas.base.nbt_writer.INbtSerializable;
import be.ephys.utilitas.feature.universal_interface.TileEntityInterface;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public abstract class UniversalInterfaceAdapter<T> implements ICapabilityProvider, INbtSerializable {

    private final TileEntityInterface tileEntity;

    public UniversalInterfaceAdapter(TileEntityInterface tileEntity) {
        this.tileEntity = tileEntity;
    }

    public TileEntityInterface getInterface() {
        return tileEntity;
    }

    /**
     * This method renders the inside of the universal interface depending on it's link type
     *
     * @param tickCount the amount of times this method has been called
     */
    @SideOnly(Side.CLIENT)
    public abstract void renderInventory(long tickCount, double x, double y, double z, float tickTime);

    /**
     * Link this handler with another object
     *
     * @param link   The object to link
     * @param linker The player who tried linking
     * @return success
     */
    public abstract boolean setLink(T link, EntityPlayer linker);

    public abstract ITextComponent getName();

    public abstract void onBlockUpdate();

    public abstract void onTick(long tick);

    public abstract IInventory getInventory();

    /**
     * @return the linked object is right next to the coords passed as a parameter
     */
    public abstract boolean isNextTo(BlockPos pos);

    /**
     * @return the dimention id of the object, 0 if the object is null
     */
    public abstract int getDimension();

    protected boolean isRemote() {
        return tileEntity.getWorld().isRemote;
    }

    @SideOnly(Side.CLIENT)
    protected static void defaultRenderInventory(long tickCount) {
        renderBlock(Blocks.CHEST, tickCount);
    }

    @SideOnly(Side.CLIENT)
    protected static void renderBlock(Block block, long tickCount) {
        renderBlock(block.getDefaultState(), tickCount);
    }

    @SideOnly(Side.CLIENT)
    protected static void renderBlock(IBlockState block, long tickCount) {
        GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslated(-0.5, -0.5, 0.5);

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(block, 1.0F);
    }
}

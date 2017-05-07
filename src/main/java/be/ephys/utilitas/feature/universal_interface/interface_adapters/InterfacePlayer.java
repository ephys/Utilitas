package be.ephys.utilitas.feature.universal_interface.interface_adapters;

import be.ephys.utilitas.api.registry.UniversalInterfaceAdapter;
import be.ephys.utilitas.base.helpers.*;
import be.ephys.utilitas.feature.universal_interface.PlayerInventoryRegistry;
import be.ephys.utilitas.feature.universal_interface.TileEntityInterface;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class InterfacePlayer extends UniversalInterfaceAdapter<EntityPlayer> {

    private boolean isEnderChest = false;
    private GameProfile gameProfile = null;
    private EntityPlayer userEntity = null;

    @SideOnly(Side.CLIENT)
    private RenderHelper.SkinData skin;

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(long tickCount, double x, double y, double z, float tickTime) {

        if (isEnderChest) {
            renderBlock(Blocks.ENDER_CHEST, tickCount);
            return;
        }

        GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);

        GL11.glPushMatrix();
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glTranslatef(0.0F, 0.35F, 0.0F);
        GL11.glRotatef(180F, 1F, 0, 0);

        if (skin == null) {
            skin = RenderHelper.getPlayerSkin(gameProfile);
        }

        RenderHelper.renderPlayer(skin, 0.06F);

        GL11.glPopMatrix();
    }

    @Override
    public boolean setLink(EntityPlayer link, EntityPlayer linker) {

        if (EntityHelper.isFakePlayer(link)) {
            ChatHelper.sendChatMessage(link, "You cannot link to fake players.");
            return false;
        }

        if (!link.getGameProfile().getId().equals(linker.getGameProfile().getId())) {
            ChatHelper.sendChatMessage(link, "You cannot link to another another player's inventory.");
            return false;
        }

        this.userEntity = link;
        this.gameProfile = link.getGameProfile();

        return true;
    }



    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("profile", NBTUtil.writeGameProfile(new NBTTagCompound(), this.gameProfile));
        nbt.setBoolean("is_ender_chest", isEnderChest);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.gameProfile = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("profile"));
        isEnderChest = NBTHelper.getBoolean(nbt, "is_ender_chest", false);
    }

    @Override
    public void onLoad() {
        if (getInterface().hasWorldObj()) {
            searchPlayer();

            onBlockUpdate();
        }
    }

    @Override
    public void onBlockUpdate() {
        TileEntityInterface interfaceEntity = this.getInterface();

        if (!interfaceEntity.hasWorldObj()) {
            return;
        }

        BlockPos pos = interfaceEntity.getPos();
        if (pos == null || pos.getY() >= 255) {
            return;
        }

        Block topBlock = interfaceEntity.getWorld().getBlockState(pos.up()).getBlock();

        OreDictionary.getOres("chestEnder");
        boolean underEnderChest = topBlock.equals(Blocks.ENDER_CHEST);

        if (underEnderChest ^ this.isEnderChest) {
            this.isEnderChest = underEnderChest;

            WorldHelper.markTileForUpdate(interfaceEntity);
        }
    }

    @Override
    public void onTick(long tick) {

        if ((userEntity == null || userEntity.isDead) && tick % 40 == 0) {
            searchPlayer();
        }
    }

    @SuppressWarnings({"MethodCallSideOnly", "VariableUseSideOnly"})
    private void searchPlayer() {
        if (!this.isRemote()) {
            userEntity = EntityHelper.getPlayerByUuid(gameProfile.getId());

            if (userEntity == null) {
                userEntity = PlayerInventoryRegistry.getFakePlayer(gameProfile.getId());
            }
        }
    }

    @Override
    public IInventory getInventory() {
        if (userEntity == null || userEntity.isDead) return null;

        return isEnderChest ? userEntity.getInventoryEnderChest() : userEntity.inventory;
    }

    @Override
    public boolean isNextTo(BlockPos pos) {
        return isEnderChest;
    }

    @Override
    public int getDimension() {
        if (isEnderChest) {
            // ender chests require a cross-dim upgrade unless they are in the end.
            return 1;
        }

        if (userEntity == null) {
            return 0;
        }

        return userEntity.getEntityWorld().provider.getDimension();
    }

    @Override
    public ITextComponent getName() {
        return userEntity == null ? new TextComponentString(gameProfile.getName()) : userEntity.getDisplayName();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
    }
}

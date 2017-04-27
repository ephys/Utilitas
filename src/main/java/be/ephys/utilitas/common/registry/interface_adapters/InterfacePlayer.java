package be.ephys.utilitas.common.registry.interface_adapters;

import be.ephys.utilitas.common.registry.PlayerInventoryRegistry;
import be.ephys.utilitas.common.tileentity.TileEntityInterface;
import be.ephys.utilitas.common.util.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.UUID;

public class InterfacePlayer extends UniversalInterfaceAdapter {

    private boolean isEnderChest = false;
    private String userName = null;
    private UUID userUuid = null;
    private EntityPlayer userEntity = null;

    public InterfacePlayer(TileEntityInterface tileEntity) {
        super(tileEntity);
    }

    @SideOnly(Side.CLIENT)
    private ResourceLocation skin;

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
        GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);

        if (isEnderChest) {
            renderBlock(Blocks.ENDER_CHEST);
            return;
        }

        GL11.glPushMatrix();
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glTranslatef(0.0F, 0.35F, 0.0F);
        GL11.glRotatef(180F, 1F, 0, 0);

        RenderHelper.renderSimpleBiped(skin, 0.06F);

        GL11.glPopMatrix();
    }

    @Override
    public boolean setLink(Object link, EntityPlayer linker) {
        if (!(link instanceof EntityPlayer)) {
            return false;
        }

        EntityPlayer player = ((EntityPlayer) link);

        if (EntityHelper.isFakePlayer(player)) {
            ChatHelper.sendChatMessage(player, "You cannot link to fake players.");
            return false;
        }

        if (!player.getGameProfile().getId().equals(linker.getGameProfile().getId())) {
            ChatHelper.sendChatMessage(player, "You cannot link to another another player's inventory.");
            return false;
        }

        this.userEntity = player;
        this.userName = player.getGameProfile().getName();
        this.userUuid = player.getGameProfile().getId();

        onBlockUpdate();

        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("user_name", userName);
        nbt.setTag("user_uuid", NBTUtil.createUUIDTag(userUuid));
        nbt.setBoolean("is_ender_chest", isEnderChest);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        userUuid = NBTHelper.getUuid(nbt, "user_uuid", null);
        userName = NBTHelper.getString(nbt, "user_name");
        isEnderChest = NBTHelper.getBoolean(nbt, "is_ender_chest", false);

        searchPlayer();
    }

    @Override
    public void onBlockUpdate() {
        TileEntityInterface interfaceEntity = this.getInterface();

        BlockPos pos = interfaceEntity.getPos();
        if (pos.getY() >= 255) {
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
    public void onTick(int tick) {
        if (isRemote()) {
            return;
        }

        if ((userEntity == null || userEntity.isDead) && tick % 40 == 0) {
            searchPlayer();
        }
    }

    @SuppressWarnings({"MethodCallSideOnly", "VariableUseSideOnly"})
    private void searchPlayer() {
        if (!this.isRemote()) {
            userEntity = EntityHelper.getPlayerByUuid(userUuid);

            if (userEntity == null) {
                userEntity = PlayerInventoryRegistry.getFakePlayer(userUuid);
            }
        } else {
            skin = net.minecraft.client.entity.AbstractClientPlayer.getLocationSkin(userName);
            net.minecraft.client.entity.AbstractClientPlayer.getDownloadImageSkin(skin, userName);
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
        return userEntity == null ? new TextComponentString(userName) : userEntity.getDisplayName();
    }

    @Override
    public void validate() {
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

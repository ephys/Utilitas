package nf.fr.ephys.playerproxies.common.registry.uniterface;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.cookiecore.helpers.ChatHelper;
import nf.fr.ephys.cookiecore.helpers.EntityHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;
import nf.fr.ephys.cookiecore.helpers.RenderHelper;
import nf.fr.ephys.playerproxies.client.renderer.TileEntityInterfaceRenderer;
import nf.fr.ephys.playerproxies.common.registry.PlayerInventoryRegistry;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public class InterfacePlayer extends UniversalInterface {
	private boolean isEnderChest = false;

	private String userName = null;
	private UUID userUUID = null;
	private EntityPlayer userEntity = null;

	public InterfacePlayer(TileEntityInterface tileEntity) {
		super(tileEntity);
	}

	@SideOnly(Side.CLIENT)
	private net.minecraft.util.ResourceLocation skin;

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
		GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);

		if (isEnderChest) {
			TileEntityInterfaceRenderer.renderBlocksInstance.renderBlockAsItem(Blocks.ender_chest, 0, 1.0F);
		} else {
			GL11.glPushMatrix();
				GL11.glColor3f(1.0F, 1.0F, 1.0F);
				GL11.glTranslatef(0.0F, 0.35F, 0.0F);
				GL11.glRotatef(180F, 1F, 0, 0);

				RenderHelper.renderSimpleBiped(skin, 0.06F);
			GL11.glPopMatrix();
		}
	}

	@Override
	public boolean setLink(Object link, EntityPlayer linker) {
		if (!(link instanceof EntityPlayer))
			return false;

		EntityPlayer player = ((EntityPlayer) link);

		if (!player.getGameProfile().getId().equals(linker.getGameProfile().getId())) {
			ChatHelper.sendChatMessage(player, "You cannot link to another another player's inventory");

			return false;
		}

		this.userEntity = player;
		this.userName = player.getGameProfile().getName();
		this.userUUID = player.getGameProfile().getId();

		onBlockUpdate();

		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("userName", userName);
		NBTHelper.setUUID(nbt, "userUUID", userUUID);
		nbt.setBoolean("enderChest", isEnderChest);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		userUUID = NBTHelper.getUUID(nbt, "userUUID", null);
		userName = NBTHelper.getString(nbt, "userName");

		isEnderChest = NBTHelper.getBoolean(nbt, "enderChest", false);

		searchPlayer();
	}

	@Override
	public void onBlockUpdate() {
		TileEntityInterface tileEntity = this.getTileEntity();

		Block topBlock = tileEntity.getWorldObj().getBlock(tileEntity.xCoord, tileEntity.yCoord + 1, tileEntity.zCoord);
		boolean b = topBlock.equals(Blocks.ender_chest); // || OreDictionary.getOreName(topBlock).equals(oreDict));

		if (b ^ isEnderChest) {
			isEnderChest = b;

			tileEntity.getWorldObj().markBlockForUpdate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
		}
	}

	@Override
	public void onTick(int tick) {
		if (getTileEntity().getWorldObj().isRemote) return;

		if ((userEntity == null || userEntity.isDead) && tick % 40 == 0) {
			searchPlayer();
		}
	}

	private void searchPlayer() {
		if (this.getTileEntity().getWorldObj() == null || !this.getTileEntity().getWorldObj().isRemote) {
			userEntity = EntityHelper.getPlayerByUUID(userUUID);

			if (userEntity == null) {
				userEntity = PlayerInventoryRegistry.getFakePlayer(userUUID);
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

		/*if (isEnderChest)
			return userEntity == null ? PlayerInventoryRegistry.getEnderchest(userName) : userEntity.getInventoryEnderChest();
		else
			return userEntity == null ? PlayerInventoryRegistry.getInventory(userName) : userEntity.inventory;*/
	}

	@Override
	public IFluidHandler getFluidHandler() {
		return null;
	}

	@Override
	public String getName() {
		return userEntity == null ? userName : userEntity.getDisplayName();
	}

	@Override
	public void validate() {}
}

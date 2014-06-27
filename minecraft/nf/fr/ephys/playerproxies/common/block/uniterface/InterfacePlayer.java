package nf.fr.ephys.playerproxies.common.block.uniterface;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;
import nf.fr.ephys.playerproxies.client.renderer.TileEntityInterfaceRenderer;
import nf.fr.ephys.playerproxies.common.core.PacketHandler;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.helpers.NBTHelper;

public class InterfacePlayer extends UniversalInterface {
	private boolean isEnderChest = false;
	
	private String userName = null;
	private EntityPlayer userEntity = null;
	
	private static String oreDict = OreDictionary.getOreName(Block.enderChest.blockID);
	
	static {
		oreDict = oreDict.equalsIgnoreCase("unknown") ? null : oreDict;
	}

	public InterfacePlayer(TileEntityInterface tileEntity) {
		super(tileEntity);
	}

	@SideOnly(Side.CLIENT)
	public static final ModelBiped MODEL_BIPED = new ModelBiped(0.0F);
	
	@SideOnly(Side.CLIENT)
	private ResourceLocation skin;

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventory(int tickCount, double x, double y, double z, float tickTime) {
		GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
		
		if (isEnderChest) {
			TileEntityInterfaceRenderer.renderBlocksInstance.renderBlockAsItem(Block.enderChest, 0, 1.0F);
		} else {
			RenderManager.instance.renderEngine.bindTexture(skin);

			GL11.glPushMatrix();
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
			GL11.glTranslatef(0.0F, 0.35F, 0.0F);
			tickTime = 0.06F;

			GL11.glRotatef(180F, 1F, 0, 0);
			MODEL_BIPED.bipedHead.render(tickTime);
			MODEL_BIPED.bipedBody.render(tickTime);
			MODEL_BIPED.bipedRightArm.render(tickTime);
			MODEL_BIPED.bipedLeftArm.render(tickTime);
			MODEL_BIPED.bipedRightLeg.render(tickTime);
			MODEL_BIPED.bipedLeftLeg.render(tickTime);
			MODEL_BIPED.bipedHeadwear.render(tickTime);
			GL11.glPopMatrix();
		}
	}

	@Override
	public boolean setLink(Object link, EntityPlayer linker) {
		if (!(link instanceof EntityPlayer))
			return false;

		EntityPlayer player = ((EntityPlayer) link);
		
		if (!player.username.equals(linker.username)) {
			linker.addChatMessage("You cannot link to another another player's inventory");
			return false;
		}

		this.userName = player.username;

		searchPlayer();
		onBlockUpdate(0);

		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("userName", userName);
		nbt.setBoolean("enderChest", isEnderChest);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		userName = NBTHelper.getString(nbt, "userName");
		isEnderChest = NBTHelper.getBoolean(nbt, "enderChest", false);
		
		searchPlayer();
	}

	@Override
	public void onBlockUpdate(int side) {
		TileEntityInterface tileEntity = this.getTileEntity();

		int topBlock = tileEntity.worldObj.getBlockId(tileEntity.xCoord, tileEntity.yCoord + 1, tileEntity.zCoord);
		boolean b = (topBlock == Block.enderChest.blockID || OreDictionary.getOreName(topBlock).equals(oreDict));

		if (b ^ isEnderChest) {
			isEnderChest = b;

			tileEntity.onInventoryChanged();
			tileEntity.worldObj.markBlockForUpdate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
		}
	}
	
	@Override
	public void onTick() {
		if (getTileEntity().worldObj.isRemote) return;

		if (userEntity == null || userEntity.isDead)
			searchPlayer();
	}
	
	private void searchPlayer() {
		if (this.getTileEntity().worldObj == null || !this.getTileEntity().worldObj.isRemote)
			userEntity = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(userName);
		else {
			skin = AbstractClientPlayer.getLocationSkin(userName);
			AbstractClientPlayer.getDownloadImageSkin(skin, userName);
		}
	}

	@Override
	public IInventory getInventory() {
		return userEntity == null ? null : isEnderChest ? userEntity.getInventoryEnderChest() : userEntity.inventory;
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

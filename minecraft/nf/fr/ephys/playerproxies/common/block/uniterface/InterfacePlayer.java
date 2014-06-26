package nf.fr.ephys.playerproxies.common.block.uniterface;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
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
		oreDict = oreDict.equals("unknown") ? null : oreDict;
	}

	public InterfacePlayer(TileEntityInterface tileEntity) {
		super(tileEntity);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventory(int tickCount, double par1, double par3, double par5, float par7) {
		if (userEntity == null) return;

		if (isEnderChest) {
			GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
			TileEntityInterfaceRenderer.renderBlocksInstance.renderBlockAsItem(Block.enderChest, 0, 1.0F);
		} else {
			World world = net.minecraft.client.Minecraft.getMinecraft().theWorld;
			EntityPlayer player = world.getPlayerEntityByName(userName);
			
			if(player != net.minecraft.client.Minecraft.getMinecraft().thePlayer) {
				//System.out.println(net.minecraft.client.Minecraft.getMinecraft().thePlayer.height);
				GL11.glTranslatef(0.0F, -1.5F, 0.0F);
			} else
				GL11.glRotatef(tickCount, 0.0F, 1.0F, 0.0F);

			net.minecraft.client.renderer.entity.RenderManager.instance.getEntityRenderObject(player).doRender(player, 0.0D, 0.5D, 0.0D, 1.0F, par7);
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
		else
			userEntity = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(userName);
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

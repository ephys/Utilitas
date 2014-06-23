package nf.fr.ephys.playerproxies.common.block.uniterface;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityZombie;
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

	public InterfacePlayer(TileEntityInterface tileEntity) {
		super(tileEntity);
	}

	@Override
	public void renderInventory(int tickCount, double par1, double par3, double par5, float par7) {
		// TODO
		/*World world = Minecraft.getMinecraft().theWorld;
		EntityPlayer player = world.getPlayerEntityByName(userName);

		if(player == null)
			RenderManager.instance.renderEntity(new EntityZombie(Minecraft.getMinecraft().theWorld), 1.0F);
		else {
			if(player != Minecraft.getMinecraft().thePlayer)
				GL11.glTranslatef(0.0F, -1.5F, 0.0F);
			else
				GL11.glRotatef(tickCount++, 0.0F, 1.0F, 0.0F);

			RenderManager.instance.getEntityRenderObject(player).doRender(player, 0.0D, 0.5D, 0.0D, 1.0F, par7);
		}*/
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
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		userName = NBTHelper.getString(nbt, "userName");
		
		searchPlayer();
		onBlockUpdate(0);
	}

	@Override
	public void onBlockUpdate(int side) {
		TileEntityInterface tileEntity = this.getTileEntity();
		
		boolean b = OreDictionary.getOreName(tileEntity.worldObj.getBlockId(tileEntity.xCoord, tileEntity.yCoord + 1, tileEntity.zCoord)).equals("enderChest");

		if (b ^ isEnderChest) {
			isEnderChest = b;

			tileEntity.onInventoryChanged();
		}
	}
	
	@Override
	public void onTick() {
		if (getTileEntity().worldObj.isRemote) return;

		if (userEntity == null || userEntity.isDead)
			searchPlayer();
	}
	
	private void searchPlayer() {
		userEntity = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(userName);
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
}

package nf.fr.ephys.playerproxies.common.entity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet204ClientInfo;
import net.minecraft.network.packet.Packet30Entity;
import net.minecraft.network.packet.Packet34EntityTeleport;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.common.IExtendedEntityProperties;
import nf.fr.ephys.playerproxies.common.core.NetServerHandlerFake;
import nf.fr.ephys.playerproxies.common.tileentity.TESpawnerLoader;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.ThreadDownloadImageData;

/*
 * TODO
 * - Cape
 * - Username (not visible through walls)
 */

public class Ghost extends EntityPlayerMP implements IEntityAdditionalSpawnData {
	private int offset = (int) (Math.random() * 50);

	private TESpawnerLoader linkedStabilizer = null;

	private int[] linkedStabilizerPos;

	public String username;

	@SideOnly(Side.CLIENT)
	private ThreadDownloadImageData downloadImageSkin;
	@SideOnly(Side.CLIENT)
	private ThreadDownloadImageData downloadImageCape;
	@SideOnly(Side.CLIENT)
	private ResourceLocation locationSkin;
	@SideOnly(Side.CLIENT)
	private ResourceLocation locationCape;

	public Ghost(World world) {
		this(world, "Ghost");
	}

	public Ghost(World world, String username) {
		super(FMLCommonHandler.instance().getMinecraftServerInstance(), world,
				username, new ItemInWorldManager(world));

		this.username = username;

		this.playerNetServerHandler = new NetServerHandlerFake(FMLCommonHandler
				.instance().getMinecraftServerInstance(), this);
	}
	
	@Override
	protected void entityInit() {
		tryAttach();
		super.entityInit();
	}

	@SideOnly(Side.CLIENT)
	private void downloadSkins() {
		this.locationSkin = AbstractClientPlayer.getLocationSkin(this.username);
		this.locationCape = AbstractClientPlayer.getLocationCape(this.username);
		this.downloadImageSkin = AbstractClientPlayer.getDownloadImageSkin(
				this.locationSkin, this.username);
		this.downloadImageCape = AbstractClientPlayer.getDownloadImageCape(
				this.locationCape, this.username);
	}

	public Ghost(World world, String username, double xCoord, double yCoord, double zCoord) {
		this(world, username);
		this.setPositionAndRotation2(xCoord, yCoord, zCoord, 0, 0, 0);
	}

	public Ghost(World world, String username, TESpawnerLoader linkedStabilizer) {
		this(world, username);
		this.setLinkedStabilizer(linkedStabilizer);
	}
	
	@Override
	protected void fall(float par1) {}

	public void setLinkedStabilizer(TESpawnerLoader stabilizer) {
		this.linkedStabilizer = stabilizer;

		if (stabilizer != null) {
			double posx = linkedStabilizer.xCoord + 0.5;
			double posy = linkedStabilizer.yCoord + 1.1;
			double posz = linkedStabilizer.zCoord + 0.5;
			
			this.setPositionAndRotation2(posx, posy, posz, 0, 0, 0);

			PacketDispatcher.sendPacketToAllAround(posx,
					posy, posz, 64,
					this.worldObj.provider.dimensionId, new Packet34EntityTeleport(this)
				);
		}
	}

	public TESpawnerLoader getLinkedStabilizer() {
		return linkedStabilizer;
	}

	@SideOnly(Side.CLIENT)
	public float getNextHoveringFloat() {
		float result = ((this.getAge() + offset) % 50) * 0.01F;
		if (result > 0.25F)
			return 0.5F - result;

		return result;
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getLocationSkin() {
		return this.locationSkin;
	}

	@Override
	public boolean isInvisible() {
		return false;
	}

	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(MathHelper.floor_double(this.posX),
				MathHelper.floor_double(this.posY + 0.5D),
				MathHelper.floor_double(this.posZ));
	}

	@Override
	public boolean isEntityInvulnerable() {
		return this.linkedStabilizer != null
				&& this.linkedStabilizer.isWorking();
	}

	@Override
	public void onDeath(DamageSource source) {
		if (this.linkedStabilizer != null)
			this.linkedStabilizer.detach();

		this.setDead();
	}

	public void sendChatToPlayer(String s) {
	}

	public boolean canCommandSenderUseCommand(int i, String s) {
		return false;
	}

	public void sendChatToPlayer(ChatMessageComponent chatmessagecomponent) {
	}

	public void addStat(StatBase par1StatBase, int par2) {
	}

	public void openGui(Object mod, int modGuiId, World world, int x, int y,
			int z) {
	}

	public boolean canAttackPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		if (this.linkedStabilizer != null) {
			nbt.setIntArray("ghost_stabilizer", new int[] {
					this.linkedStabilizer.xCoord, this.linkedStabilizer.yCoord,
					this.linkedStabilizer.zCoord });
		}

		if (this.username != null) {
			nbt.setString("username", this.username);
		}

		super.writeEntityToNBT(nbt);
	}

	@Override
	public String getEntityName() {
		return username;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("ghost_stabilizer")) {
			int[] stabLoc = nbt.getIntArray("ghost_stabilizer");
			this.linkedStabilizerPos = stabLoc;
		}

		if (nbt.hasKey("username")) {
			this.username = nbt.getString("username");
		}
	}
	
	@Override
	public boolean canBePushed() {
		return false;
	}
	
	@Override
	protected void collideWithEntity(Entity par1Entity) {}
	
	@Override
	protected void collideWithNearbyEntities() {}
	
	@Override
	public void setPositionAndRotation2(double x, double y, double z, float par7, float par8, int par9) {
        this.newPosX = x;
        this.newPosY = y;
        this.newPosZ = z;
        float halfWidth = this.width / 2.0F;
        
        this.boundingBox.setBounds(x - (double)halfWidth, y - (double)this.yOffset + (double)this.ySize, z - (double)halfWidth, x + (double)halfWidth, y - (double)this.yOffset + (double)this.ySize + (double)this.height, z + (double)halfWidth);
	}
	

	@Override
	public void onUpdate() {
		//super.onLivingUpdate();
		super.onUpdateEntity();

		this.entityAge++;
		
		/*if(this.hurtTime > 0)
			this.hurtTime--;

		if (this.hurtResistantTime > 0) {
			--this.hurtResistantTime;
		}*/

		if (linkedStabilizerPos != null) {
			TileEntity te = this.worldObj.getBlockTileEntity(
					linkedStabilizerPos[0], linkedStabilizerPos[1],
					linkedStabilizerPos[2]);

			if (te instanceof TESpawnerLoader) {
				((TESpawnerLoader) te).attach(this);
			}

			linkedStabilizerPos = null;
		}

		if (this.getAge() % 10 == 0) {
			if (!isEntityInvulnerable())
				this.attackEntityFrom(DamageSource.magic, 1);
			else if (this.getHealth() < this.getMaxHealth())
				this.heal(1);
		}
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeUTF(this.username);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		this.username = data.readUTF();

		downloadSkins();
	}

	private void tryAttach() {
		if(this.linkedStabilizer != null)
			return;
		
		TileEntity te = this.worldObj.getBlockTileEntity((int) posX,
				(int) posY, (int) posZ);

		if (te instanceof TESpawnerLoader) {
			((TESpawnerLoader) te).attach(this);
		}
	}
}

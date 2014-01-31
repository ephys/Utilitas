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
		super(FMLCommonHandler.instance().getMinecraftServerInstance(),
				world,
				username,
				new ItemInWorldManager(world)
		);
		
		this.username = username;

		this.playerNetServerHandler = new NetServerHandlerFake(FMLCommonHandler
				.instance().getMinecraftServerInstance(), this);
	}

	@SideOnly(Side.CLIENT)
	private void downloadSkins() {
        this.locationSkin = AbstractClientPlayer.getLocationSkin(this.username);
        this.locationCape = AbstractClientPlayer.getLocationCape(this.username);
        this.downloadImageSkin = AbstractClientPlayer.getDownloadImageSkin(this.locationSkin, this.username);
        this.downloadImageCape = AbstractClientPlayer.getDownloadImageCape(this.locationCape, this.username);
	}

	public Ghost(World world, String username, double xCoord, double yCoord, double zCoord) {
		this(world, username);
		this.setPosition(xCoord, yCoord, zCoord);
	}

	public Ghost(World world, String username, TESpawnerLoader linkedStabilizer) {
		this(world, username);
		this.setLinkedStabilizer(linkedStabilizer);
	}

	public void setLinkedStabilizer(TESpawnerLoader stabilizer) {
		this.linkedStabilizer = stabilizer;

		if(stabilizer != null) {
			this.setPosition(
				linkedStabilizer.xCoord + 0.5,
				linkedStabilizer.yCoord + 1, 
				linkedStabilizer.zCoord + 0.5
			);

           /* this.noClip = true;
            
            this.moveEntity(linkedStabilizer.xCoord, linkedStabilizer.yCoord, linkedStabilizer.zCoord);
		
            this.noClip = false; */
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
		return this.linkedStabilizer != null && this.linkedStabilizer.isWorking();
	}

	@Override
	public void onDeath(DamageSource source) {
		if (this.linkedStabilizer != null)
			this.linkedStabilizer.detach();

		this.setDead();
	}

	public void sendChatToPlayer(String s) {}
	public boolean canCommandSenderUseCommand(int i, String s) { return false; }
	public void sendChatToPlayer(ChatMessageComponent chatmessagecomponent) {}
	public void addStat(StatBase par1StatBase, int par2) {}
	public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {}
	public boolean canAttackPlayer(EntityPlayer player) { return false; }

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		if(this.linkedStabilizer != null) {
			nbt.setIntArray("ghost_stabilizer", new int[]{
				this.linkedStabilizer.xCoord, 
				this.linkedStabilizer.yCoord, 
				this.linkedStabilizer.zCoord
			});
		}
		
		if(this.username != null) {
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
		if(nbt.hasKey("ghost_stabilizer")) {
			int[] stabLoc = nbt.getIntArray("ghost_stabilizer");
			this.linkedStabilizerPos = stabLoc;
		}
		
		if(nbt.hasKey("username")) {
			this.username = nbt.getString("username");

			downloadSkins();
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		
		this.entityAge++;
		
		if (this.hurtResistantTime > 0) {
			--this.hurtResistantTime;
		}
		
		if(linkedStabilizerPos != null) {
			TileEntity te = this.worldObj.getBlockTileEntity(linkedStabilizerPos[0], linkedStabilizerPos[1], linkedStabilizerPos[2]);

			if(te instanceof TESpawnerLoader) {
				this.linkedStabilizer = (TESpawnerLoader) te;
			}
			
			linkedStabilizerPos = null;
		}

		if (this.getAge()%1000 == 0) {
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
		tryAttach();
	}

	private void tryAttach() {
		TileEntity te = this.worldObj.getBlockTileEntity((int)posX, (int)posY, (int)posZ);
		
		if(te instanceof TESpawnerLoader) {
			((TESpawnerLoader) te).attach(this);
		}
	}
}

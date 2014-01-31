package nf.fr.ephys.playerproxies.common.entity;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet204ClientInfo;
import net.minecraft.network.packet.Packet30Entity;
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
import nf.fr.ephys.playerproxies.common.core.NetServerHandlerFake;
import nf.fr.ephys.playerproxies.common.tileentity.TESpawnerLoader;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;

/*
 * TODO
 * Here is another nice problem :)
 * On the server side, everything is fine, we have a Ghost instance for each player.
 * On the client side, an EntityOtherPlayerMP is spawned. That is NOT fine because it means we can't hook the entity renderer to it without affecting every other player renders
 * 
 * btw: world.isRemote means it's the client
 * 
 * The solutions:
 * A) Spawn a local "ghost" Ghost instance, make it linked to the EntityOtherPlayerMP, make the EntityOtherPlayerMP invisible
 *    Then make the ghost update it's state depending on the real entity's.
 * 
 * B) Understand why we get an EntityOtherPlayerMP and correct it if at all possible. I have no idea how
 *    Though it's probably because we're an extension of EntityPlayerMP which means the packet sync will make the client side spawn an EntityOtherPlayerMP
 *    And I don't believe there is any way to change that. So the best bet would be solution A
 *    Also, we need to get the skin and we cannot get the skin without using AbstractClientPlayer, which we cannot cast from Ghost but can from EOPMP
 *    So solution B no, solution A yes
 *
 * Make Universal Interface ItemBlock transparent
 * make the Ghost more and more translucent depending on it's current health
 * 
 */

public class Ghost extends EntityPlayerMP {
	private int offset = (int) (Math.random() * 50);

	private TESpawnerLoader linkedStabilizer = null;

	private int[] linkedStabilizerPos;
	
	@SideOnly(Side.CLIENT)
	private EntityOtherPlayerMP fakePlayer = null; // Because the client side spawns an EntityOtherPlayerMP instead of what we want
	
	// used to recreate an entity onload, NOT for anything else
	public Ghost(World world) {
		super(FMLCommonHandler.instance().getMinecraftServerInstance(),
				world,
				"dummy", // can't know his name yet -_-
				new ItemInWorldManager(world)
		);

		//this.setInvisible(true);

		this.playerNetServerHandler = new NetServerHandlerFake(FMLCommonHandler
				.instance().getMinecraftServerInstance(), this);
		
		if(this.worldObj.isRemote)
			this.fakePlayer = (EntityOtherPlayerMP)this.worldObj.getClosestPlayerToEntity(this, 0.1);
	}

	public Ghost(World world, String username, double xCoord, double yCoord,
			double zCoord) {
		super(FMLCommonHandler.instance().getMinecraftServerInstance(), world,
				username, new ItemInWorldManager(world));

		this.setPosition(xCoord, yCoord, zCoord);
		
		this.playerNetServerHandler = new NetServerHandlerFake(FMLCommonHandler.instance().getMinecraftServerInstance(), this);
		
		//this.setInvisible(true);
		
		this.worldObj.spawnEntityInWorld(this);
		
		if(this.worldObj.isRemote)
			this.fakePlayer = (EntityOtherPlayerMP)this.worldObj.getClosestPlayerToEntity(this, 0.1);
	}

	public Ghost(World world, String username, TESpawnerLoader linkedStabilizer) {
		super(FMLCommonHandler.instance().getMinecraftServerInstance(), world,
				username, new ItemInWorldManager(world));

		this.setLinkedStabilizer(linkedStabilizer);
		
		this.playerNetServerHandler = new NetServerHandlerFake(FMLCommonHandler.instance().getMinecraftServerInstance(), this);

		//this.setInvisible(true);
		
		this.worldObj.spawnEntityInWorld(this);
		
		if(this.worldObj.isRemote)
			this.fakePlayer = (EntityOtherPlayerMP)this.worldObj.getClosestPlayerToEntity(this, 0.1);
	}

	public void setLinkedStabilizer(TESpawnerLoader stabilizer) {
		this.linkedStabilizer = stabilizer;

		if (stabilizer != null) {
			if(this.playerNetServerHandler != null)
				this.setPositionAndUpdate(linkedStabilizer.xCoord + 0.5,
					linkedStabilizer.yCoord + 1, linkedStabilizer.zCoord + 0.5);
			else
				this.setPosition(linkedStabilizer.xCoord + 0.5,
						linkedStabilizer.yCoord + 1, linkedStabilizer.zCoord + 0.5);
		}
	}

	public TESpawnerLoader getLinkedStabilizer() {
		return linkedStabilizer;
	}

	@SideOnly(Side.CLIENT)
	public float getNextHoveringFloat() {
		if(fakePlayer == null) return 0;
		
		float result = ((this.fakePlayer.getAge() + offset) % 50) * 0.01F;
		if (result > 0.25F)
			return 0.5F - result;

		return result;
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getLocationSkin() {
		if (fakePlayer instanceof AbstractClientPlayer)
			return ((AbstractClientPlayer) fakePlayer).getLocationSkin();

		return null;
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

	public void sendChatToPlayer(String s) {
	}

	public boolean canCommandSenderUseCommand(int i, String s) {
		return false;
	}

	@Override
	public void sendChatToPlayer(ChatMessageComponent chatmessagecomponent) {
	}

	@Override
	public void addStat(StatBase par1StatBase, int par2) {
	}

	@Override
	public void openGui(Object mod, int modGuiId, World world, int x, int y,
			int z) {
	}

	@Override
	public boolean canAttackPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		if(this.linkedStabilizer != null) {
			nbt.setIntArray("ghost_stabilizer", new int[]{
				this.linkedStabilizer.xCoord, 
				this.linkedStabilizer.yCoord, 
				this.linkedStabilizer.zCoord
			});
		}
		
		super.writeEntityToNBT(nbt);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if(nbt.hasKey("ghost_stabilizer")) {
			int[] stabLoc = nbt.getIntArray("ghost_stabilizer");
			this.linkedStabilizerPos = stabLoc;
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if(this.worldObj.isRemote) { // image syncing with fake player
			if(this.fakePlayer == null || this.fakePlayer.isDead) {
				this.setDead();
				return;
			}
			
			if(this.posX != this.fakePlayer.posX)
				this.posX = this.fakePlayer.posX;

			if(this.posY != this.fakePlayer.posY)
				this.posY = this.fakePlayer.posY;			

			if(this.posZ != this.fakePlayer.posZ)
				this.posZ = this.fakePlayer.posZ;
			
			return;
		}
		
		if (this.hurtResistantTime > 0) {
			--this.hurtResistantTime;
		}
		
		if(linkedStabilizerPos != null) {
			TileEntity te = this.worldObj.getBlockTileEntity(linkedStabilizerPos[0], linkedStabilizerPos[1], linkedStabilizerPos[2]);

			if(te instanceof TESpawnerLoader) {
				this.linkedStabilizer = (TESpawnerLoader) te;
				
				if(!this.linkedStabilizer.getOwner().equals(this.username)) {
					this.linkedStabilizer.recreate();
					this.setDead();
				}
			}
			
			linkedStabilizerPos = null;
		}

		if (this.getAge()%20 == 0) {
			if (!isEntityInvulnerable())
				this.attackEntityFrom(DamageSource.magic, 1);
			else if (this.getHealth() < this.getMaxHealth())
				this.heal(1);
		}
	}
}

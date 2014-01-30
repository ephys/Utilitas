package nf.fr.ephys.playerproxies.common.entity;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet204ClientInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
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

public class Ghost extends EntityPlayerMP {
	private EntityPlayer linkedPlayer = null;
	private int offset = (int) (Math.random()*50);
	
	private TESpawnerLoader linkedStabilizer = null;
	
	public Ghost(World world, String username, double xCoord, double yCoord, double zCoord) {
		super(FMLCommonHandler.instance().getMinecraftServerInstance(), world, username, new ItemInWorldManager(world));
		
		this.setPosition(xCoord, yCoord, zCoord);
		
		spawn();
	}
	
	public Ghost(World world, String username, TESpawnerLoader linkedStabilizer) {
		super(FMLCommonHandler.instance().getMinecraftServerInstance(), world, username, new ItemInWorldManager(world));
	
		this.setLinkedStabilizer(linkedStabilizer);

		spawn();
	}
	
	private void spawn() {
		this.playerNetServerHandler = new NetServerHandlerFake(FMLCommonHandler.instance().getMinecraftServerInstance(), this);
		
		setInvisible(true);
		
		this.worldObj.spawnEntityInWorld(this);
		
		this.linkedPlayer = this.worldObj.getClosestPlayerToEntity(this, 1f);
	}
	
	public void setLinkedStabilizer(TESpawnerLoader stabilizer) {
		this.linkedStabilizer = stabilizer;
		
		if(stabilizer != null) {
			this.setPosition(linkedStabilizer.xCoord + 0.5, linkedStabilizer.yCoord + 1, linkedStabilizer.zCoord + 0.5);
		}
	}
	
	public TESpawnerLoader getLinkedStabilizer() {
		return linkedStabilizer;
	}
	
	public float getNextHoveringFloat() {
		float result = ((this.linkedPlayer.getAge()+offset)%50)*0.01F;
		if(result > 0.25F)
			return 0.5F-result;

		return result;
	}
	
	public void setLinkedPlayed(EntityPlayer player) {
		this.linkedPlayer = player;
	}

	public EntityPlayer getLinkedPlayer() {
		return linkedPlayer;
	}
	
	public ResourceLocation getLocationSkin() {
		if(linkedPlayer instanceof AbstractClientPlayer)
			return ((AbstractClientPlayer) linkedPlayer).getLocationSkin();
		
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
		return false;
	}

	@Override
	public void onDeath(DamageSource source) {
		if(this.linkedStabilizer != null)
			this.linkedStabilizer.detach();
		
		if(this.linkedPlayer != null)
			this.linkedPlayer.setDead();

		this.setDead();
	}
	
    public void sendChatToPlayer(String s){}
    public boolean canCommandSenderUseCommand(int i, String s){ return false; }
    @Override
    public void sendChatToPlayer(ChatMessageComponent chatmessagecomponent){}
    @Override
    public void addStat(StatBase par1StatBase, int par2){}
    @Override
    public void openGui(Object mod, int modGuiId, World world, int x, int y, int z){}
    @Override
    public boolean canAttackPlayer(EntityPlayer player){ return false; }
    @Override
    public void updateClientInfo(Packet204ClientInfo pkt){ return; }
    
    @Override
    public void onUpdate() {
    	super.onUpdate();
    	
        if (this.hurtResistantTime > 0)
        {
            --this.hurtResistantTime;
        }

        if(linkedPlayer.isDead)
        	this.onDeath(null);
        
        if(Math.random() > 0.95) {
	        if(this.linkedStabilizer == null || !this.linkedStabilizer.isWorking())
	        	linkedPlayer.attackEntityFrom(DamageSource.magic, 1);
	        else
	        	linkedPlayer.heal(1);
        }
    }
}

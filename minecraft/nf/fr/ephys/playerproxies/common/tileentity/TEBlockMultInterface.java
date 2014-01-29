package nf.fr.ephys.playerproxies.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

public class TEBlockMultInterface extends TEBlockInterface {
	private String[] userName = null;
	private EntityPlayer[] userEntity = null;
	private TileEntity[] blockEntity = null;
	
	private IInventory linkedInventory = null;
	
	private int currentInventory = 0;
	private int tickCount = 0;
	
	private int playerCounts = 0;
	private int teCounts = 0;
	
	private static final int MAX_LINKS = 5;
	
	private static final int INVTYPE_PLAYER = 0;
	private static final int INVTYPE_TE = 1;
	
	public TEBlockMultInterface() {
		this.userName = new String[this.MAX_LINKS];
		this.userEntity = new EntityPlayer[this.MAX_LINKS];
		this.blockEntity = new TileEntity[this.MAX_LINKS];
	}
	
	public void updateEntity() {
        super.updateEntity();
        
        if(this.getLinkCount() == 0)
        	return;
        
        if(this.getLinkCount() != 1) {
            tickCount++;
            if(tickCount == 3) {
            	tickCount = 0;
            	
            	currentInventory = (currentInventory+1)%this.getLinkCount();
            }
        }
        
        if(!worldObj.isRemote) {
        	this.linkedInventory = getCurrentInventory();
        }
	 }
	
	private IInventory getCurrentInventory() {
		switch(this.getCurrentInventoryType()) {
		case INVTYPE_PLAYER:
			int curPlayer = this.getCurrentInventoryId();
			
            if(userEntity[curPlayer] == null || userEntity[curPlayer].isDead) {
            	userEntity[curPlayer] = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(userName[curPlayer]);            	
            	
            	return (userEntity[curPlayer] == null)?null:userEntity[curPlayer].inventory;
            } else {
            	return userEntity[curPlayer].inventory;
            }
            
		case INVTYPE_TE:
			int curTE = this.getCurrentInventoryId();
			
			if(this.blockEntity[curTE] == null || this.blockEntity[curTE].isInvalid()) {
				currentInventory = 0;
				this.removeTE(curTE);

				return null;
			}
			
			return (IInventory) this.blockEntity[curTE];
		}
		
		return null;
	}

	@Override
	public int getSizeInventory() {
		return (linkedInventory == null)?34:linkedInventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return (linkedInventory == null)?null:linkedInventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return (linkedInventory == null)?null:linkedInventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null; // ur never closed :|
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if(linkedInventory != null)
			linkedInventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return "ephys_pp.blockinterface";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return (linkedInventory == null)?64:linkedInventory.getInventoryStackLimit();
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
        if(linkedInventory != null)
        	linkedInventory.onInventoryChanged();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return (linkedInventory == null)?false:linkedInventory.isItemValidForSlot(i, itemstack);
	}
	
	private int getLinkCount() {
		return this.playerCounts + this.teCounts;
	}
	
	public int getCurrentInventoryType() {
		if(this.currentInventory < this.playerCounts)
			return INVTYPE_PLAYER;
		else
			return INVTYPE_TE;
	}
	
	private int getCurrentInventoryId() {
		if(this.currentInventory < this.playerCounts)
			return this.currentInventory;
		else
			return this.currentInventory-this.playerCounts;
	}
	
	private boolean isLinked(TileEntity te) {
		for(int i = 0; i < this.teCounts; i++) {
			if(this.blockEntity[i] == te)
				return true;
		}
		
		return false;
	}
	
	private void removeTE(int curTE) {
		for(int i = this.teCounts-1; i >= curTE; i--) {
			this.blockEntity[i] = this.blockEntity[i+1];
		}
		
		this.teCounts--;
	}
	
	private void removeLinkedInventory(TileEntity te, EntityPlayer player) {
		if(this.teCounts == 0)
			return;
		
		for(int i = this.teCounts-1; i > 0 && this.blockEntity[i] != te; i--) {
			this.blockEntity[i] = this.blockEntity[i+1];
		}
		
		this.teCounts--;
		
		if(te == this.linkedInventory)
			this.linkedInventory = null;
		
		player.addChatMessage(te.getBlockType().getLocalizedName()+" unlinked to the Universal Interface");
	}
	
	public void addLinkedInventory(TileEntity te, EntityPlayer player) {
		if(this.getLinkCount() >= this.MAX_LINKS) {
			player.addChatMessage("Cannot link you to this Universal Interface: Capacity overflow");
		}

		this.blockEntity[this.teCounts] = te;
		
		this.teCounts++;
		
		player.addChatMessage(te.getBlockType().getLocalizedName()+" linked to the Universal Interface");
	}
	
	private boolean isLinked(EntityPlayer player) {
		for(int i = 0; i < this.playerCounts; i++) {
			if(userName[i].equals(player.username))
				return true;
		}
		
		return false;
	}
	
	private void removeLinkedInventory(EntityPlayer player) {
		if(this.playerCounts == 0)
			return;
		
		for(int i = this.playerCounts-1; i > 0 && !this.userName[i].equals(player.username); i--) {
			this.userName[i] = this.userName[i+1];
			this.userEntity[i] = this.userEntity[i+1];
		}
		
		this.playerCounts--;
		
		if(player.inventory == this.linkedInventory)
			this.linkedInventory = null;
		
		player.addChatMessage("Your inventory has been unlinked to the Universal Interface");
	}
	
	public void addLinkedInventory(EntityPlayer player) {
		if(this.getLinkCount() >= this.MAX_LINKS) {
			player.addChatMessage("Cannot link you to this Universal Interface: Capacity overflow");
		}

		this.userName[this.playerCounts] = player.username;
		this.userEntity[this.playerCounts] = player;
		
		this.playerCounts++;
		
		player.addChatMessage("Your inventory is now linked to the Universal Interface");
	}

	public void toggleLinked(EntityPlayer player) {
		if(this.isLinked(player))
			this.removeLinkedInventory(player);
		else
			this.addLinkedInventory(player);
	}
	
	public void toggleLinked(TileEntity te, EntityPlayer player) {
		if(!(te instanceof IInventory))
			return;
		
		if(this.isLinked(te))
			this.removeLinkedInventory(te, player);
		else
			this.addLinkedInventory(te, player);
	}

	public void sendLinkedListTo(EntityPlayer player) {
		String deviceList = "";
		
		for(int i = 0; i < this.playerCounts; i++) {
			deviceList += this.userName[i]+", ";
		}
		
		for(int i = 0; i < this.teCounts; i++) {
			deviceList += this.blockEntity[i].getBlockType().getLocalizedName()+", ";
		}
		
		player.addChatMessage(this.getLinkCount()+" out of "+this.MAX_LINKS+" inventories linked: "+deviceList);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return this.linkedInventory instanceof ISidedInventory ? ((ISidedInventory) this.linkedInventory).getAccessibleSlotsFromSide(var1) : this.linkedInventory != null ? getUnSidedInventorySlots(this.linkedInventory) : new int[0];
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		if(this.linkedInventory == null) return false;
		
		return (this.linkedInventory instanceof ISidedInventory)?((ISidedInventory)this.linkedInventory).canInsertItem(i, itemstack, j):this.linkedInventory != null;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		if(this.linkedInventory == null) return false;
		
		return (this.linkedInventory instanceof ISidedInventory)?((ISidedInventory)this.linkedInventory).canExtractItem(i, itemstack, j):this.linkedInventory != null;
	}
	
	public static int[] getUnSidedInventorySlots(IInventory inventory) {
        int[] slots = new int[inventory.getSizeInventory()];
        
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }

        return slots;
	}
}

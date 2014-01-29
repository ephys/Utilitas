package nf.fr.ephys.playerproxies.common.tileentity;

import ic2.api.energy.tile.IEnergySink;

import java.util.ArrayList;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.turtle.api.ITurtleAccess;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TEBlockInterface extends TileEntity implements ISidedInventory,
		IFluidHandler, IEnergyHandler, IPowerReceptor, IEnergySink,
		IAspectContainer, IEssentiaTransport {
	private String userName = null;
	private EntityPlayer userEntity = null;

	private TileEntity blockEntity = null;
	private ITurtleAccess turtleAccess = null;

	private int[] entityLocation = null;

	public static final int INVTYPE_NULL = -1;
	public static final int INVTYPE_PLAYER = 0;
	public static final int INVTYPE_TE = 1;
	public static final int INVTYPE_TURTLE = 2;

	private int[] getTileLocation(TileEntity te) {
		return new int[] { te.xCoord, te.yCoord, te.zCoord };
	}

	@Override
	public Packet getDescriptionPacket() {
		System.out.println("Updating player");
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new Packet132TileEntityData(this.xCoord, this.yCoord,
				this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		readFromNBT(packet.data);
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);

		if (this.userName != null)
			par1NBTTagCompound.setString("userName", userName);
		else if (this.blockEntity != null) {
			par1NBTTagCompound.setIntArray("entityLocation",
					getTileLocation(blockEntity));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if (nbt.hasKey("userName")) {
			this.userName = nbt.getString("userName");
		} else {
			this.userName = null;
		}

		if (nbt.hasKey("entityLocation")) {
			this.entityLocation = nbt.getIntArray("entityLocation");
		} else {
			this.blockEntity = null;
		}
	}

	private float particlePos = 0;

	public void updateEntity() {
		super.updateEntity();

		int invType = this.getCurrentInventoryType();
		if (invType != INVTYPE_PLAYER) {
			if (entityLocation != null && entityLocation.length == 3) {
				System.out.println("changing entity");
				this.blockEntity = this.getWorldObj()
						.getBlockTileEntity(entityLocation[0],
								entityLocation[1], entityLocation[2]);

				if (this.blockEntity instanceof ITurtleAccess)
					this.turtleAccess = (ITurtleAccess) this.blockEntity;

				entityLocation = null;
			}
		}

		if (!worldObj.isRemote) {
			switch (invType) {
			case INVTYPE_PLAYER:
				if (userEntity == null || userEntity.isDead) {
					userEntity = MinecraftServer.getServer()
							.getConfigurationManager()
							.getPlayerForUsername(userName);
				}
				break;

			case INVTYPE_TE:
				if (this.blockEntity == null)
					break;

				if (this.blockEntity.isInvalid()) {
					this.blockEntity = null;
				}

				break;

			case INVTYPE_TURTLE:
				if (this.turtleAccess == null)
					this.blockEntity = null;

				if (this.blockEntity == null || this.blockEntity.isInvalid()) {
					if (this.turtleAccess.getWorld() == null)
						this.turtleAccess = null;
					else
						this.blockEntity = this.findTurtleTE();

					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
		}
	}

	private TileEntity findTurtleTE() {
		Vec3 pos = this.turtleAccess.getPosition();
		return this.turtleAccess.getWorld().getBlockTileEntity(
				(int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord);
	}

	// ================================================================================
	// IInventory interface
	// ================================================================================
	@Override
	public int getSizeInventory() {
		IInventory linkedInventory = this.getLinkedInventory();
		System.out.println(linkedInventory);
		return (linkedInventory != null) ? linkedInventory.getSizeInventory()
				: 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		IInventory linkedInventory = this.getLinkedInventory();
		return (linkedInventory != null) ? linkedInventory.getStackInSlot(i)
				: null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		IInventory linkedInventory = this.getLinkedInventory();
		return (linkedInventory != null) ? linkedInventory.decrStackSize(i, j)
				: null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		IInventory linkedInventory = this.getLinkedInventory();
		if (linkedInventory != null)
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
		IInventory linkedInventory = this.getLinkedInventory();
		return (linkedInventory != null) ? linkedInventory
				.getInventoryStackLimit() : 0;
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();

		IInventory linkedInventory = this.getLinkedInventory();
		if (linkedInventory != null)
			linkedInventory.onInventoryChanged();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		IInventory linkedInventory = this.getLinkedInventory();

		System.out.println(worldObj.isRemote + " - " + linkedInventory);

		return (linkedInventory != null) ? linkedInventory.isItemValidForSlot(
				i, itemstack) : false;
	}

	// ================================================================================
	// ISidedInventory interface
	// ================================================================================

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		IInventory linkedInventory = this.getLinkedInventory();

		return (linkedInventory instanceof ISidedInventory) ? ((ISidedInventory) linkedInventory)
				.getAccessibleSlotsFromSide(var1)
				: linkedInventory != null ? getUnSidedInventorySlots((IInventory) linkedInventory)
						: new int[0];
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		IInventory linkedInventory = this.getLinkedInventory();

		return (linkedInventory instanceof ISidedInventory) ? ((ISidedInventory) linkedInventory)
				.canInsertItem(i, itemstack, j) : linkedInventory != null;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		IInventory linkedInventory = this.getLinkedInventory();

		return (linkedInventory instanceof ISidedInventory) ? ((ISidedInventory) linkedInventory)
				.canExtractItem(i, itemstack, j) : linkedInventory != null;
	}

	public static int[] getUnSidedInventorySlots(IInventory inventory) {
		int[] slots = new int[inventory.getSizeInventory()];

		for (int i = 0; i < slots.length; i++) {
			slots[i] = i;
		}

		return slots;
	}

	// ================================================================================
	// IFluidHandler interface
	// ================================================================================

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		TileEntity linkedTE = this.getLinkedTileEntity();

		return (linkedTE instanceof IFluidHandler) ? ((IFluidHandler) linkedTE)
				.fill(from, resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		TileEntity linkedTE = this.getLinkedTileEntity();

		return (linkedTE instanceof IFluidHandler) ? ((IFluidHandler) linkedTE)
				.drain(from, resource, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		TileEntity linkedTE = this.getLinkedTileEntity();

		return (linkedTE instanceof IFluidHandler) ? ((IFluidHandler) linkedTE)
				.drain(from, maxDrain, doDrain) : null;

	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		TileEntity linkedTE = this.getLinkedTileEntity();

		return (linkedTE instanceof IFluidHandler) ? ((IFluidHandler) linkedTE)
				.canFill(from, fluid) : false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		TileEntity linkedTE = this.getLinkedTileEntity();

		return (linkedTE instanceof IFluidHandler) ? ((IFluidHandler) linkedTE)
				.canDrain(from, fluid) : false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		TileEntity linkedTE = this.getLinkedTileEntity();

		return (linkedTE instanceof IFluidHandler) ? ((IFluidHandler) linkedTE)
				.getTankInfo(from) : null;
	}

	// ================================================================================
	// End of interfaces
	// ================================================================================

	private boolean isValidTE(TileEntity te) {
		return te instanceof ISidedInventory || te instanceof IEnergyHandler
				|| te instanceof IFluidHandler || te instanceof IPowerReceptor
				|| te instanceof IEnergySink || te instanceof IAspectContainer
				|| te instanceof IEssentiaTransport;
	}

	public int getCurrentInventoryType() {
		if (this.userName != null)
			return INVTYPE_PLAYER;

		if (this.turtleAccess != null)
			return INVTYPE_TURTLE;

		if (this.blockEntity != null)
			return INVTYPE_TE;

		return INVTYPE_NULL;
	}

	public void toggleLinked(TileEntity te, EntityPlayer player) {
		if (te instanceof ITurtleAccess) {
			if (this.turtleAccess == (ITurtleAccess) te) {
				this.turtleAccess = null;
				this.blockEntity = null;

				player.addChatMessage("Turtle unlinked to this universal interface");
			} else {
				this.turtleAccess = (ITurtleAccess) te;
				this.blockEntity = this.findTurtleTE();

				this.userName = null;
				this.userEntity = null;

				player.addChatMessage("Turtle linked to this universal interface");
			}
		} else if (isValidTE(te))
			if (this.blockEntity == te) {
				this.blockEntity = null;
				player.addChatMessage((new ItemStack(te.getBlockType(),
						te.blockMetadata).getDisplayName())
						+ " unlinked to the universal interface");
			} else {
				this.blockEntity = te;
				this.userName = null;
				this.userEntity = null;
				this.turtleAccess = null;

				player.addChatMessage((new ItemStack(te.getBlockType(),
						te.blockMetadata).getDisplayName())
						+ " linked to the universal interface");
			}
	}

	public void toggleLinked(EntityPlayer player) {
		if (this.userName != null && this.userName.equals(player.username)) {
			this.userName = null;
			this.userEntity = null;

			player.addChatMessage("Your inventory is now unlinked to this universal interface");
		} else {
			this.userEntity = player;
			this.userName = player.username;
			this.turtleAccess = null;
			this.blockEntity = null;

			player.addChatMessage("Your inventory is now linked to this universal interface");
		}
	}

	private String getLinkedInventoryName() {
		int invType = this.getCurrentInventoryType();

		if (invType == this.INVTYPE_PLAYER)
			return this.userName;

		if (invType == this.INVTYPE_TE || invType == this.INVTYPE_TURTLE)
			return new ItemStack(this.blockEntity.getBlockType(),
					this.blockEntity.blockMetadata).getDisplayName();

		return "None";
	}

	public void sendLinkedListTo(EntityPlayer player) {
		player.addChatMessage("Linked inventory: " + getLinkedInventoryName());
	}

	private IInventory getLinkedInventory() {
		if (this.userEntity != null)
			return this.userEntity.inventory;

		return (IInventory) this.blockEntity;
	}

	public TileEntity getLinkedTileEntity() {
		return this.blockEntity;
	}

	@SideOnly(Side.CLIENT)
	public EntityPlayer getLinkedPlayer() {
		if ((this.userEntity == null || this.userEntity.isDead)
				&& this.userName != null) {
			this.userEntity = MinecraftServer.getServer()
					.getConfigurationManager().getPlayerForUsername(userName);
		}

		return userEntity;
	}

	@Override
	public boolean canInterface(ForgeDirection arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEnergyHandler ? ((IEnergyHandler) linkedTE)
				.canInterface(arg0) : false;
	}

	@Override
	public int extractEnergy(ForgeDirection arg0, int arg1, boolean arg2) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEnergyHandler ? ((IEnergyHandler) linkedTE)
				.extractEnergy(arg0, arg1, arg2) : 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEnergyHandler ? ((IEnergyHandler) linkedTE)
				.getEnergyStored(arg0) : 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEnergyHandler ? ((IEnergyHandler) linkedTE)
				.getMaxEnergyStored(arg0) : 0;
	}

	@Override
	public int receiveEnergy(ForgeDirection arg0, int arg1, boolean arg2) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEnergyHandler ? ((IEnergyHandler) linkedTE)
				.receiveEnergy(arg0, arg1, arg2) : 0;
	}

	@Override
	public void doWork(PowerHandler arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		if (linkedTE instanceof IPowerReceptor)
			((IPowerReceptor) linkedTE).doWork(arg0);
	}

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IPowerReceptor ? ((IPowerReceptor) linkedTE)
				.getPowerReceiver(arg0) : null;
	}

	@Override
	public World getWorld() {
		TileEntity linkedTE = this.getLinkedTileEntity();
		if (linkedTE != null)
			return linkedTE.worldObj;

		return null;
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity arg0, ForgeDirection arg1) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEnergySink ? ((IEnergySink) linkedTE)
				.acceptsEnergyFrom(arg0, arg1) : false;
	}

	@Override
	public double demandedEnergyUnits() {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEnergySink ? ((IEnergySink) linkedTE)
				.demandedEnergyUnits() : 0;
	}

	@Override
	public int getMaxSafeInput() {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEnergySink ? ((IEnergySink) linkedTE)
				.getMaxSafeInput() : 0;
	}

	@Override
	public double injectEnergyUnits(ForgeDirection arg0, double arg1) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEnergySink ? ((IEnergySink) linkedTE)
				.injectEnergyUnits(arg0, arg1) : 0;
	}

	@Override
	public boolean canInputFrom(ForgeDirection arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEssentiaTransport ? ((IEssentiaTransport)linkedTE).canInputFrom(arg0) : false;
	}

	@Override
	public boolean canOutputTo(ForgeDirection arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEssentiaTransport ? ((IEssentiaTransport)linkedTE).canOutputTo(arg0) : false;
	}

	@Override
	public AspectList getEssentia(ForgeDirection arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEssentiaTransport ? ((IEssentiaTransport)linkedTE).getEssentia(arg0) : null;
	}

	@Override
	public int getMinimumSuction() {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEssentiaTransport ? ((IEssentiaTransport)linkedTE).getMinimumSuction() : 0;
	}

	@Override
	public AspectList getSuction(ForgeDirection arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEssentiaTransport ? ((IEssentiaTransport)linkedTE).getSuction(arg0) : null;
	}

	@Override
	public boolean isConnectable(ForgeDirection arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEssentiaTransport ? ((IEssentiaTransport)linkedTE).isConnectable(arg0) : false;
	}

	@Override
	public boolean renderExtendedTube() {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEssentiaTransport ? ((IEssentiaTransport)linkedTE).renderExtendedTube() : false;
	}

	@Override
	public void setSuction(AspectList arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		if(linkedTE instanceof IEssentiaTransport)
			((IEssentiaTransport)linkedTE).setSuction(arg0);
	}

	@Override
	public void setSuction(Aspect arg0, int arg1) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		if(linkedTE instanceof IEssentiaTransport)
			((IEssentiaTransport)linkedTE).setSuction(arg0, arg1);;
	}

	@Override 
	public int takeVis(Aspect arg0, int arg1) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IEssentiaTransport ? ((IEssentiaTransport)linkedTE).takeVis(arg0, arg1) : 0;
	}

	@Override
	public int addToContainer(Aspect arg0, int arg1) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IAspectContainer ? ((IAspectContainer)linkedTE).addToContainer(arg0, arg1) : 0;
	}

	@Override
	public int containerContains(Aspect arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IAspectContainer ? ((IAspectContainer)linkedTE).containerContains(arg0) : 0;
	}

	@Override
	public boolean doesContainerAccept(Aspect arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IAspectContainer ? ((IAspectContainer)linkedTE).doesContainerAccept(arg0) : false;
	}

	@Override
	public boolean doesContainerContain(AspectList arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IAspectContainer ? ((IAspectContainer)linkedTE).doesContainerContain(arg0) : false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect arg0, int arg1) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IAspectContainer ? ((IAspectContainer)linkedTE).doesContainerContainAmount(arg0, arg1) : false;
	}

	@Override
	public AspectList getAspects() {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IAspectContainer ? ((IAspectContainer)linkedTE).getAspects() : null;
	}

	@Override
	public void setAspects(AspectList arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		if(linkedTE instanceof IAspectContainer)
			((IAspectContainer)linkedTE).setAspects(arg0);
	}

	@Override
	public boolean takeFromContainer(AspectList arg0) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IAspectContainer ? ((IAspectContainer)linkedTE).takeFromContainer(arg0) : false;
	}

	@Override
	public boolean takeFromContainer(Aspect arg0, int arg1) {
		TileEntity linkedTE = this.getLinkedTileEntity();
		return linkedTE instanceof IAspectContainer ? ((IAspectContainer)linkedTE).takeFromContainer(arg0, arg1) : false;
	}
}

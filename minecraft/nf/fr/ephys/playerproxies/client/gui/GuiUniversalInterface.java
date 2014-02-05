package nf.fr.ephys.playerproxies.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import nf.fr.ephys.playerproxies.common.container.ContainerUniversalInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;

public class GuiUniversalInterface extends GuiContainer {
	private ContainerUniversalInterface container;
	private static ResourceLocation background = new ResourceLocation("ephys.pp", "/textures/gui/universal_interface.png");

	public GuiUniversalInterface(ContainerUniversalInterface container) {
		super(container);

		this.container = container;

		xSize = 176;
		ySize = 70;
	}
	
	@Override
	public void initGui() {
		super.initGui();

		buttonList.add(new GuiButton(0, this.xSize, this.ySize, 150, 20, "Ender Chest"));
		
		((GuiButton)buttonList.get(0)).displayString = this.container.getTileEntity().enderMode ? "Ender Chest" : "Inventory";
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		switch(button.id) {
			case 0:
				this.container.toggleEnderMode();
				((GuiButton)buttonList.get(0)).displayString = this.container.getTileEntity().enderMode ? "Ender Chest" : "Inventory";
				break;
		}

		super.actionPerformed(button);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString("Universal Interface", 8, 6, 4210752);

		TEBlockInterface te = this.container.getTileEntity();
		int inventoryType = te.getCurrentInventoryType();
		
		((GuiButton)buttonList.get(0)).enabled = (inventoryType == TEBlockInterface.INVTYPE_PLAYER);
		((GuiButton)buttonList.get(0)).xPosition = (width - xSize) / 2 + 15;
		((GuiButton)buttonList.get(0)).yPosition = (height - ySize) / 2 + 40;
		
		fontRenderer.drawString("Linked inventory: ", 8, 20, 4210752);
		int color = (inventoryType != TEBlockInterface.INVTYPE_NULL) ? 0x00AA00 : 0xAA0000;
		fontRenderer.drawString(te.getLinkedInventoryName(),
			8 + this.fontRenderer.getStringWidth("Linked inventory: "),
			20, color);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    	this.mc.renderEngine.bindTexture(this.background);
    	int x = (width - xSize) / 2;
    	int y = (height - ySize) / 2;
    	this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	};
}

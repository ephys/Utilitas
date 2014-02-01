package nf.fr.ephys.playerproxies.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import nf.fr.ephys.playerproxies.common.container.ContainerUniversalInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;

public class GuiUniversalInterface extends GuiContainer {
	public static int ID = 0;
	private ContainerUniversalInterface container;

	public GuiUniversalInterface(ContainerUniversalInterface container) {
		super(container);

		this.container = container;

		xSize = 400;
		ySize = 200;
	}
	
	@Override
	public void initGui() {
		super.initGui();

		buttonList.add(new GuiButton(0, this.xSize-210, this.ySize-30, 200, 20, "Ender Chest"));
		
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
		fontRenderer.drawString("Universal Interface", 8, 6, 0xFFFFFF);

		TEBlockInterface te = this.container.getTileEntity();

		String linkedInventory = null;

		fontRenderer.drawString("Linked inventory: ", 8, 30, 0xFFFFFF);
		
		int color = (te.getCurrentInventoryType() != TEBlockInterface.INVTYPE_NULL) ? 0x00AA00 : 0xAA0000;
		fontRenderer.drawString(te.getLinkedInventoryName(),
			8 + this.fontRenderer.getStringWidth("Linked inventory: "),
			30, color);
	}

	@Override
	public void drawDefaultBackground() {
		int x = (width - xSize) >> 1;
		int y = (height - ySize) >> 1;

		drawRect(x, y, xSize, 42, Integer.MIN_VALUE);
		drawRect(x, y + 23, xSize, ySize, Integer.MIN_VALUE);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
	};
}

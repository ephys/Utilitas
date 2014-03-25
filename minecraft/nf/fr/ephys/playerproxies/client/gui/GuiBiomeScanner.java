package nf.fr.ephys.playerproxies.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import nf.fr.ephys.playerproxies.common.container.ContainerBiomeScanner;
import nf.fr.ephys.playerproxies.common.container.ContainerUniversalInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;

public class GuiBiomeScanner extends GuiContainer {
	private ContainerBiomeScanner container;
	private static ResourceLocation background = new ResourceLocation("ephys.pp", "/textures/gui/biomeScanner.png");

	public GuiBiomeScanner(ContainerBiomeScanner container) {
		super(container);

		this.container = container;

		xSize = 250;
		ySize = 200;
	}
	
	@Override
	public void initGui() {
		super.initGui();

		buttonList.add(new GuiButton(0, this.xSize, this.ySize, 150, 20, "Replicate biome signature"));
	}

	private TileEntityBiomeScanner getTileEntity() {
		return this.container.getTileEntity();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch(button.id) {
			case 0:
				getTileEntity().startReckoning();
				break;
		}

		super.actionPerformed(button);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString("turtulscan 0.1a", 8, 8, 0x212121);
		//fontRenderer.drawString("progress "+getTileEntity().getProgress()+"%", 8, 20, 0xeeeeee);
		
		BiomeGenBase biome = getTileEntity().getBiome();
		
		final int xOffset = 4;		
		fontRenderer.drawString("Biome: " + biome.biomeName, 8, 23+xOffset, 0xeeeeee);
		fontRenderer.drawString("Weather type: "+(biome.getEnableSnow() ? "Snow":"Rain"), 12, 35+xOffset, 0xeeeeee);
		fontRenderer.drawString("Average temperature: "+(biome.getFloatTemperature()*10)+"°c", 12, 47+xOffset, 0xeeeeee);
		fontRenderer.drawString("Average rainfall: "+biome.getFloatRainfall()+"mb", 12, 59+xOffset, 0xeeeeee);
		fontRenderer.drawString("Spawn capacity: "+(biome.getSpawningChance()*100)+"%", 12, 71+xOffset, 0xeeeeee);

		((GuiButton)buttonList.get(0)).enabled = (getTileEntity().getProgress() == -1);
		((GuiButton)buttonList.get(0)).xPosition = ((width - xSize) >> 1) + 8;
		((GuiButton)buttonList.get(0)).yPosition = ((height - ySize) >> 1) + ySize - 20 - 8;
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

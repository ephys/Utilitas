package nf.fr.ephys.playerproxies.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import nf.fr.ephys.playerproxies.common.container.ContainerBiomeScanner;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBiomeScanner;
import org.lwjgl.opengl.GL11;

public class GuiBiomeScanner extends GuiContainer {
	private ContainerBiomeScanner container;
	private static ResourceLocation background = new ResourceLocation("ephys.pp", "textures/gui/biomeScanner.png");

	public GuiBiomeScanner(ContainerBiomeScanner container) {
		super(container);

		this.container = container;

		xSize = 250;
		ySize = 200;
	}

	@Override
	public void initGui() {
		super.initGui();
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
		fontRendererObj.drawString("turtulscan 0.1a", 8, 8, 0x212121);

		BiomeGenBase biome = getTileEntity().getBiome();

		final int xOffset = 4;
		fontRendererObj.drawString("Biome: " + biome.biomeName, 8, 23 + xOffset, 0xeeeeee);
		fontRendererObj.drawString("Weather type: " + (biome.getEnableSnow() ? "Snow":"Rain"), 12, 35 + xOffset, 0xeeeeee);
		fontRendererObj.drawString("Average temperature: " + (getTileEntity().getFloatTemperature() * 100) + "%", 12, 47 + xOffset, 0xeeeeee);
		fontRendererObj.drawString("Average rainfall: " + (biome.getFloatRainfall() * 100) + "%", 12, 59 + xOffset, 0xeeeeee);
		fontRendererObj.drawString("Spawn capacity: " + (biome.getSpawningChance() * 100) + "%", 12, 71 + xOffset, 0xeeeeee);

		if (getTileEntity().getProgress() != -1)
			fontRendererObj.drawString("Decrypting biome signature : " + getTileEntity().getProgress() + "%", 8, 94, 0xeeeeee);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    	this.mc.renderEngine.bindTexture(background);
    	int x = (width - xSize) / 2;
    	int y = (height - ySize) / 2;
    	this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}

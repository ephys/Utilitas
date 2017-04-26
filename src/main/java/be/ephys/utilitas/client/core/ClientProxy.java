package be.ephys.utilitas.client.core;

import be.ephys.utilitas.common.core.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		registerRenderers();

		((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new FluidColorRegistry());
	}

	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInterface.class, new TileEntityInterfaceRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemTicker.class, new TileEntityItemTickerRenderer());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBeaconTierII.class, new TileEntityBeaconTierIIRenderer());

		RenderingRegistry.registerBlockHandler(BlockBeaconTierIIRenderer.RENDER_ID, new BlockBeaconTierIIRenderer());
		RenderingRegistry.registerBlockHandler(BlockFluidDiffuserRenderer.RENDER_ID, new BlockFluidDiffuserRenderer());
	}

	@Override
	public void registerPacket() {
		super.registerPacket();
	}

	public static World getClientWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
}

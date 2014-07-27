package nf.fr.ephys.playerproxies.client.core;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import nf.fr.ephys.playerproxies.client.registry.FluidColorRegistry;
import nf.fr.ephys.playerproxies.client.renderer.*;
import nf.fr.ephys.playerproxies.common.core.CommonProxy;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBeaconTierII;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityItemTicker;

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
}
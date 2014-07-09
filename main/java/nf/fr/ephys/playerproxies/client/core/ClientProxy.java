package nf.fr.ephys.playerproxies.client.core;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import nf.fr.ephys.playerproxies.client.renderer.TileEntityInterfaceRenderer;
import nf.fr.ephys.playerproxies.client.renderer.TileEntityItemTickerRenderer;
import nf.fr.ephys.playerproxies.common.core.CommonProxy;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBeaconTierII;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityItemTicker;

public class ClientProxy extends CommonProxy {
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		registerRenderers();
	}

	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInterface.class, new TileEntityInterfaceRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemTicker.class, new TileEntityItemTickerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBeaconTierII.class, new TileEntityBeaconRenderer());
	}
}
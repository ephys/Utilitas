package nf.fr.ephys.playerproxies.client.core;

import nf.fr.ephys.playerproxies.client.renderer.TileEntityBlockInterfaceRenderer;
import nf.fr.ephys.playerproxies.common.core.CommonProxy;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void initMod() {
		super.initMod();

		registerRenderers();
	}
	
	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TEBlockInterface.class, new TileEntityBlockInterfaceRenderer());
	}
}

package nf.fr.ephys.playerproxies.client.core;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBlaze;
import net.minecraftforge.common.MinecraftForge;
import nf.fr.ephys.playerproxies.client.renderer.GhostRenderer;
import nf.fr.ephys.playerproxies.client.renderer.TileEntityBlockInterfaceRenderer;
import nf.fr.ephys.playerproxies.common.core.CommonProxy;
import nf.fr.ephys.playerproxies.common.entity.Ghost;
import nf.fr.ephys.playerproxies.common.tileentity.TEBlockInterface;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void initMod() {
		super.initMod();

		registerRenderers();
	}
	
	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TEBlockInterface.class, new TileEntityBlockInterfaceRenderer());
//		RenderingRegistry.registerEntityRenderingHandler(Ghost.class, new GhostRenderer());
		
		MinecraftForge.EVENT_BUS.register(new GhostRenderer());
	}
}

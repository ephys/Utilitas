package nf.fr.ephys.playerproxies.client.core;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBlaze;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import nf.fr.ephys.playerproxies.client.renderer.GhostRenderer;
import nf.fr.ephys.playerproxies.client.renderer.ItemBiomeStorageRenderer;
import nf.fr.ephys.playerproxies.client.renderer.TileEntityBlockInterfaceRenderer;
import nf.fr.ephys.playerproxies.client.renderer.TileEntityItemTickerRenderer;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.core.CommonProxy;
import nf.fr.ephys.playerproxies.common.entity.Ghost;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBlockInterface;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityItemTicker;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void initMod() {
		super.initMod();

		registerRenderers();
	}
	
	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlockInterface.class, new TileEntityBlockInterfaceRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemTicker.class, new TileEntityItemTickerRenderer());
		
		RenderingRegistry.registerEntityRenderingHandler(Ghost.class, new GhostRenderer());
		MinecraftForgeClient.registerItemRenderer(PlayerProxies.itemBiomeStorage.itemID, new ItemBiomeStorageRenderer());
	}
}
package be.ephys.utilitas;

import be.ephys.utilitas.base.core.ClientProxy;
import be.ephys.utilitas.base.core.CommonProxy;
import be.ephys.utilitas.base.core.GuiHandler;
import be.ephys.utilitas.base.core.MutableCreativeTab;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = Utilitas.MODID, name = Utilitas.NAME,
    version = Utilitas.VERSION, dependencies = "required-after:Forge@[12.18.3.2185,)"
//    guiFactory = GuiModConfigFactory.CLASS_NAME
)
public class Utilitas {

    public static final String VERSION = "1.10.2-2.0.0";
    public static final String MODID = "utilitas";
    public static final String NAME = "Utilitas";

    @Mod.Instance(Utilitas.MODID)
    public static Utilitas instance;

    @SidedProxy(
        clientSide = ClientProxy.CLASS_NAME,
        serverSide = CommonProxy.CLASS_NAME
    )
    public static CommonProxy proxy;
    public final GuiHandler guiHandler = new GuiHandler();

    private Logger logger;

    //    public static final SimpleNetworkWrapper NET_HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    public static final MutableCreativeTab CREATIVE_TAB = new MutableCreativeTab(MODID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void imcCallback(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            proxy.handleImc(message);
        }
    }

    public static Logger getLogger() {
        return instance.logger;
    }

    public static int registerGui(GuiHandler.GuiWrapper wrapper) {
        return Utilitas.instance.guiHandler.registerGui(wrapper);
    }

    public static void registerTile(Class<? extends TileEntity> clazz, String key) {
        GameRegistry.registerTileEntity(clazz, MODID + key);
    }
}

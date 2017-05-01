package be.ephys.utilitas.base.feature;

import be.ephys.utilitas.Utilitas;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Feature {

    private final FeatureMeta meta;

    public Feature() {
        this.meta = this.getClass().getAnnotation(FeatureMeta.class);
    }

    public final FeatureMeta metadata() {
        return meta;
    }

    public void preInit(FMLPreInitializationEvent event) {}

    public void registerPackets(FMLPreInitializationEvent event) {};

    public void registerContents(FMLPreInitializationEvent event) {}

    public void registerCrafts(FMLInitializationEvent event) {}

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}

    @SideOnly(Side.CLIENT)
    public void preInitClient(FMLPreInitializationEvent event) {}

    @SideOnly(Side.CLIENT)
    public void initClient(FMLInitializationEvent event) {}

    @SideOnly(Side.CLIENT)
    public void postInitClient(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}

    public void handleImc(FMLInterModComms.IMCMessage message, String key) {
        Utilitas.getLogger().warn("Received IMC with key '" + message.key + "' from '" + message.getSender() + "' but it is unsupported.");
    }
}

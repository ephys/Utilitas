package be.ephys.utilitas.base.core;

import be.ephys.utilitas.base.feature.Feature;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    public static final String CLASS_NAME = "be.ephys.utilitas.base.core.ClientProxy";

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        for (Feature f : features) {
            f.preInitClient(event);
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        for (Feature f : features) {
            f.initClient(event);
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        for (Feature f : features) {
            f.postInitClient(event);
        }
    }
}

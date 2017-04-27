package be.ephys.utilitas.common.util;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public final class CapabilityHelper {

    public static <T> T getCapability(ICapabilityProvider entity, Capability<T> capability) {
        for (EnumFacing face : EnumFacing.values()) {
            if (entity.hasCapability(capability, face)) {
                return entity.getCapability(capability, face);
            }
        }

        return null;
    }
}

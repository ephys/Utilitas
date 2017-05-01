package be.ephys.utilitas.api;

import net.minecraft.entity.player.EntityPlayer;

public interface ILinkable {

    boolean link(EntityPlayer linker, Object linkedObject);
}

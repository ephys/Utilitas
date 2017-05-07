package be.ephys.utilitas.feature.slime_staff;

import be.ephys.utilitas.api.ISlimeFamiliar;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.World;

public class EntitySlimeFamiliar extends EntityColoredSlime implements ISlimeFamiliar {

    public static final String ENTITY_NAME = "slime_familiar";

    public EntitySlimeFamiliar(World worldIn) {
        super(worldIn);

        setSlimeSize(1);
        setEntityInvulnerable(true);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public int getSlimeColor() {
        return 0;
    }

    @Override
    protected EntitySlime createInstance() {
        throw new RuntimeException("Cannot create children for familiar slimes");
    }
}

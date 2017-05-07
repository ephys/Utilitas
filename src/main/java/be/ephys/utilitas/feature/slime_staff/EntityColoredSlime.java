package be.ephys.utilitas.feature.slime_staff;

import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.World;

public abstract class EntityColoredSlime extends EntitySlime {

    public EntityColoredSlime(World worldIn) {
        super(worldIn);
    }

    public abstract int getSlimeColor();

    @Override
    protected boolean spawnCustomParticles() {
        // TODO spawn colored particles
        return true;
    }

    abstract protected EntitySlime createInstance();
}

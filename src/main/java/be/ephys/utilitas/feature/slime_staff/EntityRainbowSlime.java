package be.ephys.utilitas.feature.slime_staff;

import be.ephys.utilitas.base.helpers.RenderHelper;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;

public class EntityRainbowSlime extends EntityColoredSlime {

    public static final String ENTITY_NAME = "rainbow_slime";

    public EntityRainbowSlime(World worldIn) {
        super(worldIn);
    }

    @Override
    public int getSlimeColor() {
        return worldObj.isRaining()
            ? RenderHelper.getRainbowColor(2, 0.7f, 1)
            : 0x76BE6D;
    }

    @Override
    protected EntitySlime createInstance() {
        return new EntityRainbowSlime(worldObj);
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        if (!worldObj.isRaining()) {
            return LootTableList.ENTITIES_SLIME;
        }

        if (getSlimeSize() > 1) {
            return LootTableList.EMPTY;
        }

        return this.getSlimeSize() == 1 ? LootTableList.ENTITIES_SLIME : LootTableList.EMPTY;
    }

//    protected Item getDropItem() {
//        if (this.getSlimeSize() != 1) {
//            return null;
//        }
//
//        if (!this.worldObj.isRaining()) {
//            return Items.SLIME_BALL;
//        }
//
//        return FeatureSlimeStaff.INSTANCE.rainbowSlimeBall;
//    }
}

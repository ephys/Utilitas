package be.ephys.utilitas.feature.vanilla_tweaks;

import be.ephys.utilitas.base.feature.Config;
import be.ephys.utilitas.base.feature.Feature;
import be.ephys.utilitas.base.feature.FeatureMeta;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@FeatureMeta(
    name = "Vanilla Tweaks",
    description = "Tweaks the base game"
)
public class VanillaTweaksFeature extends Feature {

    @Config(description = "Adds vanilla items like the dragon egg and command blocks to the creative tabs.")
    private boolean addToCreativeTabs = true;

    @Config(description = "Ender pearls deal no damage when used.")
    private boolean disableEnderPearlDamage = true;

    @Override
    public void postInitClient(FMLPostInitializationEvent event) {
        Items.POTIONITEM.setContainerItem(Items.GLASS_BOTTLE);

        if (addToCreativeTabs) {
            softSetCreativeTab(Blocks.DRAGON_EGG, CreativeTabs.DECORATIONS);
            softSetCreativeTab(Blocks.END_PORTAL_FRAME, CreativeTabs.DECORATIONS);

            softSetCreativeTab(Blocks.COMMAND_BLOCK, CreativeTabs.REDSTONE);
            softSetCreativeTab(Blocks.REPEATING_COMMAND_BLOCK, CreativeTabs.REDSTONE);
            softSetCreativeTab(Blocks.CHAIN_COMMAND_BLOCK, CreativeTabs.REDSTONE);
        }

        if (disableEnderPearlDamage) {
            MinecraftForge.EVENT_BUS.register(new EnderPearlEventHandler());
        }
    }

    private void softSetCreativeTab(Block block, CreativeTabs tab) {
        if (block.getCreativeTabToDisplayOn() == null) {
            block.setCreativeTab(tab);
        }
    }

    private static class EnderPearlEventHandler {

        @SubscribeEvent
        public void voidEnderPearlDamage(EnderTeleportEvent event) {
            event.setAttackDamage(0);

            Entity entity = event.getEntity();
            World world = entity.getEntityWorld();

            world.playSound(null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            entity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
        }
    }
}

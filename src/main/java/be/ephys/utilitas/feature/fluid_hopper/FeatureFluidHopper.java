package be.ephys.utilitas.feature.fluid_hopper;

import be.ephys.utilitas.Utilitas;
import be.ephys.utilitas.base.core.GuiHandler;
import be.ephys.utilitas.base.feature.Config;
import be.ephys.utilitas.base.feature.Feature;
import be.ephys.utilitas.base.feature.FeatureMeta;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@FeatureMeta(
    name = "Fluid Hopper",
    description = "A hopper, for fluids!"
)
public class FeatureFluidHopper extends Feature {

    public static final GuiHandler.GuiWrapper GUI_FLUID_HOPPER = new GuiHandler.GuiWrapper() {

        @Override
        public Container getContainer(EntityPlayer player, World world, BlockPos blockPos) {
            return new ContainerFluidHopper((TileEntityFluidHopper) world.getTileEntity(blockPos));
        }

        @Override
        public Gui getGui(EntityPlayer player, World world, BlockPos blockPos) {
            return new GuiFluidHopper((ContainerFluidHopper) this.getContainer(player, world, blockPos));
        }
    };

    public static final BlockFluidHopper FLUID_HOPPER = new BlockFluidHopper();

    @Config
    private boolean minecartHopperEnabled = true;

    @Config
    private boolean minecartTankEnabled = true;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        Utilitas.registerGui(GUI_FLUID_HOPPER);

        GameRegistry.register(FLUID_HOPPER);
        FLUID_HOPPER.setCreativeTab(Utilitas.CREATIVE_TAB);

        GameRegistry.registerTileEntity(TileEntityFluidHopper.class, "fluid_hopper");
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        GameRegistry.addRecipe(
            new ItemStack(FLUID_HOPPER),
            "l l", "lhl", " l ",
            'l', new ItemStack(Items.DYE, 1, 4),
            'h', Blocks.HOPPER
        );
    }
}

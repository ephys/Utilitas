package nf.fr.ephys.playerproxies.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockBeacon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nf.fr.ephys.playerproxies.common.PlayerProxies;
import nf.fr.ephys.playerproxies.common.tileentity.TileEntityBeaconTierII;

public class BlockBeaconTierII extends BlockBeacon {
	public static void register() {
		PlayerProxies.Blocks.betterBeacon = new BlockBeaconTierII();
		PlayerProxies.Blocks.betterBeacon.setBlockName("PP_BetterBeacon").setCreativeTab(PlayerProxies.creativeTab);

		GameRegistry.registerBlock(PlayerProxies.Blocks.betterBeacon, PlayerProxies.Blocks.betterBeacon.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityBeaconTierII.class, PlayerProxies.Blocks.betterBeacon.getUnlocalizedName());
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return super.createNewTileEntity(world, i);
	}

	@Override
	public boolean onBlockActivated(World world, int i, int i2, int i3, EntityPlayer entityPlayer, int i4, float v, float v2, float v3) {
		return true;
	}
}

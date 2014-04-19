package nf.fr.ephys.playerproxies.client.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

public class WorldProviderNoVoidFog extends WorldProvider {
	private WorldProvider superWorldProvider;

	public WorldProviderNoVoidFog(WorldProvider superWorldProvider) {
		this.superWorldProvider = superWorldProvider;
		this.dimensionId = superWorldProvider.dimensionId;
		this.field_82913_c = superWorldProvider.field_82913_c;
		this.hasNoSky = superWorldProvider.hasNoSky;
		this.isHellWorld = superWorldProvider.isHellWorld;
		this.lightBrightnessTable = superWorldProvider.lightBrightnessTable;
		this.terrainType = superWorldProvider.terrainType;
		this.worldChunkMgr = superWorldProvider.worldChunkMgr;
		this.worldObj = superWorldProvider.worldObj;
	}

	@Override
	public boolean getWorldHasVoidParticles() {
		return false;
	}

	@Override
	public double getVoidFogYFactor() {
		return 1.0D;
	}
	
	@Override
	public float[] calcSunriseSunsetColors(float par1, float par2) {
		return superWorldProvider.calcSunriseSunsetColors(par1, par2);
	}

	@Override
	public String getDimensionName() {
		return superWorldProvider.getDimensionName();
	}

	@Override
	public float calculateCelestialAngle(long par1, float par3) {
		return superWorldProvider.calculateCelestialAngle(par1, par3);
	}

	@Override
	public void calculateInitialWeather() {
		superWorldProvider.calculateInitialWeather();
	}

	@Override
	public boolean canBlockFreeze(int x, int y, int z, boolean byWater) {
		return superWorldProvider.canBlockFreeze(x, y, z, byWater);
	}

	@Override
	public boolean canCoordinateBeSpawn(int par1, int par2) {
		return superWorldProvider.canCoordinateBeSpawn(par1, par2);
	}

	@Override
	public boolean canDoLightning(Chunk chunk) {
		return superWorldProvider.canDoLightning(chunk);
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return superWorldProvider.canDoRainSnowIce(chunk);
	}

	@Override
	public boolean canMineBlock(EntityPlayer player, int x, int y, int z) {
		return superWorldProvider.canMineBlock(player, x, y, z);
	}

	@Override
	public boolean canRespawnHere() {
		return superWorldProvider.canRespawnHere();
	}

	@Override
	public boolean canSnowAt(int x, int y, int z) {
		return superWorldProvider.canSnowAt(x, y, z);
	}

	@Override
	public IChunkProvider createChunkGenerator() {
		return superWorldProvider.createChunkGenerator();
	}

	@Override
	public boolean doesXZShowFog(int par1, int par2) {
		return superWorldProvider.doesXZShowFog(par1, par2);
	}

	@Override
	public Vec3 drawClouds(float partialTicks) {
		return superWorldProvider.drawClouds(partialTicks);
	}

	@Override
	public int getActualHeight() {
		return superWorldProvider.getActualHeight();
	}

	@Override
	public int getAverageGroundLevel() {
		return superWorldProvider.getAverageGroundLevel();
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return superWorldProvider.getBiomeGenForCoords(x, z);
	}

	@Override
	public float getCloudHeight() {
		return superWorldProvider.getCloudHeight();
	}

	@Override
	public void updateWeather() {
		superWorldProvider.updateWeather();
	}

	@Override
	public IRenderHandler getCloudRenderer() {
		return superWorldProvider.getCloudRenderer();
	}

	@Override
	public String getDepartMessage() {
		return superWorldProvider.getDepartMessage();
	}

	@Override
	public ChunkCoordinates getEntrancePortalLocation() {
		return superWorldProvider.getEntrancePortalLocation();
	}

	@Override
	public Vec3 getFogColor(float par1, float par2) {

		return superWorldProvider.getFogColor(par1, par2);
	}

	@Override
	public int getHeight() {

		return superWorldProvider.getHeight();
	}

	@Override
	public double getHorizon() {

		return superWorldProvider.getHorizon();
	}

	@Override
	public int getMoonPhase(long par1) {

		return superWorldProvider.getMoonPhase(par1);
	}

	@Override
	public double getMovementFactor() {

		return superWorldProvider.getMovementFactor();
	}

	@Override
	public ChunkCoordinates getRandomizedSpawnPoint() {

		return superWorldProvider.getRandomizedSpawnPoint();
	}

	@Override
	public int getRespawnDimension(EntityPlayerMP player) {

		return superWorldProvider.getRespawnDimension(player);
	}

	@Override
	public String getSaveFolder() {

		return superWorldProvider.getSaveFolder();
	}

	@Override
	public long getSeed() {

		return superWorldProvider.getSeed();
	}

	@Override
	public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {

		return superWorldProvider.getSkyColor(cameraEntity, partialTicks);
	}

	@Override
	public IRenderHandler getSkyRenderer() {

		return superWorldProvider.getSkyRenderer();
	}

	@Override
	public ChunkCoordinates getSpawnPoint() {

		return superWorldProvider.getSpawnPoint();
	}

	@Override
	public float getStarBrightness(float par1) {

		return superWorldProvider.getStarBrightness(par1);
	}

	@Override
	public String getWelcomeMessage() {

		return superWorldProvider.getWelcomeMessage();
	}

	@Override
	public long getWorldTime() {

		return superWorldProvider.getWorldTime();
	}

	@Override
	public boolean isBlockHighHumidity(int x, int y, int z) {

		return superWorldProvider.isBlockHighHumidity(x, y, z);
	}

	@Override
	public boolean isDaytime() {

		return superWorldProvider.isDaytime();
	}

	@Override
	public boolean isSkyColored() {

		return superWorldProvider.isSkyColored();
	}

	@Override
	public boolean isSurfaceWorld() {

		return superWorldProvider.isSurfaceWorld();
	}

	@Override
	public void resetRainAndThunder() {

		superWorldProvider.resetRainAndThunder();
	}

	@Override
	public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful) {

		superWorldProvider.setAllowedSpawnTypes(allowHostile, allowPeaceful);
	}

	@Override
	public void setCloudRenderer(IRenderHandler renderer) {

		superWorldProvider.setCloudRenderer(renderer);
	}

	@Override
	public void setDimension(int dim) {

		superWorldProvider.setDimension(dim);
	}

	@Override
	public void setSkyRenderer(IRenderHandler skyRenderer) {

		superWorldProvider.setSkyRenderer(skyRenderer);
	}

	@Override
	public void setSpawnPoint(int x, int y, int z) {

		superWorldProvider.setSpawnPoint(x, y, z);
	}

	@Override
	public void setWorldTime(long time) {
		superWorldProvider.setWorldTime(time);
	}

	@Override
	public boolean shouldMapSpin(String entity, double x, double y, double z) {
		return superWorldProvider.shouldMapSpin(entity, x, y, z);
	}

	@Override
	public void toggleRain() {
		superWorldProvider.toggleRain();
	}
}
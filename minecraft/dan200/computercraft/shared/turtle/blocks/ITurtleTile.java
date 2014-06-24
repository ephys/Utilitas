package dan200.computercraft.shared.turtle.blocks;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.common.IDirectionalTile;
import dan200.computercraft.shared.computer.blocks.IComputerTile;
import dan200.computercraft.shared.util.Colour;
import net.minecraft.util.Vec3;

public abstract interface ITurtleTile extends IComputerTile, IDirectionalTile {
	public abstract Colour getColour();

	public abstract ITurtleUpgrade getUpgrade(TurtleSide paramTurtleSide);

	public abstract ITurtleAccess getAccess();

	public abstract Vec3 getRenderOffset(float paramFloat);

	public abstract float getRenderYaw(float paramFloat);

	public abstract float getToolRenderAngle(TurtleSide paramTurtleSide, float paramFloat);
}
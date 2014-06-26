package dan200.computercraft.shared.turtle.blocks;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.util.Vec3;

public abstract interface ITurtleTile {
	public abstract ITurtleAccess getAccess();
}
package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a block in the game world.
 * Blocks are immovable objects that can be used to create terrain or obstacles, or any other
 * block-like element in our world
 */
public class Block extends GameObject {
	/** The size of the block in pixels. */
	public static final int SIZE= 30;

	/**
	 * Creates a new Block object with a constant size of 30x30 pixels., where any other object
	 * cant pass over it. It is not relevant if the other object in question does not have a
	 * collision defined with the block (meaning not in the same layer, and no interaction
	 * explicitly allowed)
	 * @param topLeftCorner the top left corner of the block in pixels
	 * @param renderable the renderable object that will be used to draw the block
	 */
	public Block(Vector2 topLeftCorner, Renderable renderable){
		super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
		physics().preventIntersectionsFromDirection(Vector2.ZERO);
		physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);

	}
}

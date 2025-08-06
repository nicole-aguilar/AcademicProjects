package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sky in the game world.
 */
public class Sky {
	/** Default color of the sky.*/
	private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

	/**
	 * Creates a new Sky object with the specified window dimensions.
	 * @param windowDimensions
	 * @return
	 */
	public static GameObject create(Vector2 windowDimensions){
		GameObject sky = new GameObject(Vector2.ZERO, windowDimensions,
				new RectangleRenderable(BASIC_SKY_COLOR));
		sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sky.setTag(ElementsTag.SKY.getTag()); // for debugging purposes
		return sky;
	}
}

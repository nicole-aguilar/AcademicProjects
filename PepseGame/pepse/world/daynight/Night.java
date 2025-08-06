package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.ElementsTag;

import java.awt.*;

/**
 * Create the night sky by creating a black block of the size of the window in front of the
 * camera, and obscure the rest of the objects in the game. it will have a transparency factor
 * where 0 is fully transparent at 12PM and 0.5F at 12AM.
 */
public class Night {
	/**max opaqueness of the night sky*/
	private static final float MIDNIGHT_OPACITY = 0.5f;
	/** min opaqueness of the night sky*/
	private static final float NOON_OPACITY = 0f;
	/** Color of the night sky, black. */
	private static final Color NIGHT_COLOR = Color.decode("#000000");

	/**
	 * Creates a GameObject representing the night sky according to windowDimensions, and change
	 * its transparency factor according to the cycleLength (number of seconds in a "day").
	 *
	 * @param windowDimensions The dimensions of the game window.
	 * @param cycleLength The length of the day-night cycle in seconds.
	 * @return A GameObject representing the night sky.
	 */
	public static GameObject create(Vector2 windowDimensions, float cycleLength){
		RectangleRenderable nightBox = new RectangleRenderable(NIGHT_COLOR);
		GameObject night = new GameObject(Vector2.ZERO,windowDimensions,nightBox);
		night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		night.setTag(ElementsTag.NIGHT.getTag());
		new Transition<Float>(
				night,
				night.renderer()::setOpaqueness,
				NOON_OPACITY,
				MIDNIGHT_OPACITY,
				Transition.CUBIC_INTERPOLATOR_FLOAT,
				cycleLength/2,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null);
		return night;
	}
}
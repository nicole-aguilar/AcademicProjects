package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.world.ElementsTag;

import java.awt.*;

/**
 * Class in charge of creating and behaviour if GameObject sun.
 */
public class Sun {
	/** Percentage of the smaller window edge used as the sun’s diameter. */
	private static final float SUN_SIZE_RATIO = 0.10f;
	/** y coordinate of the rotation centre as a ratio of window height. */
	private static final float CYCLE_CENTER_Y_RATIO = 2f / 3f;
	/** Initial y coordinate of the sun as a ratio of window height. */
	private static final float INITIAL_SUN_Y_RATIO = 1f / 4f;
	/** Start angle for the rotation transition. */
	private static final float ROTATION_START_DEG = 0f;
	/** End angle for the rotation transition. */
	private static final float ROTATION_END_DEG   = 360f;

	/**
	 * Creates a GameObject Sun, which will rotate in 360 degrees around a pre-determined center
	 * point
	 * @param windowDimensions the dimensions of the game window to determine position and size
	 *                           of sun
	 * @param cycleLength the length of the day-night cycle in seconds.
	 * @return GameObject representing the sun.
	 */
	public static GameObject create (Vector2 windowDimensions,float cycleLength){
		Vector2 cycleCenter = new Vector2(windowDimensions.x() * 0.5f,
				windowDimensions.y() * CYCLE_CENTER_Y_RATIO);
		Vector2 initialPos = new Vector2(windowDimensions.x() * 0.5f,
				windowDimensions.y() * INITIAL_SUN_Y_RATIO);
		float diameter = Math.min(windowDimensions.x(), windowDimensions.y()) * SUN_SIZE_RATIO;
		Vector2 sunSize   = new Vector2(diameter, diameter);
		GameObject sun = new GameObject( initialPos, sunSize, new OvalRenderable(Color.YELLOW));
		sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sun.setTag(ElementsTag.SUN.getTag());
		sunTransition(sun, cycleCenter, initialPos, cycleLength);
		return sun;
	}

	/**
	 * Creates a transition for the sun to rotate around a center point in a full circle.
	 * @param sun the GameObject representing the sun
	 * @param cycleCenter the center point around which the sun will rotate
	 * @param initialSunPosition the initial position of the sun
	 * @param cycleLength the length of the day-night cycle in seconds
	 */
	private static void sunTransition(GameObject sun, Vector2 cycleCenter,
									  Vector2 initialSunPosition, float cycleLength) {
		new Transition<Float>(
				sun,
				(Float angle) -> sun.setCenter(
						initialSunPosition.subtract(cycleCenter)
								.rotated(angle)
								.add(cycleCenter)
				),
				ROTATION_START_DEG,
				ROTATION_END_DEG,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				cycleLength,
				Transition.TransitionType.TRANSITION_LOOP,
				null
		);
	}
}
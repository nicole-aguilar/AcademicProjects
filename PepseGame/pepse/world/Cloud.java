package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import danogl.gui.ImageReader;


import java.awt.*;

/** * Represents a cloud in the game world that moves horizontally across the screen.
 * The cloud is created outside the left edge of the screen and moves to the right,
 * reappearing on the left side once it exits the right edge.
 */
public class Cloud {
	/** Path to the default cloud image. */
	private static final String CLOUD_IMAGE_PATH = "assets/cloud.png";
	/** Indicates whether the image has transparency. */
	private static final boolean HAS_ALPHA_PARAMETER = true;
	/** Time it takes for the cloud to cross the whole window in seconds. */
	private static final float CLOUD_MOVEMENT_SPEED = 10f;
	/** Base color of the cloud, used for rendering. */
	private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255);
	/** Size factor for the cloud, relative to the window dimensions. */
	private static final float CLOUD_SIZE_FACTOR = 0.2f;
	/** Padding for horizontal and vertical rain, so the rain is only in image seen. */
	private static final float HORIZONTAL_RAIN_PADDING = 40f;
	/** Padding for vertical rain, so the rain is only in image seen. */
	private static final float VERTICAL_RAIN_PADDING = 40f;

	/**
	 * The cloud is created OUTSIDE the left corner of the screen, and moves till reaching
	 * outside the screen on the right side. And the again (TRANSITION LOOP). Crosses THE WHOLE
	 * WINDOW IN 10 SECONDS. It doesn't disappear if it still in window view (if character moved
	 * it still in view), but it will be created again when it is outside the screen.
	 *
	 * @param windowDimensions the dimensions of the game window to determine position and size
	 * @param imageReader      the image reader used to load the cloud image.
	 * @return A GameObject representing the cloud, which moves horizontally across the screen.
	 */
	public static GameObject create(Vector2 windowDimensions, ImageReader imageReader) {

		Vector2 size = windowDimensions.mult(CLOUD_SIZE_FACTOR);
		Renderable art = buildRenderable(imageReader);
		Vector2 start = initialPosition(windowDimensions, size);
		GameObject cloud = new GameObject(start, size, art);
		cloud.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		cloud.setTag(ElementsTag.CLOUDS.getTag());
		addHorizontalLoop(cloud, windowDimensions.x());
		return cloud;
	}

	/**
	 * Returns the horizontal padding for rain, which is used to ensure that rain is only visible
	 * within the cloud image area.
	 *
	 * @return The horizontal padding for rain.
	 */
	public static float getHorizontalRainPadding() {
		return HORIZONTAL_RAIN_PADDING;
	}

	/**
	 * Returns the vertical padding for rain, which is used to ensure that rain is only visible
	 * within the cloud image area.
	 *
	 * @return The vertical padding for rain.
	 */
	public static float getVerticalRainPadding() {
		return VERTICAL_RAIN_PADDING;
	}

	/**
	 * Attempts to load the cloud texture; falls back to a plain white rectangle
	 * if the file is missing.
	 */
	private static Renderable buildRenderable(ImageReader reader) {
		try {
			return reader.readImage(CLOUD_IMAGE_PATH, HAS_ALPHA_PARAMETER);
		} catch (Exception e) {
			System.err.println("Could not load cloud image – using fallback: " + e.getMessage());
			return new RectangleRenderable(BASE_CLOUD_COLOR);
		}
	}

	/**
	 * Calculates the initial position of the cloud object based on the window dimensions
	 *
	 * @param windowDimensions the dimensions of the game window
	 * @param cloudDimension   the dimensions of the cloud object
	 * @return A Vector2 representing the initial position of the cloud
	 */
	private static Vector2 initialPosition(Vector2 windowDimensions, Vector2 cloudDimension) {
		float xCoordinate = -cloudDimension.x();
		float yCoordinate = windowDimensions.y() * CLOUD_SIZE_FACTOR;
		return new Vector2(xCoordinate, yCoordinate);
	}

	/**
	 * Adds a transition to the cloud object that moves it horizontally across the screen.
	 *
	 * @param cloud       The cloud GameObject to which the transition will be added.
	 * @param windowWidth The width of the game window, to determine the end position of the cloud.
	 */
	private static void addHorizontalLoop(GameObject cloud, float windowWidth) {
		float startX = cloud.getTopLeftCorner().x();
		float endX = windowWidth + cloud.getDimensions().x();
		new Transition<>(
				cloud,
				x -> cloud.setCenter(new Vector2(x, cloud.getCenter().y())),
				startX,
				endX,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				CLOUD_MOVEMENT_SPEED,
				Transition.TransitionType.TRANSITION_LOOP,
				null
		);
	}
}
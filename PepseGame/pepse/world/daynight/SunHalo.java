package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import pepse.world.ElementsTag;

import java.awt.*;

/**
 * Creates a GameObject sun halo, goes under the sun and follows its position.
 */
public class SunHalo {
	/** Color of the sun halo, A semi-transparent yellow. */
	private static final Color sunHaloColor = new Color(255,255,0,20);
	/** Renderer for the sun halo, semi-transparent yellow oval. */
	private static final OvalRenderable sunHaloRenderer = new OvalRenderable(sunHaloColor);
	/** Size of the sun halo relative to the sun size. */
	private static final float SUN_HALO_SIZE = 1.75f ;

	/**
	 * Creates a GameObject sun halo, which will follow the sun's position, and uses component to
	 * keep track of the movement
	 * @param sun the GameObject sun that follows
	 * @return GameObject representing the sun halo.
	 */
	public static GameObject create(GameObject sun){GameObject sunHalo = new GameObject(
				sun.getCenter(),
				sun.getDimensions().mult(SUN_HALO_SIZE),
			sunHaloRenderer
		);
		sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sunHalo.setTag(ElementsTag.SUN_HALO.getTag());
		sunHalo.addComponent((deltaTime)->sunHalo.setCenter(sun.getCenter()));
		return sunHalo;
	}
}

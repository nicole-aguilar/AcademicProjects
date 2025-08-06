package pepse;

import danogl.collisions.Layer;

/**
 * GameLayers is an enumeration that defines various layers used in the game not part of the
 * predetermined layers in danogl.
 */
public enum GameLayers {
	/**The SKY layer*/
	SKY(Layer.BACKGROUND),
	/** The CLOUDS layer */
	CLOUDS(14),
	/** The RAIN layer */
	RAIN(13),
	/** The SUN layer */
	SUN(-101),
	/** The SUN HALO layer */
	SUN_HALO(-102),
	/** The NIGHT layer */
	NIGHT(60);

	/**
	 * The layer ID associated with this GameLayer.
	 */
	private final int layer;
	/**
	 * Constructs a GameLayers instance with the specified layer ID.
	 * @param layer The layer ID to associate with this GameLayer.
	 */
	GameLayers(int layer) {
		this.layer = layer;
	}

	/**
	 * Returns the layer ID associated with this GameLayer.
	 * @return the layer ID as an integer.
	 */
	public int getLayer() {
		return layer;
	}
}

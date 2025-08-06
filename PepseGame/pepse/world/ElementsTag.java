package pepse.world;
/**
 * Enum representing various tags for game elements.
 * Each tag corresponds to a string identifier used in the game logic.
 */
public enum ElementsTag {
	/** Tag for the sky background. */
	SKY("sky"),

	/** Tag for the sun object. */
	SUN("sun"),

	/** Tag for the sun's halo effect. */
	SUN_HALO("sun-halo"),

	/** Tag for flora objects like trees. */
	FLORA("flora"),

	/** Tag for cloud objects. */
	CLOUDS("clouds"),

	/** Tag for the player avatar. */
	PLAYER("player"),

	/** Tag for ground blocks. */
	GROUND("ground"),

	/** Tag for grass-covered ground blocks. */
	GROUND_LAWN("ground_lawn"),

	/** Tag for background objects. */
	BACKGROUND("background"),

	/** Tag for the night overlay. */
	NIGHT("night"),

	/** Tag for tree trunks. */
	TRUNK("trunk"),
	/** Tag for tree leaf. */
	LEAF("leaf"),
	/** Tag for the fruit object. */
	FRUIT("fruit"),
	/** Tag for the Avatar*/
	AVATAR("avatar"),
	/** Tag to check if the fruit was eaten. */
	EATEN_FRUIT("eaten-fruit");

	/** The string representation of the tag. */
	private final String tag;

	/**
	 * Constructs an ElementsTag with its string identifier.
	 *
	 * @param tag The string representation of the tag.
	 */
	ElementsTag(String tag) {
		this.tag = tag;
	}

	/**
	 * Returns the string tag associated with the enum constant.
	 *
	 * @return The tag string.
	 */
	public String getTag() {
		return tag;
	}
	/**
	 * Returns the corresponding ElementsTag for a given string.
	 * If no match is found, returns null.
	 *
	 * @param tag the string representation to look for
	 * @return the matching ElementsTag or null
	 */
	public static ElementsTag fromString(String tag) {
		for (ElementsTag element : ElementsTag.values()) {
			if (element.getTag().equals(tag)) {
				return element;
			}
		}
		return null;
	}
}

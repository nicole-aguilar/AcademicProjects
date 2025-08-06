package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * It is responsible for the blocks that make up the terrain of the world and allows for other
 * objects to know what is the height of the terrain at a given X coordinate. It is NOT  a
 * gameObject itself, but is IN CHARGE of the blocks (a gameObject).
 */
public class Terrain {
	/** The height of the ground at x=0, calculated as 2/7 of the window height. */
	private final int groundHeightAtX0;
	/** The base color of the ground blocks. */
	private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
	/** The depth of the terrain, how many blocks deep it goes. */
	private static final int TERRAIN_DEPTH = 20;
	/** The factor to multiply noise with to create natural-looking terrain variations. */
	private static final int NOISE_FACTOR = 20;
	/** The noise generator used to create natural-looking terrain variations. */
	private final NoiseGenerator noiseGenerator;

	/**
	 * Constructs a Terrain object with the specified window dimensions and seed for the noise generator.
	 * The ground height at x=0 is set to 2/7 of the window height.
	 * @param windowDimensions The dimensions of the game window.
	 * @param seed The seed for the noise generator to ensure consistent terrain generation.
	 */
	public Terrain(Vector2 windowDimensions, int seed) {
		this.groundHeightAtX0 = (int) (windowDimensions.y() * 2f / 7f);
		this.noiseGenerator = new NoiseGenerator(seed, groundHeightAtX0);
	}

	/** Returns the height of the ground at the given x coordinate, which is calculated using a
	 * noise function.
	 * The height is based on the groundHeightAtX0 and the noise value at that x coordinate.
	 * @param x The x coordinate to get the ground height for.
	 * @return The height of the ground at the given x coordinate.
	 */
	public float groundHeightAt(float x) {
		return groundHeightAtX0 +
				(float) noiseGenerator.noise(x / 10f, Block.SIZE * NOISE_FACTOR);
	}

	/** Create a list of blocks representing the terrain in the given x-range.
	 * The range includes minX and maxX, and the blocks are created at intervals of Block.SIZE.
	 * @param minX The minimum x coordinate to consider.
	 * @param maxX The maximum x coordinate to consider.
	 * @return A list of blocks representing the terrain in the specified range.
	 */
	public List<Block> createInRange(int minX, int maxX) {
		List<Block> blocks = new ArrayList<>();
		int start = roundFloorBlock(minX);
		int end   = roundCeilBlock(maxX);
		for (int x = start; x <= end; x += Block.SIZE) {
			blocks.addAll(createColumn(x));
		}
		return blocks;
	}

	/** Create a column of blocks at the given x coordinate. */
	private List<Block> createColumn(int x) {
		List<Block> col = new ArrayList<>();
		int baseY = blockAlignedY(groundHeightAt(x));
		for (int i = 0; i < TERRAIN_DEPTH; i++) {
			int y = baseY + i * Block.SIZE;
			col.add(buildBlock(x, y, i == 0));
		}
		return col;
	}

	/** Creates a Single block at the given x coordinate, tags as a ground_lawn block or ground
	 * one. */
	private Block buildBlock(int x, int y, boolean top) {
		Renderable r = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
		Block b = new Block(new Vector2(x, y), r);
		b.setTag(top ? ElementsTag.GROUND_LAWN.getTag() : ElementsTag.GROUND.getTag());
		return b;
	}
	/** Returns the height of the ground at the given x coordinate, rounded to the nearest block
	 * size. */
	private int blockAlignedY(float h) {
		return (int) (Math.floor(h / Block.SIZE) * Block.SIZE);
	}
	/** Returns the x coordinate rounded down to the nearest block size. */
	private int roundFloorBlock(int x) {
		return (int) (Math.floor((float) x / Block.SIZE) * Block.SIZE);
	}
	/** Returns the x coordinate rounded up to the nearest block size. */
	private int roundCeilBlock(int x) {
		return (int) (Math.ceil((float) x / Block.SIZE) * Block.SIZE);
	}
}

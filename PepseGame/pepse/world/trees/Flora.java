package pepse.world.trees;

import danogl.GameObject;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Responsible for planting trees in the game world by interacting with the Terrain.
 * Each tree consists of a trunk and a set of randomly placed leaves.
 */
public class Flora {
    /**
     * The probability (between 0 and 1) that a tree will be planted at a given x-coordinate.
     * Used by Flora to randomly decide whether to generate a tree at each terrain position.
     */
    private static final double TREE_PROBABILITY = 0.1;
    /**min height for the random range*/
    private static final int MIN_HEIGHT = 3;
    /**max height for the random range*/
    private static final int MAX_HEIGHT = 6;
    /** Terrain used to determine ground height at each x-coordinate. */
    private final Terrain terrain;

    /** Random number generator initialized per x-coordinate using a seed. */
    private Random random;

    /** A callback function to trigger when a fruit is eaten. */
    private final Runnable callback;

    /** Seed used to generate reproducible random values per tree location. */
    private final int seed;

    /** The cycle duration after which fruits respawn. */
    private final float cycle;


    /**
     * Constructs a Flora generator with a reference to the terrain and a seed for randomness.
     * @param terrain The terrain used to determine ground height at each x position.
     * @param seed A seed for consistent random behavior.
     */
    public Flora(Terrain terrain, int seed,Runnable callback, float cycle) {
        this.terrain = terrain;
        //this.random = new Random(Objects.hash(60,seed));
        this.callback = callback;
        this.seed = seed;
        this.cycle = cycle;
    }

    /**
     * Creates trees at random positions within the given x-range.
     * @param minX The minimum x coordinate to consider.
     * @param maxX The maximum x coordinate to consider.
     * @return A list of GameObjects (trunks and leaves) representing all generated trees.
     */

    public List<GameObject> createInRange(int minX, int maxX) {
        List<GameObject> allTreeParts = new ArrayList<>();
        for (int x = minX; x <= maxX; x += Block.SIZE) {
            random = new Random(Objects.hash(x,seed));
            if (random.nextDouble() < TREE_PROBABILITY) {
                TreeCreator tree = createTreeAt(x);
                allTreeParts.addAll(tree.getTreeParts());
            }
        }
        return allTreeParts;
    }

    /**
     * Creates a single TreeCreator at the specified x-coordinate.
     * Computes the ground height and assigns a random height to the tree.
     *
     * @param x The x-coordinate where the tree should be placed.
     * @return A TreeCreator instance representing the new tree.
     */
    private TreeCreator createTreeAt(int x) {
        float groundY = terrain.groundHeightAt(x);
        int alignedY = (int)(Math.floor(groundY / Block.SIZE) * Block.SIZE);
        Vector2 treePosition = new Vector2(x, alignedY);
        int trunkHeight = random.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1) + MIN_HEIGHT;
        return new TreeCreator(treePosition, trunkHeight, random,callback,cycle);
    }
}
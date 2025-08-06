package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import pepse.world.ElementsTag;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * A factory class responsible for constructing a tree at a given location and height.
 * The tree consists of a trunk made of blocks and a crown made of randomly placed leaves and fruits.
 * The logic for creating the structure of the tree is encapsulated within this class,
 * while tree placement logic is handled externally (e.g., by Flora).
 */
public class TreeCreator {

    /** The base color of the trunk blocks. */
    private static final Color TRUNK_COLOR = new Color(100, 50, 20);

    /** The base color of the leaf blocks. */
    private static final Color LEAF_COLOR = new Color(50, 200, 30);

    /**
     * The probability (between 0 and 1) that a leaf will be placed in a crown position.
     * Used to sparsely populate the crown with leaves.
     */
    private static final double LEAF_PROBABILITY = 0.7;

    /**
     * The probability (between 0 and 1) that a fruit will be added to a leaf position.
     * Only evaluated if a leaf is placed.
     */
    private static final double FRUIT_PROBABILITY = 0.3;

    /** A list containing all parts of the tree (trunk, leaves, fruits). */
    private final ArrayList<GameObject> treeParts;

    /** The position (bottom of the trunk) where the tree will be created. */
    private final Vector2 position;

    /** The total number of trunk blocks to stack (i.e., tree height). */
    private final int height;

    /** Optional callback to execute when a fruit is eaten. */
    private final Runnable callback;

    /** The time in seconds until a fruit reappears after being consumed. */
    private final float cycle;

    /**
     * Constructs a new TreeCreator at a given position and height.
     * Immediately triggers the internal logic to generate all tree parts.
     *
     * @param position The bottom position where the tree will be placed.
     * @param height The number of trunk blocks (tree height).
     * @param random A Random object used to control leaf/fruit placement.
     * @param callback A callback function to assign to any generated fruit (may be null).
     * @param cycle Duration until fruits respawn after being eaten.
     */
    public TreeCreator(Vector2 position, int height, Random random, Runnable callback, float cycle) {
        this.position = position;
        this.height = height;
        this.callback = callback;
        this.treeParts = new ArrayList<>();
        this.cycle = cycle;
        generateTree(random);
    }

    /**
     * Generates the tree by creating both the trunk and crown.
     *
     * @param random Random generator for probabilistic placement.
     */
    private void generateTree(Random random) {
        createTrunk();
        createCrown(random);
    }

    /**
     * Creates the vertical trunk of the tree using colored blocks.
     */
    private void createTrunk() {
        for (int i = 0; i < height; i++) {
            Vector2 trunkPos = new Vector2(
                    position.x(),
                    position.y() - (i + 1) * Block.SIZE
            );
            GameObject trunk = new Block(
                    trunkPos,
                    new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR))
            );
            trunk.setTag(ElementsTag.TRUNK.getTag());
            treeParts.add(trunk);
        }
    }

    /**
     * Creates the tree's crown by attempting to place leaves and fruits
     * within a square region above the trunk top.
     *
     * @param random Random generator used to determine leaf/fruit placement.
     */
    private void createCrown(Random random) {
        float crownY = position.y() - (height + 1) * Block.SIZE;
        int crownSize = 2;
        for (int dx = -crownSize; dx <= crownSize; dx++) {
            for (int dy = -crownSize; dy <= crownSize; dy++) {
                if (random.nextFloat() < LEAF_PROBABILITY) {
                    Vector2 leafPos = new Vector2(
                            position.x() + dx * Block.SIZE,
                            crownY + dy * Block.SIZE
                    );

                    Leaf leaf = new Leaf(leafPos, LEAF_COLOR);
                    treeParts.add(leaf);

                    if (random.nextFloat() < FRUIT_PROBABILITY) {
                        Fruit fruit = new Fruit(leafPos, cycle);
                        if (callback != null) {
                            fruit.setCallback(callback);
                        }
                        treeParts.add(fruit);
                    }
                }
            }
        }
    }

    /**
     * Returns a list of all parts that make up the created tree.
     *
     * @return A new list containing all GameObjects of the tree.
     */
    public ArrayList<GameObject> getTreeParts() {
        return new ArrayList<>(treeParts);
    }
}

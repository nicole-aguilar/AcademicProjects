package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.ElementsTag;

import java.awt.*;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import java.util.Random;

/**
 * Represents a leaf in the game world.
 * Each leaf is a small square block that sways left and right to simulate wind.
 * The swaying starts after a random delay to create a more natural animation effect.
 */
public class Leaf extends GameObject {
    private static final float AMPLITUDE = 2f;
    private static final float WAVE_DURATION = 2f;
    private static final float MAX_DELAY = 2f;

    /** The original position of the leaf before waving begins. */
    private final Vector2 basePosition;

    /**
     * Constructs a new Leaf at the specified top-left corner with the given color.
     *
     * @param topLeftCorner The top-left corner where the leaf is placed.
     * @param color The color of the leaf.
     */
    public Leaf(Vector2 topLeftCorner, Color color) {
        super(topLeftCorner, Vector2.ONES.mult(Block.SIZE),
                new RectangleRenderable(color));
        setTag(ElementsTag.LEAF.getTag());
        transform().setVelocity(Vector2.ZERO);
        this.basePosition = topLeftCorner.getImmutableCopy();

        float delay = new Random().nextFloat() * MAX_DELAY;
        new ScheduledTask(this, delay, false, this::startWaving);
    }

    /**
     * Starts a waving animation by applying a horizontal transition motion
     * back and forth around the original X position of the leaf.
     */
    private void startWaving() {
        new Transition<>(
                this,
                offset -> setTopLeftCorner(new Vector2(basePosition.x() + offset, basePosition.y())),
                -AMPLITUDE,
                AMPLITUDE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                WAVE_DURATION,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }
}

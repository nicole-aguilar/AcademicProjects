package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.GameObjectPhysics;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import pepse.world.ElementsTag;

import java.awt.*;

/**
 * Represents a fruit that can be consumed by the Avatar for energy.
 * When eaten, the fruit disappears temporarily and respawns after a specified cycle duration.
 * The fruit class is decoupled from the Avatar and can trigger external behavior via an optional callback.
 */
public class Fruit extends GameObject {

    /** The default color of the fruit (red). */
    private static final Color DEFAULT_FRUIT_COLOR = new Color(255, 0, 0);

    /** The size of the fruit relative to a standard block (e.g., 0.8 * Block.SIZE). */
    private static final float FRUIT_SIZE_FACTOR = 0.8f;

    /** Opacity value representing full visibility (1.0 = fully visible). */
    private static final float FULL_OPAQUENESS = 1f;

    /** Opacity value representing full transparency (0.0 = fully invisible). */
    private static final float ZERO_OPAQUENESS = 0f;

    /** Mass value representing a non-physical or "disabled" state for the fruit. */
    private static final float ZERO_MASS = 0f;

    /** Dimensions representing a fully "disappeared" object (0 width and height). */
    private static final Vector2 ZERO_DIMENSIONS = Vector2.ZERO;

    /** A velocity vector representing a stationary object. */
    private static final Vector2 ZERO_VELOCITY = Vector2.ZERO;

    /** Duration (in seconds) before the fruit respawns after being eaten. */
    private final float cycle;

    /** Optional callback to be invoked when the fruit is eaten. */
    private Runnable onEatenCallback;

    /**
     * Constructs a fruit at the given location with the default color.
     *
     * @param topLeftCorner The top-left corner where the fruit is placed.
     * @param cycle The number of seconds until the fruit reappears after being eaten.
     */
    public Fruit(Vector2 topLeftCorner, float cycle) {
        this(topLeftCorner, DEFAULT_FRUIT_COLOR, cycle);
    }

    /**
     * Constructs a fruit at the given location with a specified color.
     *
     * @param topLeftCorner The top-left corner where the fruit is placed.
     * @param color The color of the fruit.
     * @param cycle The number of seconds until the fruit reappears after being eaten.
     */
    public Fruit(Vector2 topLeftCorner, Color color, float cycle) {
        super(
                topLeftCorner,
                Vector2.ONES.mult(Block.SIZE * FRUIT_SIZE_FACTOR),
                new OvalRenderable(ColorSupplier.approximateColor(color))
        );
        setTag(ElementsTag.FRUIT.getTag());
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        physics().preventIntersectionsFromDirection(ZERO_VELOCITY);
        transform().setVelocity(ZERO_VELOCITY);
        this.cycle = cycle;
    }

    /**
     * Sets a callback to be invoked when the fruit is eaten.
     *
     * @param callback A Runnable to execute upon consumption.
     */
    public void setCallback(Runnable callback) {
        this.onEatenCallback = callback;
    }

    /**
     * Handles collision with another GameObject.
     * If the other object is tagged as the Avatar, triggers the fruit-eating logic.
     *
     * @param other The other GameObject involved in the collision.
     * @param collision The collision object (not used in current logic).
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if (other.getTag().equals(ElementsTag.AVATAR.getTag())) {
            eatFruit();
        }
    }

    /**
     * Performs the actions required when the fruit is eaten:
     * makes it invisible, changes its tag, disables physics,
     * and schedules a respawn after the defined cycle time.
     */
    private void eatFruit() {
        renderer().setOpaqueness(ZERO_OPAQUENESS);
        setTag(ElementsTag.EATEN_FRUIT.getTag());
        physics().setMass(ZERO_MASS);
        setDimensions(ZERO_DIMENSIONS);
        if (onEatenCallback != null) {
            onEatenCallback.run();
        }
        new ScheduledTask(this, cycle, false, this::respawnFruit);
    }

    /**
     * Respawns the fruit by restoring its size, appearance, and physics properties.
     */
    private void respawnFruit() {
        setTag(ElementsTag.FRUIT.getTag());
        renderer().setOpaqueness(FULL_OPAQUENESS);
        setDimensions(Vector2.ONES.mult(Block.SIZE * FRUIT_SIZE_FACTOR));
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        transform().setVelocity(ZERO_VELOCITY);
    }
}

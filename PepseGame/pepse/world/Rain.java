package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.WindowController;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.AvatarObject.AvatarObserver;

import java.awt.Color;
import java.util.Random;

/**
 * Generates a raindrop GameObjects under a cloud.It picks a random amount of drops, distribute 
 * the drops horizontally under the cloud, and animates their fall and fade-out. And finally it 
 * removes them.
 */
public class Rain implements AvatarObserver {
    /** Minimum number of raindrops to generate in each update cycle */
    private static final int   MIN_DROPS = 9;
    /** Maximum number of raindrops to generate in each update cycle */
    private static final int   MAX_DROPS = 12;
    /** Width of each raindrop in pixels */
    private static final float DROP_WIDTH = 20f;
    /** Height of each raindrop in pixels */
    private static final float DROP_HEIGHT = 20f;
    /** Speed at which raindrops fall across the screen */
    private static final float FALL_SPEED = 2f;
    /** Duration in seconds for raindrops to fade out completely */
    private static final float FADE_OUT_TIME = 0.5f;
    /** Color of raindrops with alpha transparency */
    private static final Color RAIN_COLOR = new Color(17, 66, 255, 128);
    /** Maximum horizontal movement range for raindrops during falling animation */
    private static final float HORIZONTAL_MOVEMENT_RANGE = 50f;
    /** Offset value used to center the horizontal movement calculation */
    private static final float MOVEMENT_CENTER_OFFSET = 0.5f;
    /** Starting opacity value for raindrops */
    private static final float INITIAL_OPACITY = 1f;
    /** Final opacity value for raindrops (fully transparent) */
    private static final float FINAL_OPACITY = 0f;
    /** Vertical variation factor for raindrop starting positions */
    private static final float Y_VARIATION_FACTOR = 3.5f;
    /** Layer in which raindrops are rendered */
    private final int  rainLayer;
    /** Cloud object from which rain drops */
    private final GameObject cloud;
    /** Controller for accessing window properties */
    private final WindowController windowController;
    /** Collection to which raindrop objects are added and removed */
    private final GameObjectCollection gameObjects;
    /** Random number generator for deterministic raindrop behavior */
    private final Random random;
    /** Horizontal padding from cloud edges for raindrop generation */
    private final float horizontalPadding;
    /** Vertical padding from cloud bottom for raindrop generation */
    private final float verticalPadding;


    /**
     * Creates a Rain object that spawns raindrops under a specified cloud.
     * @param cloud             Cloud object whose bounds define the spawn area.
     * @param windowController  For retrieving window dimensions.
     * @param seed              Seed for deterministic randomness (same cloud → same rain).
     * @param gameObjects       Collection where drops are added / removed.
     * @param rainLayer         Rendering layer for the drops.
     * @param horizontalPadding Horizontal distance kept from cloud's left/right edges.
     * @param verticalPadding   Vertical distance kept from cloud's bottom.
     */
    public Rain(GameObject cloud,
                WindowController windowController,
                int seed,
                GameObjectCollection gameObjects,
                int rainLayer,
                float horizontalPadding,
                float verticalPadding) {

        this.cloud = cloud;
        this.windowController = windowController;
        this.random = new Random(seed);
        this.gameObjects = gameObjects;
        this.rainLayer = rainLayer;
        this.horizontalPadding = horizontalPadding;
        this.verticalPadding = verticalPadding;
    }

    /** 
     * Called once per frame by the game loop.
     */
    @Override
    public void update() {
        createMultipleDrops();
    }

    /** 
     * Decides a random amount of drops to be created under the cloud horizontally.
     */
    private void createMultipleDrops() {
        int amount = random.nextInt(MAX_DROPS - MIN_DROPS + 1) + MIN_DROPS;
        for (int i = 0; i < amount; i++) {
            createSingleDrop(calculateCloudUnderSpace());
        }
    }

    /**
     * Returns a screen position inside the padded rectangle under the cloud.
     */
    private Vector2 calculateCloudUnderSpace() {
        float cloudWidth = cloud.getDimensions().x() - 2 * horizontalPadding;
        Vector2 cloudTopLeft = cloud.getTopLeftCorner().add(new Vector2(horizontalPadding, 0));
        float cloudBottomY = cloudTopLeft.y() + cloud.getDimensions().y();
        float x = cloudTopLeft.x() + random.nextFloat() * cloudWidth;
        float yVariation = DROP_HEIGHT * Y_VARIATION_FACTOR;
        float y = cloudBottomY - DROP_HEIGHT - verticalPadding - random.nextFloat() * yVariation;
        return new Vector2(x, y);
    }

    /**
     * Creates a single raindrop GameObject, giving it a fall and fade transitions,
     * and adds it to the GameObjectCollection.
     *
     * @param position Top-left corner where the drop should appear.
     */
    private void createSingleDrop(Vector2 position) {
        GameObject drop = new GameObject(
                position,
                new Vector2(DROP_WIDTH, DROP_HEIGHT),
                new RectangleRenderable(RAIN_COLOR)
        );
        drop.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        fallingAnimation(drop, position);
        fadingAnimation(drop);

        gameObjects.addGameObject(drop, rainLayer);
    }

    /**
     * In charge of horizontal sway + vertical fall animation
     */
    private void fallingAnimation(GameObject drop, Vector2 startPos) {
        float xOffset = (random.nextFloat() - MOVEMENT_CENTER_OFFSET) * HORIZONTAL_MOVEMENT_RANGE;
        float targetY = startPos.y() + windowController.getWindowDimensions().y();

        new Transition<>(
                drop,
                progress -> {
                    float x = startPos.x() + xOffset * progress;
                    float y = startPos.y() + (targetY - startPos.y()) * progress;
                    drop.setCenter(new Vector2(x, y));
                },
                0f,
                1f,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                FALL_SPEED,
                Transition.TransitionType.TRANSITION_ONCE,
                null
        );
    }

    /**
     * Fades out the drop by animating its opacity from 1 to 0.
     */
    private void fadingAnimation(GameObject drop) {
        new Transition<>(
                drop,
                drop.renderer()::setOpaqueness,
                INITIAL_OPACITY,
                FINAL_OPACITY,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                FADE_OUT_TIME,
                Transition.TransitionType.TRANSITION_ONCE,
                () -> gameObjects.removeGameObject(drop, rainLayer)
        );
    }
}
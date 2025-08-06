package pepse.world;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;
import pepse.world.AvatarObject.AvatarAnimation;
import pepse.world.AvatarObject.AvatarObserver;
import pepse.world.AvatarObject.Energy.EnergyCallBack;
import pepse.world.AvatarObject.Energy.EnergyManager;
import pepse.world.AvatarObject.Movements;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents the main player-controlled character (Avatar) in the game world.
 *
 * The Avatar responds to user keyboard input and can perform actions such as:
 * - Running left or right
 * - Jumping (if enough energy is available)
 * - Idling when no movement is detected
 *
 * The Avatar integrates animation handling for different movement states,
 * applies gravity, handles collisions with the ground, and interacts with an
 * energy management system via callbacks.
 *
 * Key responsibilities include:
 * - Reading user input to determine motion
 * - Switching animations based on movement type (run, idle, jump)
 * - Managing physics properties such as velocity and collisions
 * - Notifying listeners (observers) on jump events
 * - Updating the energy system using a provided callback interface
 *
 * The Avatar does not manage its own energy directly, but reports movement
 * events to an external {@link EnergyManager} via an {@link EnergyCallBack}.
 */

public class Avatar extends GameObject  {
    /** Path to the default avatar image (idle state). */
    private static final String IMAGE_PATH = "assets/idle_0.png";

    /** Indicates whether the image has transparency (alpha channel). */
    private static final boolean HAS_ALPHA_PARAMETER = true;

    /** Basic block size in the game – used for consistent scaling. */
    private static final int BLOCK_SIZE = 30;

    /** Avatar height in terms of block units – avatar is twice as tall as a block. */
    public static final float AVATAR_HEIGHT_UNITS = 2f;
    /** Dimensions of the avatar – width of one block and height of two blocks. */
    private static final Vector2 AVATAR_SIZE = new Vector2(BLOCK_SIZE, BLOCK_SIZE * AVATAR_HEIGHT_UNITS);
    /** Horizontal movement speed of the avatar. */
    private static final float VELOCITY_X = 200;

    /** Vertical velocity applied when the avatar jumps. */
    private static final float VELOCITY_Y = -500;

    /** Constant acceleration downwards (gravity). */
    private static final float GRAVITY = 500;
    /**the imge side  - if true left else right*/
    public static final boolean LEFT = true;

    /** Listener for keyboard inputs – used to control avatar movement. */
    private final UserInputListener inputListener;
    /** Handles loading and managing the avatar's animation frames. */
    private final AvatarAnimation animation;

    /** Animation to display when the avatar is running. */
    private final AnimationRenderable runAnimation;

    /** Animation to display when the avatar is idle (not moving). */
    private final AnimationRenderable idleAnimation;

    /** Animation to display when the avatar jumps. */
    private final AnimationRenderable jumpAnimation;

    /** List of listeners to be notified when the avatar jumps. */
    private final List<AvatarObserver> jumpListeners = new ArrayList<>();

    /** Callback interface to notify when the avatar moves, used for energy updates. */
    private EnergyCallBack energyCallback;

    /** Reference to the energy manager that tracks and updates the avatar's energy. */
    private final EnergyManager energyManager;

    /**
     * Constructs a new avatar at the given top-left position.
     *
     * @param topLeftCorner  The position to place the avatar.
     * @param inputListener  Input listener for handling keyboard events.
     * @param imageReader    Used to load the avatar's image.
     */
    public Avatar(Vector2 topLeftCorner,
                  UserInputListener inputListener,
                  ImageReader imageReader,EnergyManager energyManager) {
        super(topLeftCorner,AVATAR_SIZE, imageReader.readImage(IMAGE_PATH,HAS_ALPHA_PARAMETER));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
        this.animation = new AvatarAnimation(imageReader);
        this.runAnimation = animation.createAnimation(Movements.RUN);
        this.idleAnimation = animation.createAnimation(Movements.IDLE);
        this.jumpAnimation = animation.createAnimation(Movements.JUMP);
        this.energyManager = energyManager;
        setTag(ElementsTag.AVATAR.getTag());

    }
    /**
     * Handles collision with other game objects.
     * If colliding with a block, stops the avatar's vertical movement.
     *
     * @param other     The other game object involved in the collision.
     * @param collision The collision information.
     */    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        //TODO - ENUM
        if(other.getTag().equals(ElementsTag.GROUND_LAWN.getTag())
                ||other.getTag().equals(ElementsTag.GROUND.getTag())) {

            Vector2 normal = collision.getNormal();
            if (normal.y() < 0) {
                this.transform().setVelocityY(0);

                float blockTop = other.getTopLeftCorner().y();
                float avatarBottom = getTopLeftCorner().y() + getDimensions().y();

                if (avatarBottom > blockTop) {
                    float correction = blockTop - avatarBottom;
                    Vector2 currentPos = getTopLeftCorner();
                    transform().setTopLeftCorner(new Vector2(currentPos.x(), currentPos.y() + correction));
                }
            }
        }
    }
    /**
     * Registers a listener to be notified when the avatar jumps.
     *
     * @param listener The listener to add.
     */
    public void addJumpListener(AvatarObserver listener) {
        jumpListeners.add(listener);
    }
    /**
     * Notifies all registered listeners that a jump event occurred.
     */
    public  void JumpEvent() {
        for (AvatarObserver listener : jumpListeners) {
            listener.update();
        }
    }

    /**
     * Sets a callback to be triggered upon avatar movement.
     * The callback informs another component (e.g., energy system)
     * about the avatar's action (jump, run, idle).
     *
     * @param callback A callback implementing {@link EnergyCallBack}.
     */
    public void setEnergyCallback(EnergyCallBack callback) {
        this.energyCallback = callback;
    }
    /**
     * Handles avatar movement and animation based on user input.
     * Sends a callback to update energy according to the performed action
     * (run, jump, idle), only if enough energy is available.
     *
     * @param deltaTime Time elapsed since the last frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        boolean mooving = false;
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)&&energyManager.hasAnyEnergy()){
            xVel -= VELOCITY_X;
            run(LEFT);
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)&&energyManager.hasAnyEnergy()){
            xVel += VELOCITY_X;
            run(!LEFT);
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&energyManager.hasEnoughEnergyForJump()){
            mooving = jumpMove();
        }
        if (!mooving && !inputListener.isKeyPressed(KeyEvent.VK_SPACE)&&
                isOnGround()) {
            renderer().setRenderable(idleAnimation);
            if (energyCallback != null){
                energyCallback.onAvatarMoved(Movements.IDLE);
            }
        }
        transform().setVelocityX(xVel);
    }
    /**treat jump move in update*/
    private boolean jumpMove() {
        boolean mooving;
        transform().setVelocityY(VELOCITY_Y);
        renderer().setRenderable(jumpAnimation);
        JumpEvent();
        mooving = true;
        if (energyCallback != null){
            energyCallback.onAvatarMoved(Movements.JUMP);
        }
        return mooving;
    }
    /**treat run move in update*/
    private void run(boolean left) {
        renderer().setRenderable(runAnimation);
        renderer().setIsFlippedHorizontally(left);
        if (energyCallback != null){
            energyCallback.onAvatarMoved(Movements.RUN);
        }
    }
    /**check if the avatar is on the ground*/
    private boolean isOnGround() {
        return getVelocity().y() >= -1 && getVelocity().y() <= 1;
    }

}
package pepse.world.AvatarObject;
/**
 * Interface for objects that observe the avatar's actions.
 * Typically used to react to specific events such as a jump.
 */
public interface AvatarObserver {
    /**
     * Called when the observed event (e.g., jump) occurs.
     */
    public void update();
}

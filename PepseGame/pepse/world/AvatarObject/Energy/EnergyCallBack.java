package pepse.world.AvatarObject.Energy;

import pepse.world.AvatarObject.Movements;

/**
 * An interface for handling energy updates in response to avatar movement.
 * Implementers of this interface define how energy should be modified
 * based on the type of movement the avatar performs.
 */
public interface EnergyCallBack {
    /**
     * Called when the avatar performs a movement action.
     * This method allows updating the energy level accordingly.
     *
     * @param movement The movement action performed by the avatar
     *                 (e.g., JUMP, RUN, IDLE).
     */
    void onAvatarMoved(Movements movement);

}

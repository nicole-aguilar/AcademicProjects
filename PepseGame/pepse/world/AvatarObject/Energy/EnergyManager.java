package pepse.world.AvatarObject.Energy;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Manages the Avatar's energy level and displays it on the screen.
 *
 * The energy level is visually shown as a percentage, and is updated
 * whenever energy increases or decreases. This class also provides
 * utility methods for checking whether the Avatar can perform actions
 * (such as jumping or running), based on the current energy value.
 *
 * Energy is clamped between {@code MIN_ENERGY} and {@code MAX_ENERGY}.
 * The energy display uses camera coordinates and appears fixed on screen.
 */
public class EnergyManager {

    /** Maximum energy value (full energy). */
    private static final float MAX_ENERGY = 100;

    /** Minimum energy value (depleted energy). */
    private static final float MIN_ENERGY = 0;

    /** Minimum energy required to allow jumping. */
    private static final float ENERGY_REQUIRED_FOR_JUMP = 10;

    /** Width of the energy display box. */
    private static final float DISPLAY_WIDTH = 140;

    /** Height of the energy display box. */
    private static final float DISPLAY_HEIGHT = 30;

    /** Empty string constant, used to initialize text. */
    private static final String EMPTY_STRING = "";

    /** Format string for the energy percentage displayed on screen. */
    public static final String ENERGY_GRAPHIC = "Energy: %.0f%%";

    /** The game object that displays the energy text on screen. */
    private final GameObject energyDisplay;

    /** The text renderer responsible for drawing the energy string. */
    private final TextRenderable textRenderable;

    /** Current energy value, clamped between MIN_ENERGY and MAX_ENERGY. */
    private float energy;

    /**
     * Constructs a new EnergyManager and places the energy display at the given position.
     *
     * @param position The top-left corner position for the energy display.
     */
    public EnergyManager(Vector2 position) {
        Vector2 size = new Vector2(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        this.textRenderable = new TextRenderable(EMPTY_STRING);
        this.textRenderable.setColor(Color.BLACK);
        this.energyDisplay = new GameObject(position, size, textRenderable);
        this.energy = MAX_ENERGY;
        energyDisplay.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        updateText();
    }

    /**
     * Increases the current energy by a given amount, up to the maximum.
     *
     * @param amount The amount of energy to restore.
     */
    public void increaseEnergy(float amount) {
        energy = Math.min(MAX_ENERGY, energy + amount);
        updateText();
    }

    /**
     * Decreases the current energy by a given amount, down to the minimum.
     *
     * @param amount The amount of energy to reduce.
     */
    public void decreaseEnergy(float amount) {
        energy = Math.max(MIN_ENERGY, energy - amount);
        updateText();
    }

    /**
     * Updates the on-screen text to reflect the current energy value.
     */
    private void updateText() {
        textRenderable.setString(ENERGY_GRAPHIC.formatted(energy));
    }

    /**
     * Returns the GameObject representing the on-screen energy display.
     *
     * @return A GameObject that can be added to the game world.
     */
    public GameObject getDisplay() {
        return energyDisplay;
    }

    /**
     * Returns the current energy value.
     *
     * @return A float between 0 and 100 representing energy.
     */
    public float getEnergy() {
        return energy;
    }

    /**
     * Returns whether the Avatar has enough energy to perform a jump.
     *
     * @return True if energy is at least the required jump threshold, false otherwise.
     */
    public boolean hasEnoughEnergyForJump() {
        return energy >= ENERGY_REQUIRED_FOR_JUMP;
    }

    /**
     * Returns whether the Avatar has any remaining energy at all.
     *
     * @return True if energy is greater than 0, false otherwise.
     */
    public boolean hasAnyEnergy() {
        return energy > 0;
    }
}

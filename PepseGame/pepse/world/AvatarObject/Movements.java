package pepse.world.AvatarObject;

/**
 * Enum representing possible avatar movements.
 * Each movement has an associated string label.
 */
public enum Movements {
    /** Avatar is jumping. */
    JUMP("jump"),

    /** Avatar is running. */
    RUN("run"),

    /** Avatar is idle (not moving). */
    IDLE("idle");

    /** String representation of the movement. */
    private final String move;

    /**
     * Constructs a movement enum with its string representation.
     *
     * @param move The string representation of the movement.
     */
    Movements(String move){
        this.move = move;
    }

    /**
     * Returns the string representation of the movement.
     *
     * @return The movement string.
     */
    public String getMove(){
        return move;
    }
}

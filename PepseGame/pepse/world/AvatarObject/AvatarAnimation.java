package pepse.world.AvatarObject;

import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A  class for creating avatar animations based on movement type (RUN, IDLE, UP).
 * Uses enum Movements for type safety and clarity.
 */
public class AvatarAnimation  {
    /**Path to image assets */
    private static final String ASSETS_PATH = "assets/";
    /**Whether the images have transparency*/
    private static final boolean HAS_ALPHA = true;
    /**Duration per frame in seconds*/
    private static final float FRAME_DURATION = 0.2f;
    /**image reader*/
    private final ImageReader imageReader;

    /**
     * Constructs a new AvatarAnimation with the given image reader.
     * @param imageReader Utility to read image files.
     */
    public AvatarAnimation(ImageReader imageReader) {
        this.imageReader = imageReader;
    }

    /**
     * Creates an animation for a given movement using the Movements enum.
     * @param movement Type of movement (RUN, IDLE, UP).
     * @return AnimationRenderable based on the movement type.
     */
    public  AnimationRenderable createAnimation(Movements movement) {
        String prefix = movement.getMove() + "_";
        List<Renderable> frames = loadFrames(prefix, ASSETS_PATH);

        return new AnimationRenderable(frames.toArray(new Renderable[0]), FRAME_DURATION);
    }

    /**
     * Loads image frames from the assets folder that start with the given prefix.
     * Frames are sorted alphabetically to ensure correct animation order.
     * @param prefix Filename prefix (e.g., "run_", "idle_", etc.)
     * @return List of Renderable image frames.
     */
    private  List<Renderable> loadFrames(String prefix, String assetsPath) {
        File folder = new File(assetsPath);
        File[] matchingFiles = folder.listFiles((dir, name) ->
                name.startsWith(prefix));

        List<Renderable> frames = new ArrayList<>();
        if (matchingFiles == null) return frames;

        Arrays.sort(matchingFiles, Comparator.comparing(File::getName));

        for (File file : matchingFiles) {
            frames.add(imageReader.readImage(file.getPath(), HAS_ALPHA));
        }

        return frames;
    }
}

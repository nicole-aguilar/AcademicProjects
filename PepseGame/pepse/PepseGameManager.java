package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.AvatarObject.AvatarObserver;
import pepse.world.AvatarObject.Energy.EnergyManager;
import pepse.world.trees.Flora;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 * Manages the main game logic and initialization of the Pepse platformer.
 * This includes creating the sky, terrain, avatar, and flora (trees),
 * as well as configuring game layers and input.
 */

public class PepseGameManager extends GameManager {
	/** The score awarded for each jump action performed by the avatar. */
	private static final int JUMP_SCORE = 10;
	/** Seed used for deterministic randomness in tree generation. */
	private static final int SEED = 3;
	/** Desired frame rate for the game loop. */
	private static final int TARGET_FRAMERATE = 60;
	/** Duration of a full day-night cycle (in seconds). */
	private static final float CYCLE_LENGTH = 30f;
	/** Energy deducted from avatar while running. */
	private static final float RUN_SCORE = 0.5f;
	/** Energy gained by avatar while idle. */
	private static final int IDLE_SCORE = 1;
	/** Energy gained by eating one fruit. */
	private static final int FRUIT_ENERGY = 10;
	/** Time (in seconds) it takes for fruit to respawn. */
	private static final int CYCLE = 30;
	/** The player's avatar character. */
	private Avatar avatar;
	/** Terrain manager responsible for generating ground blocks. */
	private Terrain terrain;
	/** Leftmost boundary of currently loaded terrain. */
	private int screenStart = (int)Vector2.ZERO.x();
	/** Rightmost boundary of currently loaded terrain. */
	private int screenEnd;
	/** Controller for window management (dimensions, framerate, etc). */
	private WindowController windowController;
	/** Flora generator that places trees, leaves, and fruits in the world. */
	private Flora flora;
	/** Extra radius around the avatar for terrain generation. */
	private static final int EXTRA_TERRAIN_RADIUS = Block.SIZE * 40;
	/** Extra distance from the avatar to delete distant terrain and flora. */
	private static final int EXTRA_DELETE = Block.SIZE *  4;
	/** The start of the range of terrain that has been loaded. */
	private int loadedStart;
	/** The end of the range of terrain that has been loaded. */
	private int loadedEnd;

	/**
	 * Initializes the game world, including terrain, avatar, and trees.
	 * This method sets up the rendering layers and physics interactions between game objects.
	 *
	 * @param imageReader       Reader to load images for rendering.
	 * @param soundReader       Reader to load sound assets (unused here).
	 * @param inputListener     Listener for user input events (keyboard).
	 * @param windowController  Controller for window size and display control.
	 */
	@Override
	public void initializeGame(ImageReader imageReader, SoundReader soundReader,
							   UserInputListener inputListener, WindowController windowController) {
		this.windowController = windowController;
		windowController.setTargetFramerate(TARGET_FRAMERATE);
		screenEnd = (int) windowController.getWindowDimensions().x();
		super.initializeGame(imageReader, soundReader, inputListener, windowController);
		loadedStart = floorToGrid(screenStart);
		loadedEnd   = ceilToGrid (screenEnd);
		createSky(windowController);
		this.terrain = new Terrain(windowController.getWindowDimensions(), SEED);
		List<Block> grounds = terrain.createInRange((int)Vector2.ZERO.x(),
				(int) windowController.getWindowDimensions().x());
		int windowWidth = (int) windowController.getWindowDimensions().x();
		createGroundBlocks(grounds);
		GameObject cloud = createClouds(windowController, imageReader);
		AvatarObserver rain = createRain(cloud, windowController);
		//add energy and avatar
		Vector2 topLeftEnergyPosition = new Vector2(10, 10);
		EnergyManager energyManager = new EnergyManager(topLeftEnergyPosition);
		this.flora = new Flora(terrain, SEED,() -> energyManager.increaseEnergy(FRUIT_ENERGY),
				CYCLE);
		this.avatar =  createAvatar(imageReader, inputListener, grounds, windowWidth,energyManager
				,rain);
		setCamera(new Camera(avatar, Vector2.ZERO,
				windowController.getWindowDimensions(),
				windowController.getWindowDimensions()));
		createTrees((int)Vector2.ZERO.x(),
				(int)windowController.getWindowDimensions().x());
		//Makes sure the avatar clashes with the static layer
		gameObjects().layers().shouldLayersCollide(Layer.DEFAULT, Layer.STATIC_OBJECTS, true);
		createDayNight(windowController);
		createEnergyLevel(avatar,energyManager);
	}
	/**
	 * Creates and adds cloud object to the game for background decoration.
	 *
	 * @param windowController The controller for window dimensions.
	 * @param imageReader      Used to load the cloud image.
	 */
	private GameObject createClouds(WindowController windowController, ImageReader imageReader) {
		GameObject skyCloud = Cloud.create(windowController.getWindowDimensions(),
				imageReader);
		gameObjects().addGameObject(skyCloud, GameLayers.CLOUDS.getLayer());
		return skyCloud;
	}

	/**
	 * Creates and adds the energy display to the UI layer,
	 * and sets the avatar's energy update callback.
	 *
	 * @param avatar           The player avatar.
	 * @param energyManager    Manages the avatar's energy state.
	 */
	private void createEnergyLevel(Avatar avatar,
								   EnergyManager energyManager) {
		gameObjects().addGameObject(energyManager.getDisplay(), Layer.UI);
		avatar.setEnergyCallback(movement -> {
			switch (movement) {
				case JUMP :
					energyManager.decreaseEnergy(JUMP_SCORE);
					break;
				case RUN :
					energyManager.decreaseEnergy(RUN_SCORE);
					break;
				case IDLE :
					energyManager.increaseEnergy(IDLE_SCORE);
					break;
			}
		});
	}

	/**
	 * Creates a Rain object associated with the given cloud and window controller.
	 * @param cloud The cloud GameObject that the rain will be associated with.
	 * @param windowController The controller for window dimensions.
	 * @return A Rain object that handles rain rendering and behavior.
	 */
	private Rain createRain(GameObject cloud, WindowController windowController) {
		return new Rain(cloud, windowController, SEED, gameObjects(), GameLayers.RAIN.getLayer(),
				Cloud.getHorizontalRainPadding(), Cloud.getVerticalRainPadding());
	}

	/**
	 * Adds ground blocks to the static game layer.
	 *
	 * @param grounds A list of terrain blocks to add to the game.
	 */
	private void createGroundBlocks(List<Block> grounds) {
		for (Block block : grounds) {
			gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);

		}
	}

	/**
	 * Creates and adds the static sky background to the game.
	 *
	 * @param windowController The controller for window dimensions.
	 */
	private void createSky(WindowController windowController) {
		GameObject sky = Sky.create(windowController.getWindowDimensions());
		gameObjects().addGameObject(sky, GameLayers.SKY.getLayer());
	}

	/**
	 * Creates the day-night cycle elements including sun, night overlay, and sun halo.
	 *
	 * @param windowController The controller for window dimensions.
	 */
	private void createDayNight(WindowController windowController) {
		GameObject night = pepse.world.daynight.Night.create(windowController.getWindowDimensions(),
				CYCLE_LENGTH);
		GameObject sun = pepse.world.daynight.Sun.create(windowController.getWindowDimensions(),
				CYCLE_LENGTH);
		GameObject sunHalo = pepse.world.daynight.SunHalo.create(sun);
		gameObjects().addGameObject(night, GameLayers.NIGHT.getLayer()); // night  layer
		gameObjects().addGameObject(sunHalo, GameLayers.SUN_HALO.getLayer());
		gameObjects().addGameObject(sun, GameLayers.SUN.getLayer()); // sun layer
	}

	/**
	 * Creates trees in the specified screen range and adds them to the game objects.
	 * @param screenStart The starting x-coordinate of the screen range.
	 * @param screenEnd The ending x-coordinate of the screen range.
	 */
	private void createTrees(int screenStart, int screenEnd) {
		List<GameObject> trees = flora.createInRange(screenStart, screenEnd);
		for (GameObject treePart : trees) {
			ElementsTag tag = ElementsTag.fromString(treePart.getTag());
			if (tag != null) {
				switch (tag){
					case TRUNK :
						gameObjects().addGameObject(treePart, Layer.STATIC_OBJECTS);
						break;
					case LEAF :
						gameObjects().addGameObject(treePart, Layer.DEFAULT);
						break;
					case FRUIT:
						gameObjects().addGameObject(treePart, Layer.DEFAULT);
						break;

				}
			}
		}
	}

	/**
	 * Creates the avatar character for the player, initializing its position and input handling.
	 *
	 * @param imageReader      Reader to load images for rendering the avatar.
	 * @param inputListener    Listener for user input events (keyboard).
	 * @param grounds          List of ground blocks in the game world.
	 * @param windowWidth      The width of the game window, used to determine starting position.
	 * @param energyManager    Manages the avatar's energy state.
	 * @param avatarObserver   Observer for avatar events (e.g., jumping).
	 * @return The created Avatar object.
	 */
	private Avatar createAvatar(ImageReader imageReader, UserInputListener inputListener,
								List<Block> grounds, int windowWidth, EnergyManager energyManager,
								AvatarObserver avatarObserver) {
		Block startingBlock = findStartingBlock(grounds, windowWidth);
		Vector2 avatarPos = computeAvatarStartPos(startingBlock);
		Avatar avatar = new Avatar(avatarPos, inputListener, imageReader, energyManager);
		avatar.addJumpListener(avatarObserver);
		setCameraForAvatar(avatar, avatarPos);
		gameObjects().addGameObject(avatar, Layer.DEFAULT);
		return avatar;
	}

	/**
	 * Finds the starting block for the avatar based on the grounds list.
	 * The starting block is the first ground lawn block that is within the window width.
	 *
	 * @param grounds      The list of ground blocks in the game world.
	 * @param windowWidth  The width of the game window, used to filter blocks.
	 * @return The first ground lawn block that is within the window width.
	 */
	private Block findStartingBlock(List<Block> grounds, int windowWidth) {
		return grounds.stream()
				.filter(b -> ElementsTag.GROUND_LAWN.getTag().equals(b.getTag()))
				.filter(b -> {
					float x = b.getTopLeftCorner().x();
					return x >= 0 && x <= windowWidth;
				})
				.min(Comparator.comparing(b -> b.getTopLeftCorner().y()))
				.orElseThrow();
	}

	/**
	 * Computes the starting position for the avatar based in the top left corner
	 * of the starting block, adjusting for the avatar's height.
	 *
	 * @param startingBlock The block where the avatar will start.
	 * @return A Vector2 representing the computed starting position for the avatar.
	 */
	private Vector2 computeAvatarStartPos(Block startingBlock) {
		Vector2 blockPos = startingBlock.getTopLeftCorner();
		float avatarH = Block.SIZE * Avatar.AVATAR_HEIGHT_UNITS;
		return new Vector2(blockPos.x(), blockPos.y() - avatarH);
	}

	/**
	 * Sets the camera to follow the avatar, centering it in the window.
	 * The camera's initial position is set based on the avatar's position
	 * and the window dimensions.
	 *
	 * @param avatar     The avatar to follow with the camera.
	 * @param avatarPos  The position of the avatar in the game world.
	 */
	private void setCameraForAvatar(Avatar avatar, Vector2 avatarPos) {
		Vector2 initPos = windowController.getWindowDimensions()
				.mult(0.5f)
				.subtract(avatarPos);
		setCamera(new Camera(avatar,
				initPos,
				windowController.getWindowDimensions(),
				windowController.getWindowDimensions()));
	}

	/**
	 * Checks the screen bounds and creates or removes terrain and flora as needed.
	 */
	private void checkScreenBounds() {
		int centerX   = (int) avatar.getCenter().x();
		int wantStart = floorToGrid(centerX - EXTRA_TERRAIN_RADIUS);
		int wantEnd   = ceilToGrid (centerX + EXTRA_TERRAIN_RADIUS);
		//Right side
		for (int x = loadedEnd; x < wantEnd; x += Block.SIZE) {
			generateColumn(x);
		}
		//Left side
		for (int x = loadedStart - Block.SIZE; x >= wantStart; x -= Block.SIZE) {
			generateColumn(x);
		}
		loadedStart = wantStart;
		loadedEnd   = wantEnd;
	}

	/**
	 * Generates a column of terrain and flora at the specified x-coordinate.
	 * This includes creating ground blocks and trees if needed
	 *
	 * @param x The x-coordinate where the column should be generated.
	 */
	private void generateColumn(int x) {
		int colEnd = x + Block.SIZE;
		List<Block> blocks = terrain.createInRange(x, colEnd);
		createGroundBlocks(blocks);
		createTrees(x, colEnd);
	}

	/**
	 * Rounds the given x-coordinate down to the nearest grid position.
	 * @param x the x coordinate to round down
	 * @return the x coordinate rounded down to the nearest grid position
	 */
	private static int floorToGrid(int x) {
		return (int) Math.floor((float) x / Block.SIZE) * Block.SIZE;
	}

	/**
	 * Rounds the given x-coordinate up to the nearest grid position.
	 * @param x the x coordinate to round up
	 * @return  the x coordinate rounded up to the nearest grid position
	 */
	private static int ceilToGrid(int x) {
		return (int) Math.ceil ((float) x / Block.SIZE) * Block.SIZE;
	}

	/**
	 * Removes game objects that are too far from the avatar's current position.
	 */
	private void removeDistantObjects() {
		int cullLeft  = loadedStart - EXTRA_DELETE;
		int cullRight = loadedEnd   + EXTRA_DELETE;
		List<GameObject> toRemove = new ArrayList<>();
		for (GameObject obj : gameObjects()) {
			ElementsTag tag = ElementsTag.fromString(obj.getTag());
			if (tag == null || !isRemovableTag(tag)) continue;
			float x = obj.getTopLeftCorner().x();
			if (x < cullLeft || x > cullRight) {
				toRemove.add(obj);
			}
		}
		// remove after iteration
		for (GameObject obj : toRemove) {
			ElementsTag tag = ElementsTag.fromString(obj.getTag());
			int layer = isStaticTag(tag) ? Layer.STATIC_OBJECTS : Layer.DEFAULT;
			gameObjects().removeGameObject(obj, layer);
		}
	}

	/**
	 * Checks if the given tag represents a game object that can be removed.
	 * Removable tags include ground, ground lawn, trunk, leaf, and fruit.
	 *
	 * @param tag The ElementsTag to check.
	 * @return true if the tag is removable, false otherwise.
	 */
	private boolean isRemovableTag(ElementsTag tag) {
		return tag == ElementsTag.GROUND_LAWN ||
				tag == ElementsTag.GROUND ||
				tag == ElementsTag.TRUNK ||
				tag == ElementsTag.LEAF ||
				tag == ElementsTag.FRUIT;
	}

	/**
	 * Checks if the given tag represents a static object that should not be removed.
	 * Static tags include trunk, ground, and ground lawn.
	 *
	 * @param tag The ElementsTag to check.
	 * @return true if the tag is static, false otherwise.
	 */
	private boolean isStaticTag(ElementsTag tag) {
		return tag == ElementsTag.TRUNK ||
				tag == ElementsTag.GROUND ||
				tag == ElementsTag.GROUND_LAWN;
	}

	/**
	 * Updates the game state, checking screen bounds and removing distant objects.
	 * This method is called every frame to ensure the game world remains responsive
	 * and only contains relevant objects within the camera's view.
	 *
	 * @param deltaTime The time elapsed since the last update, used for smooth animations.
	 */
	@Override
	public void update(float deltaTime) {
		checkScreenBounds();
		removeDistantObjects();
		super.update(deltaTime);
	}

	/**
	 * Main method to launch the game.
	 * @param args Command-line arguments (unused).
	 */
	public static void main(String[] args){
		new PepseGameManager().run();
	}
}
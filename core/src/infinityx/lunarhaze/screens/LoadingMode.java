package infinityx.lunarhaze.screens;
/*
 * LoadingMode.java
 *
 * Asset loading is a really tricky problem.  If you have a lot of sound or images,
 * it can take a long time to decompress them and load them into memory.  If you just
 * have code at the start to load all your assets, your game will look like it is hung
 * at the start.
 *
 * The alternative is asynchronous asset loading.  In asynchronous loading, you load a
 * little bit of the assets at a time, but still animate the game while you are loading.
 * This way the player knows the game is not hung, even though he or she cannot do
 * anything until loading is complete. You know those loading screens with the inane tips
 * that want to be helpful?  That is asynchronous loading.
 *
 * This player mode provides a basic loading screen.  While you could adapt it for
 * between level loading, it is currently designed for loading all assets at the
 * start of the game.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import infinityx.assets.AssetDirectory;
import infinityx.lunarhaze.graphics.GameCanvas;
import infinityx.util.ScreenObservable;

/**
 * Class that provides a loading screen for the state of the game.
 */
public class LoadingMode extends ScreenObservable implements Screen {

    /**
     * Track the current state of the loading screen.
     */
    private enum LoadingState {
        FADE_IN, LOAD, FADE_OUT
    }

    private LoadingState loadingState;

    // There are TWO asset managers.  One to load the loading screen.  The other to load the assets
    /**
     * Internal assets for this loading screen
     */
    private final AssetDirectory internal;
    /**
     * The actual assets to be loaded
     */
    private final AssetDirectory assets;
    /**
     * Background texture for start-up
     */
    private final Texture background;
    /**
     * Game Title texture
     */
    private final Texture game_title;
    /**
     * Loading text texture
     */
    private final Texture loading_text;
    /**
     * Moonphase texture
     */
    private final Texture moonphase;
    /**
     * Animation used for the moonphase
     */
    Animation<TextureRegion> moonAnimation; // Must declare frame type (TextureRegion)
    /**
     * A variable used to track the time for animation
     */
    float stateTime;
    /**
     * Loading Text Height Ration
     */
    private static final float TEXT_HEIGHT_RATIO = 0.08f;
    /**
     * Default height ratio for the moon
     */
    private static final float HEIGHT_RATIO_MOON = 0.15f;
    /**
     * Default height ratio for the game title
     */
    private static final float HEIGHT_RATIO_TITLE = 0.58f;
    /**
     * Default budget for asset loader (do nothing but load 60 fps)
     */
    private static final int DEFAULT_BUDGET = 15;
    /**
     * Standard window size (for scaling)
     */
    private static final int STANDARD_WIDTH = 2560;
    /**
     * Standard window height (for scaling)
     */
    private static final int STANDARD_HEIGHT = 1600;

    /**
     * Reference to GameCanvas created by the root
     */
    private final GameCanvas canvas;

    /**
     * Scaling factor for when the resolution changes.
     */
    private float scale;

    /**
     * Current progress (0 to 1) of the asset manager
     */
    private float progress;
    /**
     * The amount of time to devote to loading assets (as opposed to on screen hints, etc.)
     */
    private int budget;

    /**
     * current time (in seconds) this screen has been alive
     */
    private float elapsed;

    /**
     * time (in seconds) it should take this screen to fade-in and fade-out
     */
    private static final float FADE_TIME = 1.75f;

    /**
     * Easing in function, easing out is reversed
     */
    private static final Interpolation EAS_FN = Interpolation.exp5Out;

    /**
     * alpha tint, rgb should be 1 as we are only changing transparency
     */
    private final Color alphaTint = new Color(1, 1, 1, 0);

    /**
     * Lobby and pause background music
     */
    private Music lobby_background;

    /**
     * Already started lobby background
     */
    private boolean set_lobby = false;


    /**
     * Returns the budget for the asset loader.
     * <p>
     * The budget is the number of milliseconds to spend loading assets each animation
     * frame.  This allows you to do something other than load assets.  An animation
     * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to
     * do something else.  This is how game companies animate their loading screens.
     *
     * @return the budget in milliseconds
     */
    public int getBudget() {
        return budget;
    }

    /**
     * Sets the budget for the asset loader.
     * <p>
     * The budget is the number of milliseconds to spend loading assets each animation
     * frame.  This allows you to do something other than load assets.  An animation
     * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to
     * do something else.  This is how game companies animate their loading screens.
     *
     * @param millis the budget in milliseconds
     */
    public void setBudget(int millis) {
        budget = millis;
    }

    /**
     * Returns the asset directory produced by this loading screen
     * <p>
     * This asset loader is NOT owned by this loading scene, so it persists even
     * after the scene is disposed.  It is your responsbility to unload the
     * assets in this directory.
     *
     * @return the asset directory produced by this loading screen
     */
    public AssetDirectory getAssets() {
        return assets;
    }

    /**
     * Creates a LoadingMode with the default budget, size and position.
     *
     * @param file   The asset directory to load in the background
     * @param canvas The game canvas to draw to
     */
    public LoadingMode(String file, GameCanvas canvas) {
        this(file, canvas, DEFAULT_BUDGET);
    }

    /**
     * Creates a LoadingMode with the default size and position.
     * <p>
     * The budget is the number of milliseconds to spend loading assets each animation
     * frame.  This allows you to do something other than load assets.  An animation
     * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to
     * do something else.  This is how game companies animate their loading screens.
     *
     * @param file   The asset directory to load in the background
     * @param canvas The game canvas to draw to
     * @param millis The loading budget in milliseconds
     */
    public LoadingMode(String file, GameCanvas canvas, int millis) {
        this.canvas = canvas;
        budget = millis;

        // Compute the dimensions from the canvas
        resize(canvas.getWidth(), canvas.getHeight());

        // We need these files loaded immediately
        internal = new AssetDirectory("loading.json");
        internal.loadAssets();
        internal.finishLoading();

        // Load the next two images immediately.
        background = internal.getEntry("background", Texture.class);
        background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        moonphase = internal.getEntry("moonphase", Texture.class);
        loading_text = internal.getEntry("loadtext", Texture.class);
        game_title = internal.getEntry("title", Texture.class);


        // No progress so far.
        progress = 0;

        // Start loading the real assets
        assets = new AssetDirectory(file);
        assets.loadAssets();

        loadingState = LoadingState.FADE_IN;
        create();
    }

    /**
     * Called when this screen should release all resources.
     */
    public void dispose() {
        internal.unloadAssets();
        internal.dispose();
        moonphase.dispose();
    }

    /**
     * Update the status of this player mode.
     * <p>
     * We prefer to separate update and draw from one another as separate methods, instead
     * of using the single render() method that LibGDX does.  We will talk about why we
     * prefer this in lecture.
     *
     * @param delta Number of seconds since last animation frame
     */
    private void update(float delta) {
        stateTime += delta;
//        if (!set_lobby) {
//            lobby_background.setLooping(true);
//            lobby_background.play();
//            set_lobby = true;
//        }
        switch (loadingState) {
            case FADE_IN:
                elapsed += delta;
                // progress along fade-in
                float inProg = Math.min(1f, elapsed / FADE_TIME);
                alphaTint.a = EAS_FN.apply(inProg);
                if (inProg == 1f) {
                    loadingState = LoadingState.LOAD;
                    elapsed = 0;
                }
                break;
            case LOAD:
                if (progress < 1.0f) {
                    // Begin loading assets
                    assets.update(budget);
                    this.progress = assets.getProgress();
                    if (progress >= 1.0f) {
                        this.progress = 1.0f;
                        loadingState = LoadingState.FADE_OUT;
                    }
                }
                break;
            case FADE_OUT:
                elapsed += delta;
                // progress along fade-out
                float outProg = Math.min(1f, elapsed / FADE_TIME);
                alphaTint.a = EAS_FN.apply(1 - outProg);
                if (outProg == 1f) {
                    observer.exitScreen(this, 0);
                }
                break;
        }

    }

    /**
     * Draw the status of this player mode.
     * <p>
     * We prefer to separate update and draw from one another as separate methods, instead
     * of using the single render() method that LibGDX does.  We will talk about why we
     * prefer this in lecture.
     */
    private void draw(float delta) {
        canvas.clear(Color.BLACK);
        canvas.beginUI(GameCanvas.DrawPass.SPRITE);

        switch (loadingState) {
            case FADE_OUT:
                drawBackground(canvas);
            case FADE_IN:
                drawBackground(canvas);
                break;
            case LOAD:
                drawBackground(canvas);
                break;
        }

        TextureRegion currentFrame = moonAnimation.getKeyFrame(stateTime, true);
        canvas.draw(
                currentFrame, alphaTint,
                currentFrame.getRegionWidth() / 2, currentFrame.getRegionHeight() / 2,
                canvas.getWidth() / 2, canvas.getHeight() * HEIGHT_RATIO_MOON,
                0f,
                0.3f * scale, 0.3f * scale
        );
        canvas.end();
    }

    private void drawBackground(GameCanvas canvas) {
        canvas.drawOverlay(background, alphaTint, true);
        canvas.draw(game_title, alphaTint, game_title.getWidth() / 2, game_title.getHeight() / 2,
                canvas.getWidth() / 2, canvas.getHeight() * HEIGHT_RATIO_TITLE, 0, 0.45f * scale, 0.45f * scale);
        canvas.draw(
                loading_text, alphaTint,
                loading_text.getWidth() / 2, 0,
                canvas.getWidth() / 2, canvas.getHeight() * TEXT_HEIGHT_RATIO,
                0, 0.5f * scale, 0.5f * scale
        );
    }

    // ADDITIONAL SCREEN METHODS

    /**
     * Called when the Screen should render itself.
     * <p>
     * We defer to the other methods update() and draw().  However, it is VERY important
     * that we only quit AFTER a draw.
     *
     * @param delta Number of seconds since last animation frame
     */
    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }

    /**
     * Split the sprite sheet and generate animation frames
     */
    public void create() {
        TextureRegion[][] moonTextures = TextureRegion.split(moonphase, moonphase.getWidth() / 36,
                moonphase.getHeight() / 1);
        TextureRegion[] moonFrames = new TextureRegion[36 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 36; j++) {
                moonFrames[index++] = moonTextures[i][j];
            }
        }
        moonAnimation = new Animation<TextureRegion>(0.13f, moonFrames);
        stateTime = 0f;
    }

    /**
     * Called when the Screen is resized.
     * <p>
     * This can happen at any point during a non-paused state but will never happen
     * before a call to show().
     *
     * @param width  The new width in pixels
     * @param height The new height in pixels
     */
    public void resize(int width, int height) {
        float sx = ((float) width) / STANDARD_WIDTH;
        float sy = ((float) height) / STANDARD_HEIGHT;
        scale = (sx < sy ? sx : sy);
    }

    /**
     * Called when the Screen is paused.
     * <p>
     * This is usually when it's not active or visible on screen. An Application is
     * also paused before it is destroyed.
     */
    public void pause() {
        // TODO Auto-generated method stub

    }

    /**
     * Called when the Screen is resumed from a paused state.
     * <p>
     * This is usually when it regains focus.
     */
    public void resume() {
        // TODO Auto-generated method stub

    }

    /**
     * Called when this screen becomes the current screen for a Game.
     */
    public void show() {
        elapsed = 0f;
    }

    /**
     * Called when this screen is no longer the current screen for a Game.
     */
    public void hide() {
    }
}
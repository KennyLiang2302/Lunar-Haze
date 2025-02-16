/*
 * SoundBuffer.java
 *
 * This interface exposes more of the OpenAL interface to the user to improve audio
 * options.  In particular, it adds a callback function to Sound (to complement the
 * one that exists for Music).
 *
 * @author Walker M. White
 * @date   4/15/20
 */
package infinityx.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

/**
 * This interface represents playback for an in-memory audio asset.
 * <p>
 * As a subinterface of {@link Sound}, this supports pausing, resuming and so on,
 * even on multiple simultaneous instances.  It also adds support for a callback
 * function (see {@link OnCompletionListener}) when an instance has
 * stopped playing.
 * <p>
 * The audio data being play is fully loaded into memory, so this interface is
 * primarily for short clips. While there is no upper limit on the audio file size,
 * you should avoid using this method for any sound asset greater than 1 MB.
 * <p>
 * Sound buffer instances are created via {@link AudioEngine#newSound}. When you
 * are done with using the sound buffer instance you have to dispose it via the
 * {@link #dispose()} method.
 * <p>
 * This interface is not even remotely thread-safe.  LibGDX requires that all audio
 * interfaces be interacted with in the main application thread (for much the same
 * reason that OpenGL requires this).
 */
public interface SoundBuffer extends Sound {

    /**
     * The interface for a callback invoked as a sound instance is completed
     * <p>
     * This callback is essentially the same as{@link Music.OnCompletionListener}.
     * It only differs in the fact that the sound object may have multiple instances
     * playing simultaneously, so it must specify the particular instance that has
     * just ended.
     */
    interface OnCompletionListener {
        /**
         * Called when the end of a music stream is reached during playback.
         *
         * @param buffer   The sound buffer that finished playing
         * @param instance The particular instance that has completed
         */
        void onCompletion(SoundBuffer buffer, long instance);
    }

    /**
     * Registers a callback invoked as a sound instance completes.
     *
     * @param listener The callback that will be run.
     */
    void setOnCompletionListener(OnCompletionListener listener);


    // #mark -
    // #mark Source Attributes

    /**
     * Returns the file that generated this asset.
     * <p>
     * The currently supported formats are WAV, MP3 and OGG.
     *
     * @return the file that generated this asset.
     */
    FileHandle getFile();

    /**
     * Returns true if this is a mono audio asset.
     * <p>
     * LibGDX only supports mono and stereo audio assets. It does not support
     * complex multi-channel assets (such as 7.1 surround).
     *
     * @return true if this is a mono audio asset.
     */
    boolean isMono();

    /**
     * Returns the number of audio samples (per channel) per second.
     *
     * @return the number of audio samples (per channel) per second.
     */
    int getSampleRate();

    /**
     * Returns the duration of this audio asset in seconds.
     *
     * @return the duration of this audio asset in seconds.
     */
    float getDuration();

    // #mark -
    // #mark Playback Control

    /**
     * Returns true if the given instance is actively playing
     * <p>
     * The sound id should be one given by {@link #play()} or {@link #play(float)}.
     * If the sound id is not valid, this method returns false.
     *
     * @param soundId The playback instance
     * @return true if the given instance is actively playing
     */
    boolean isPlaying(long soundId);

    /**
     * Returns the volume of the given instance
     * <p>
     * The sound id should be one given by {@link #play()} or {@link #play(float)}.
     * If the sound id is not valid, this method returns -1.
     *
     * @param soundId The playback instance
     * @return the volume of the given instance
     */
    float getVolume(long soundId, float volume);

    /**
     * Returns the loop setting of the given instance
     * <p>
     * The sound id should be one given by {@link #play()} or {@link #play(float)}.
     * If the sound id is not valid, this method returns false.
     *
     * @param soundId The playback instance
     * @return the loop setting of the given instance
     */
    boolean getLooping(long soundId, boolean looping);

    /**
     * Returns the pitch of the given instance
     * <p>
     * The pitch multiplier is value between 0.5 and 2.0, where 1 is unchanged,
     * >1 is faster, and <1 is slower.
     * <p>
     * The sound id should be one given by {@link #play()} or {@link #play(float)}.
     * If the sound id is not valid, this method returns 1.
     *
     * @param soundId The playback instance
     * @return the pitch of the given instance
     */
    float getPitch(long soundId);

    /**
     * Sets the pan of the given instance
     * <p>
     * The pan is a value -1 to 1.  The value 0 is the default center position. -1
     * is full left and 1 is full right.
     * <p>
     * The sound id should be one given by {@link #play()} or {@link #play(float)}.
     * If the sound id is not valid, this method has no effect.
     *
     * @param soundId The playback instance
     * @param pan     The pan value -1 (left) to 1 (right). Use 0 for center.
     */
    void setPan(long soundId, float pan);

    /**
     * Returns the pan value of the given instance
     * <p>
     * The pan is a value -1 to 1.  The value 0 is the default center position. -1
     * is full left and 1 is full right.
     * <p>
     * The sound id should be one given by {@link #play()} or {@link #play(float)}.
     * If the sound id is not valid, this method returns 0.
     *
     * @param soundId The playback instance
     * @return the pan value of the given instance
     */
    float getPan(long soundId);

    /**
     * Returns the current position of this instance in seconds.
     * <p>
     * The sound id should be one given by {@link #play()} or {@link #play(float)}.
     * If the sound id is not valid, this method returns -1.
     *
     * @param soundId The playback instance
     * @return the current position of this instance in seconds.
     */
    float getPosition(long soundId);

    /**
     * Sets the current position of this instance in seconds.
     * <p>
     * The sound id should be one given by {@link #play()} or {@link #play(float)}.
     * If the sound id is not valid, this method has no effect.  A value outside
     * of the bounds of the assets sets the position to the appropriate end point.
     *
     * @param soundId The playback instance
     * @param seconds The current position of this instance in seconds.
     */
    void setPosition(long soundId, float seconds);

}

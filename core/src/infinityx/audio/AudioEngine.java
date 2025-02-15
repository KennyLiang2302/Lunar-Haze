/*
 * AudioEngine.java
 *
 * This interface is an extension of the Audio interface that exposes more OpenAL
 * functionality to the student.
 *
 * @author Walker M. White
 * @date   4/15/20
 */
package infinityx.audio;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * This interface provides an audio controller with more functionality than {@link Audio}.
 * <p>
 * In particular, this interface allows the user to create {@link AudioSource} objects
 * that are not explicitly attached to a {@link Sound} or {@link Music} asset.  It also
 * provides explicit access to the {@link SoundEffect} and {@link MusicQueue} interfaces.
 * <p>
 * As with {@link Audio}{, all All resources created via this interface have to be
 * disposed as soon as they are no longer used.
 * <p>
 * Despite what {@link Audio} claims, no sound instances (not even {@link Music}) are
 * paused when the application is minimized (it was not clear that this was respected
 * in all instances of class Audio). It is your responsibility to {@link #pause()}
 * the engine from the appropriate {@link ApplicationListener}.
 */
public interface AudioEngine extends Audio {
    /**
     * Returns the number of simultaneous sound sources supported by this audio engine.
     * <p>
     * Possible simultaneous sound sources include instances of {@link SoundEffect},
     * {@link MusicQueue}, and {@link AudioDevice}.
     */
    int getCapacity();

    /**
     * Creates a new {#link AudioSource} from the given file.
     * <p>
     * A sample is a music asset that is not explicitly associated with the audio engine.
     * You can read data directly and pass it to an {@link AudioDevice}. Alternatively,
     * you can queue the sample on to a {@link MusicQueue} to support gapless
     * transitions in your music.
     * <p>
     * The currently supported formats are WAV, MP3 and OGG.
     * <p>
     * The audio source should be disposed if it is no longer used via the
     * {@link AudioSource#dispose()} method.
     *
     * @param file The sound asset
     * @return a new {#link Sample} from the given file.
     * @throws GdxRuntimeException if the asset could not be loaded
     */
    AudioSource newSource(FileHandle file);

    /**
     * Creates a new {@link SoundEffect} which to play back audio effects.
     * <p>
     * Sound buffers should be used for low latency effects such as gun shots or
     * explosions. The audio data is retrieved from the file specified and loaded
     * fully into memory. While there is no upper limit on the audio file size, you
     * should avoid using this method for any sound asset greater than 1 MB.
     * <p>
     * The currently supported formats are WAV, MP3 and OGG.
     * <p>
     * The sound buffer should be disposed if it is no longer used via the
     * {@link SoundEffect#dispose()} method.
     *
     * @param file The sound asset
     * @return a new {#link SoundBuffer} from the given file.
     * @throws GdxRuntimeException if the asset could not be loaded
     */
    SoundEffect newSound(FileHandle file);

    /**
     * Creates a new {@link SoundEffect} which to play back audio effects.
     * <p>
     * Sound buffers should be used for low latency effects such as gun shots or
     * explosions. The audio data is retrieved from the file specified and loaded
     * fully into memory. While there is no upper limit on the audio file size, you
     * should avoid using this method for any sound asset greater than 1 MB.
     * <p>
     * The currently supported formats are WAV, MP3 and OGG.
     * <p>
     * The sound buffer should be disposed if it is no longer used via the
     * {@link SoundEffect#dispose()} method.
     *
     * @param source The sound asset
     * @return a new {#link SoundBuffer} from the given audio source.
     */
    SoundEffect newSoundBuffer(AudioSource source);

    /**
     * Creates a new {@link MusicQueue} to stream from the given file.
     * <p>
     * A music buffer streams music from the sound asset without fully loading it into
     * memory. This is idea for long running music. The currently supported formats are
     * WAV, MP3 and OGG.
     * <p>
     * It is possible to append additional {@link AudioSource} instances to a music
     * buffer. Doing so creates gapless playback from one music track to another. All
     * sources added to this buffer must have the same sample rate and audio channels
     * (mono or stereo) as the original.
     * <p>
     * Despite what {@link Audio} claims, no sound instances (not even {@link Music})
     * are paused when the application is minimized (it was not clear that this was
     * respected in all instances of class Audio). It is your responsibility to
     * {@link #pause()} the engine from the appropriate {@link ApplicationListener}.
     *
     * @param file The sound asset
     * @return a new {#link MusicBuffer} from the given file.
     * @throws GdxRuntimeException if the asset could not be loaded
     */
    MusicQueue newMusic(FileHandle file);

    /**
     * Creates a new {@link MusicQueue} with the given properties.
     * <p>
     * A music buffer streams music from the sound asset without fully loading it into
     * memory. This is idea for long running music. The currently supported formats are
     * WAV, MP3 and OGG.
     * <p>
     * This music asset starts out with no contents and so playing it will not produce
     * any sound. To create music, you should append additional {@link AudioSource}
     * instances to a music buffer. Doing so creates gapless playback from one music
     * track to another. All sources added to this buffer must have the same sample
     * rate and audio channels (mono or stereo) as this buffer.
     * <p>
     * Despite what {@link Audio} claims, no sound instances (not even {@link Music})
     * are paused when the application is minimized (it was not clear that this was
     * respected in all instances of class Audio). It is your responsibility to
     * {@link #pause()} the engine from the appropriate {@link ApplicationListener}.
     *
     * @param isMono     Whether this is a mono stream (as opposed to stereo)
     * @param sampleRate The fixed sample rate of this stream
     * @return a new {#link MusicBuffer} with the given properties.
     */
    MusicQueue newMusicBuffer(boolean isMono, int sampleRate);

    /**
     * Pauses all sound instances associated with this audio engine.
     * <p>
     * This will pause everything, and not just music.  This is the method that
     * should be called when your application is minimized.
     */
    void pause();

    /**
     * Pauses all sound instances previously paused.
     * <p>
     * This will only resume sound instances that were paused by the global {@link #pause()}
     * method.  Sound instances paused via their own local pause interface will not be
     * affected.
     */
    void resume();

    /**
     * Gets the EffectFactory for creating {@link EffectFilter}
     *
     * @return EffectFactory class for generating sound filters
     */
    EffectFactory getEffectFactory();
}

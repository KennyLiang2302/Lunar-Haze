package infinityx.lunarhaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;

public class GameSetting {
    //TODO: NOT FINISH YET
    private static final String MUSIC_ENABLED = "music_enabled";
    private static final String MUSIC_VOLUME = "music_volume";
    private static final String SOUND_ENABLED = "sound_enabled";
    private static final String SOUND_VOLUME = "sound_volume";
    private static final String MOVE_UP = "move_up";
    private static final String SETTING_NAME = "default";
    private Preferences preferences;

    public GameSetting() {
        preferences = Gdx.app.getPreferences(SETTING_NAME);
        if (!preferences.contains(MUSIC_ENABLED)) {
            setMusicEnabled(true);
        }
        if (!preferences.contains(MUSIC_VOLUME)) {
            setMusicVolume(1.0f);
        }
        if (!preferences.contains(SOUND_ENABLED)) {
            setSoundEnabled(true);
        }
        if (!preferences.contains(MOVE_UP)) {
            setBinding(MOVE_UP, Keys.W);
        }
    }

    protected Preferences getPrefs() {
        return Gdx.app.getPreferences(SETTING_NAME);
    }

    public boolean isMusicEnabled() {
        return getPrefs().getBoolean(MUSIC_ENABLED, true);
    }

    public void setMusicEnabled(boolean state) {
        getPrefs().putBoolean(MUSIC_ENABLED, state);
        getPrefs().flush();
    }

    public float getMusicVolume() {
        return getPrefs().getFloat(MUSIC_VOLUME, 1.0f);
    }

    public void setMusicVolume(float volume) {
        getPrefs().putFloat(MUSIC_VOLUME, volume);
        getPrefs().flush();
    }

    public boolean isSoundEnabled() {
        return getPrefs().getBoolean(SOUND_ENABLED, true);
    }

    public void setSoundEnabled(boolean state) {
        getPrefs().putBoolean(SOUND_ENABLED, state);
        getPrefs().flush();
    }

    public float getSoundVolume() {
        return getPrefs().getFloat(SOUND_VOLUME, 1.0f);
    }

    public void setSoundVolume(float volume) {
        getPrefs().putFloat(SOUND_VOLUME, volume);
        getPrefs().flush();
    }

    public int getBindingUp() {
        return getPrefs().getInteger(MOVE_UP, Keys.W);
    }

    public void setBinding(String key, int keycode) {
        getPrefs().putInteger(key, keycode);
        getPrefs().flush();
    }
}

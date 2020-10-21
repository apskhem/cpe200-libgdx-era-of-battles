package coma.game.controllers;

import com.badlogic.gdx.audio.Sound;

public class AudioController {
    public static void PlayAndSetVolume(final Sound sound, final float volume) {


        sound.setVolume(sound.play(), volume);
    }
}

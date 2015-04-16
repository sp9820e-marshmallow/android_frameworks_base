/*
 * Copyright 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.media;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import android.annotation.IntDef;

/**
 * Structure for common playback settings.
 *
 * Used by {@link AudioTrack} {@link AudioTrack#getPlaybackSettings()} and
 * {@link AudioTrack#setPlaybackSettings(PlaybackSettings)}
 * to control playback behavior.
 * <p> <strong>audio fallback mode:</strong>
 * select out-of-range parameter handling.
 * <ul>
 * <li> {@link PlaybackSettings#AUDIO_FALLBACK_MODE_DEFAULT}:
 *   System will determine best handling. </li>
 * <li> {@link PlaybackSettings#AUDIO_FALLBACK_MODE_MUTE}:
 *   Play silence for settings normally out of range.</li>
 * <li> {@link PlaybackSettings#AUDIO_FALLBACK_MODE_FAIL}:
 *   Return {@link java.lang.IllegalArgumentException} from
 *   <code>AudioTrack.setPlaybackSettings(PlaybackSettings)</code>.</li>
 * </ul>
 * <p> <strong>audio stretch mode:</strong> select
 * timestretch handling.
 * <ul>
 * <li> {@link PlaybackSettings#AUDIO_STRETCH_MODE_DEFAULT}:
 *   System will determine best selection. </li>
 * <li> {@link PlaybackSettings#AUDIO_STRETCH_MODE_VOICE}:
 *   Content is primarily voice.</li>
 * </ul>
 * <p> <strong>pitch:</strong> increases or decreases the tonal frequency of the audio content.
 * It is expressed as a multiplicative factor, where normal pitch is 1.0f.
 * <p> <strong>speed:</strong> increases or decreases the time to
 * play back a set of audio or video frames.
 * It is expressed as a multiplicative factor, where normal speed is 1.0f.
 * <p> Different combinations of speed and pitch may be used for audio playback;
 * some common ones:
 * <ul>
 * <li> <em>Pitch equals 1.0f.</em> Speed change will be done with pitch preserved,
 * often called <em>timestretching</em>.</li>
 * <li> <em>Pitch equals speed.</em> Speed change will be done by <em>resampling</em>,
 * similar to {@link AudioTrack#setPlaybackRate(int)}.</li>
 * </ul>
 */
public final class PlaybackSettings {
    /** @hide */
    @IntDef(
        value = {
                AUDIO_FALLBACK_MODE_DEFAULT,
                AUDIO_FALLBACK_MODE_MUTE,
                AUDIO_FALLBACK_MODE_FAIL,
        }
    )
    @Retention(RetentionPolicy.SOURCE)
    public @interface AudioFallbackMode {}
    public static final int AUDIO_FALLBACK_MODE_DEFAULT = 0;
    public static final int AUDIO_FALLBACK_MODE_MUTE = 1;
    public static final int AUDIO_FALLBACK_MODE_FAIL = 2;

    /** @hide */
    @IntDef(
        value = {
                AUDIO_STRETCH_MODE_DEFAULT,
                AUDIO_STRETCH_MODE_VOICE,
        }
    )
    @Retention(RetentionPolicy.SOURCE)
    public @interface AudioStretchMode {}
    public static final int AUDIO_STRETCH_MODE_DEFAULT = 0;
    public static final int AUDIO_STRETCH_MODE_VOICE = 1;

    // flags to indicate which settings are actually set
    private static final int SET_SPEED               = 1 << 0;
    private static final int SET_PITCH               = 1 << 1;
    private static final int SET_AUDIO_FALLBACK_MODE = 1 << 2;
    private static final int SET_AUDIO_STRETCH_MODE  = 1 << 3;
    private int mSet = 0;

    // settings
    private int mAudioFallbackMode = AUDIO_FALLBACK_MODE_DEFAULT;
    private int mAudioStretchMode = AUDIO_STRETCH_MODE_DEFAULT;
    private float mPitch = 1.0f;
    private float mSpeed = 1.0f;

    /**
     * Allows defaults to be returned for properties not set.
     * Otherwise a {@link java.lang.IllegalArgumentException} exception
     * is raised when getting those properties
     * which have defaults but have never been set.
     * @return this <code>PlaybackSettings</code> instance.
     */
    public PlaybackSettings allowDefaults() {
        mSet |= SET_AUDIO_FALLBACK_MODE | SET_AUDIO_STRETCH_MODE | SET_PITCH | SET_SPEED;
        return this;
    }

    /**
     * Sets the audio fallback mode.
     * @param audioFallbackMode
     * @return this <code>PlaybackSettings</code> instance.
     */
    public PlaybackSettings setAudioFallbackMode(@AudioFallbackMode int audioFallbackMode) {
        mAudioFallbackMode = audioFallbackMode;
        mSet |= SET_AUDIO_FALLBACK_MODE;
        return this;
    }

    /**
     * Retrieves the audio fallback mode.
     * @return audio fallback mode
     * @throws IllegalStateException if the audio fallback mode is not set.
     */
    public @AudioFallbackMode int getAudioFallbackMode() {
        if ((mSet & SET_AUDIO_FALLBACK_MODE) == 0) {
            throw new IllegalStateException("audio fallback mode not set");
        }
        return mAudioFallbackMode;
    }

    /**
     * Sets the audio stretch mode.
     * @param audioStretchMode
     * @return this <code>PlaybackSettings</code> instance.
     */
    public PlaybackSettings setAudioStretchMode(@AudioStretchMode int audioStretchMode) {
        mAudioStretchMode = audioStretchMode;
        mSet |= SET_AUDIO_STRETCH_MODE;
        return this;
    }

    /**
     * Retrieves the audio stretch mode.
     * @return audio stretch mode
     * @throws IllegalStateException if the audio stretch mode is not set.
     */
    public @AudioStretchMode int getAudioStretchMode() {
        if ((mSet & SET_AUDIO_STRETCH_MODE) == 0) {
            throw new IllegalStateException("audio stretch mode not set");
        }
        return mAudioStretchMode;
    }

    /**
     * Sets the pitch factor.
     * @param pitch
     * @return this <code>PlaybackSettings</code> instance.
     */
    public PlaybackSettings setPitch(float pitch) {
        mPitch = pitch;
        mSet |= SET_PITCH;
        return this;
    }

    /**
     * Retrieves the pitch factor.
     * @return pitch
     * @throws IllegalStateException if pitch is not set.
     */
    public float getPitch() {
        if ((mSet & SET_PITCH) == 0) {
            throw new IllegalStateException("pitch not set");
        }
        return mPitch;
    }

    /**
     * Sets the speed factor.
     * @param speed
     * @return this <code>PlaybackSettings</code> instance.
     */
    public PlaybackSettings setSpeed(float speed) {
        mSpeed = speed;
        mSet |= SET_SPEED;
        return this;
    }

    /**
     * Retrieves the speed factor.
     * @return speed
     * @throws IllegalStateException if speed is not set.
     */
    public float getSpeed() {
        if ((mSet & SET_SPEED) == 0) {
            throw new IllegalStateException("speed not set");
        }
        return mSpeed;
    }
}
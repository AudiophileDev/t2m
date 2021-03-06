package com.audiophile.t2m.music;

import com.audiophile.t2m.Utils;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;

import static com.audiophile.t2m.music.MidiUtils.*;

/**
 * A class that generates a rhythm based on the given text.s
 */
public class RhythmTrack implements TrackGenerator {
    /**
     * The tempo of the music
     */
    private Tempo tempo;
    /**
     * The average word length per sentence in the text
     */
    private float[] avgWordLen;

    /**
     * Creates a new rhythm generator, which can generate music based on the given music data
     *
     * @param musicData  Meta data about the text
     * @param avgWordLen The average word length per sentence in the text
     */
    RhythmTrack(MusicData musicData, float[] avgWordLen) {
        this.tempo = musicData.tempo;
        this.avgWordLen = Utils.BlurData(avgWordLen, 3);

        // Find min and max avg word length in the text
        float max = 0,
                min = 100;
        for (float anAvgWordLen : avgWordLen) {
            if (anAvgWordLen > max)
                max = anAvgWordLen;
            if (anAvgWordLen < min)
                min = anAvgWordLen;
        }
        // Decrease all values by minimum and normalize them between 0 and 3
        max -= min;
        if (max > 0)
            for (int i = 0; i < avgWordLen.length; i++) {
                avgWordLen[i] -= min; // Decrease
                avgWordLen[i] = 3 * avgWordLen[i] / max; // Normalize
            }
    }

    /**
     * Rounds the given value to a full semiquaver
     *
     * @param val The value to round
     * @return A multiple of {@link MidiUtils#SEMIQUAVER}
     */
    private int roundToQuaver(float val) {
        return (((int) val) / QUAVER) * QUAVER;
    }

    /**
     * Rounds the given value to a full quaver
     *
     * @param val The value to round
     * @return A multiple of {@link MidiUtils#QUAVER}
     */
    private int roundToSemiQuaver(float val) {
        return (((int) val) / SEMIQUAVER) * SEMIQUAVER;
    }

    /**
     * Generates the rhythm and writes it to the given track on the given channel
     *
     * @param track   The track to write to
     * @param channel The channel to write to
     */
    @Override
    public void writeToTrack(Track track, int channel) {

        int length = SecsInTicks(15, tempo.resolution);
        int bass, snare, hiHat;
        int vel = 64;
        try {
            // Add bass
            int i = 0;
            for (int n = 0; n < length; n += bass) {
                addNote(track, n, QUARTER, 36, vel, channel);
                bass = roundToQuaver(QUARTER * avgWordLen[(i += 3) % avgWordLen.length]);
            }

            //Add snare
            i = 0;
            for (int n = 0; n < length; n += snare) {
                addNote(track, n, QUARTER, 38, vel, channel);
                snare = roundToQuaver(QUAVER * avgWordLen[(i += 3 + 1) % avgWordLen.length]);
            }

            //Add hi-hat
            i = 0;
            for (int n = 0; n < length; n += hiHat % QUAVER != 0 ? SEMIQUAVER : QUAVER) {
                hiHat = roundToSemiQuaver(SEMIQUAVER * avgWordLen[(i += 3 + 2) % avgWordLen.length]);
                addNote(track, n, SEMIQUAVER, 42, vel, channel);
            }
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }
}

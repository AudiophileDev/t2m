package com.audiophile.t2m.music;

import com.audiophile.t2m.text.TextAnalyser;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

public class Composer {
    private TrackGenerator[] trackGenerators;
    private Tempo tempo;
    private String dynamic;
    private Harmony key;
    private MusicData musicData;

    private TextAnalyser analysedText;

    /**
     * This class merges the different MIDI channels of rhythm, melody and sound effects
     * it also calculates the meta data of the music (dynamic, tempo, key)
     */
    public Composer(TextAnalyser analysedText) {
        this.analysedText = analysedText;
        //TODO get key from tendencies
        this.key = new Harmony(analysedText.getSentences()[0].getWords()[0].getName().substring(0, 1), Modes.major, false);
        this.dynamic = dynamic; //forte, piano, cresc, decresc
        this.tempo = new Tempo(analysedText.getAvgWordLength());

        this.musicData = new MusicData(tempo,
                "anyDynamic", /*forte, piano, cresc, decresc*/
                key);

        this.trackGenerators = new TrackGenerator[2];
        this.trackGenerators[0] = new MelodyTrack(musicData, analysedText.getSentences(), "noteMapping.csv");
        this.trackGenerators[1] = new RhythmTrack(analysedText);
    }

    /**
     * Merges the tracks of all music generators into one sequence
     *
     * @return A Sequence with all tracks
     */
    public Sequence getSequence() {
        Sequence sequence = null;
        try {
            //TODO find PPQ factor for setting Tempo
            sequence = new Sequence(Sequence.PPQ, tempo.getAverageBpm());
            for (int i = 0; i < trackGenerators.length; i++)
                trackGenerators[i].writeToTrack(sequence.createTrack(), i);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        return sequence;
    }
}

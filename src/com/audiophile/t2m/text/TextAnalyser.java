package com.audiophile.t2m.text;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * @author Simon
 * Created on 05.09.2017.
 */
public class TextAnalyser {
    private Sentence[] sentences;

    public TextAnalyser(String text) {
        String[] sentencesList = splitSentences(text);
        sentences = new Sentence[sentencesList.length];
        for (int i = 0; i < sentences.length; i++)
            sentences[i] = new Sentence(sentencesList[i]);
    }

    /**
     * Returns sentences as array
     *
     * @return Sentence[] Sentences as array
     */
    public Sentence[] getSentences() {
        return sentences;
    }

    /**
     * Splits string into sentences
     *
     * @param text String text
     * @return String[] Sentences as string array
     */
    private String[] splitSentences(String text) {
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.GERMAN);
        iterator.setText(text);
        ArrayList<String> sentenceList = new ArrayList<>(text.length() / 6); // Avg word length in german is 5.7
        int start = iterator.first();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
            String sentence = text.substring(start, end).trim();
            // Exclude empty sentences
            if (sentence.length() > 0) {
                Stream.of(sentence.split("\n"))
                        .filter(s -> s.length() > 0)
                        .forEach(sentenceList::add);
            }
        }
        sentenceList.trimToSize(); // Remove unused indices

        // Convert ArrayList to array
        String[] sentences = new String[sentenceList.size()];
        sentenceList.toArray(sentences);
        return sentences;
    }
}

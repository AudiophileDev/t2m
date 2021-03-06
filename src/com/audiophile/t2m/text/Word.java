package com.audiophile.t2m.text;

/**
 * Represents a word in the text and holds its name, tendency and effect from the database.
 *
 * @author Simon Niedermayr
 */
public class Word {
    /**
     * The word as plain text
     */
    private String name;

    /**
     * Marks if the word is a filler word
     *
     * @see WordFilter#markFillers(Sentence[])
     */
    private boolean isFiller;

    /**
     * Reference to the word database.
     * If word is not in database this is null.
     */
    private DatabaseHandler.Entry entry;

    /**
     * Creates a new word
     *
     * @param name  The word as string
     * @param entry The entry in the database
     */
    public Word(String name, DatabaseHandler.Entry entry) {
        this.name = name;
        this.entry = entry;
    }

    public boolean isFiller() {
        return isFiller;
    }

    void setFiller(boolean filler) {
        isFiller = filler;
    }

    /**
     * Returns the words name
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    public DatabaseHandler.Entry getEntry() {
        return entry;
    }

    /**
     * Class for the representation of a words tendency.
     * <ul>
     * <li>Bad = 0</li>
     * <li>Negative = 1</li>
     * <li>Neutral = 2</li>
     * <li>Positive = 3</li>
     * <li>Good = 4</li>
     * </ul>
     */
    public enum Tendency {
        Bad(0), Negative(1), Neutral(2), Positive(3), Good(4);
        final int value;

        Tendency(int value) {
            this.value = value;
        }

        /**
         * Converts a string of a number to a <code>Tendency</code>
         *
         * @param text number as string
         * @return corresponding <code>Tendency</code> with Neutral(2) as default
         */
        public static Tendency map(String text) {
            int v;
            try {
                v = Integer.parseInt(text);
            } catch (NumberFormatException nfe) {
                v = 2;
            }
            return map(v);
        }

        /**
         * Converts a string of a number to a <code>Tendency</code>
         *
         * @param id number
         * @return corresponding <code>Tendency</code> with Neutral(2) as default
         */
        public static Tendency map(int id) {
            switch (id) {
                case 0:
                    return Bad;
                case 1:
                    return Negative;
                case 3:
                    return Positive;
                case 4:
                    return Good;
                default:
                    return Neutral;
            }
        }
    }
}

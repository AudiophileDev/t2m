package com.audiophile.t2m.text;import com.audiophile.t2m.reader.CSVTools;import java.io.IOException;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import java.util.Map;public class DatabaseHandler {    /**     * The minimum similarity needed to see two words as similar     *     * @see #FindWord(String, double)     */    public static double DEFAULT_IN_SIMILARITY = 0.65;//TODO find best value    /**     * Global database which holds words with their tendency and effects     */    private static List<Entry> database;    private static String file;    /**     * Loads word database from CSV file.     *     * @param file CSV file     * @throws IOException Throws exception if file could not be read of has wrong format     * @see CSVTools#ReadFile(String)     */    public static void LoadDB(String file) throws IOException {        DatabaseHandler.file = file;        String[][] table = CSVTools.ReadFile(file);        if (table.length > 0 && table[0].length < 3)            throw new IOException("Word database file \"" + file + "\" does not provide word,tendency and effect column");        //TODO maybe replace HashMap with Trie (test efficiency first!)        database = new ArrayList<>(table.length);        for (String[] row : table) {            assert row[0] != null;            database.add(new Entry(row[0], Word.Tendency.map(row[1]), (row[2].isEmpty() ? null : row[2])));        }    }    /**     * The method updates the given entry in the database.     *     * @param word The word to update in database     * @throws IOException Throws exception if the file database file could not be updated     * @see DatabaseHandler#writeDatabase()     */    public static void SetWord(String word, Word.Tendency tendency, String effect) throws IOException {        if (word == null || word.isEmpty()) // Do not add empty words to database            return;        boolean changed = false;        for (Entry e : database)            if (e.name.equals(word)) {                e.effect = effect;                e.tendency = tendency;                changed = true;                break;            }        if (!changed)            database.add(new Entry(word, tendency, effect));    }    /**     * Removes entry from database and writes the database to the file     * @param word     * @throws IOException     */    public static void RemoveWord(String word) throws IOException {        boolean changed = false;        for (int i = 0; i < database.size(); i++)            if (database.get(i).name.equals(word)) {                database.remove(i);                changed = true;                break;            }        if (changed)            writeDatabase();    }    /**     * Writes the current {@link DatabaseHandler#database} object to the database file     *     * @throws IOException If file could not be written     * @see CSVTools#WriteFile(String, String[][])     */    private static void writeDatabase() throws IOException {        String[][] db = new String[database.size()][3];        final int[] i = {0};        database.forEach((e) ->                db[i[0]++] = new String[]{e.name, String.valueOf(e.tendency.ordinal()), e.effect}        );        CSVTools.WriteFile(file, db);    }    /**     * Calculates the similarity (a number within 0 and 1) between two strings.     *     * @param s1 first string     * @param s2 second string     * @return Value between 0.0 and 1.0 (1 if strings are equal)     */    private static double similarity(String s1, String s2) {        String longer = s1, shorter = s2;        if (s1.length() < s2.length()) { // longer should always have greater length            longer = s2;            shorter = s1;        }        int longerLength = longer.length();        if (longerLength == 0) {            return 1.0; /* both strings are zero length */        }        return (longerLength - levenshteinDistance(longer, shorter)) / (double) longerLength;    }    /**     * Calculates Levenshtein Distance for to strings.     *     * @param lhs First word     * @param rhs Second Word     * @return distance between 0 and 1     * @see <a href="https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#java">Levenshtein Distance</a>     */    private static int levenshteinDistance(CharSequence lhs, CharSequence rhs) {        int len0 = lhs.length() + 1;        int len1 = rhs.length() + 1;        // the array of distances        int[] cost = new int[len0];        int[] newcost = new int[len0];        // initial cost of skipping prefix in String s0        for (int i = 0; i < len0; i++)            cost[i] = i;        // dynamically computing the array of distances        // transformation cost for each letter in s1        for (int j = 1; j < len1; j++) {            // initial cost of skipping prefix in String s1            newcost[0] = j;            // transformation cost for each letter in s0            for (int i = 1; i < len0; i++) {                // matching current letters in both strings                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;                // computing cost for each transformation                int cost_replace = cost[i - 1] + match;                int cost_insert = cost[i] + 1;                int cost_delete = newcost[i - 1] + 1;                // keep minimum cost                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);            }            // swap cost/newcost arrays            int[] swap = cost;            cost = newcost;            newcost = swap;        }        // the distance is the cost for transforming all letters in both strings        return cost[len0 - 1];    }    /**     * Represents an entry in the database     */    public static class Entry {        private Word.Tendency tendency;        private String name, effect;        public Entry(String name, Word.Tendency tendency, String effect) {            this.tendency = tendency;            this.effect = effect;            this.name = name;        }        public Word.Tendency getTendency() {            return tendency;        }        public String getEffect() {            return effect;        }        public String getName() {            return name;        }    }    /**     * If true the {@link DatabaseHandler#FindWord(String, double)} method searches for the word with the best match.     * This gives slightly better results but also takes much more time.     */    private final static boolean PRECISE_SEARCH = false;    /**     * If {@link DatabaseHandler#PRECISE_SEARCH} is true the function searches for closest word in database and returns     * a copy of the entry.     * Else the first word, which has a higher similarity than the given minimum, is taken.     *     * @param word The word to find the attributes for     * @return The <code>WordAttributes</code> or null if it was not found in the database     * @throws IOException Throws exception if database was not loaded jet     */    public static Entry FindWord(String word, double minSimilarity) throws IOException {        if (database == null)            throw new IOException("Word database was not loaded jet");        Entry entry = null;        double maxSimilarity = 0;        for (Entry e : database) {            double similarity = similarity(word, e.name);            if (similarity >= minSimilarity && similarity > maxSimilarity) {                entry = new Entry(e.name, e.tendency, e.effect); //TODO copy or reference?                maxSimilarity = similarity;                if (!PRECISE_SEARCH)                    break;            }        }        return entry;    }}
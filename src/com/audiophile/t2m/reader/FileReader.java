package com.audiophile.t2m.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that holds a collection of methods for file reading.
 *
 * @author Simon Niedermayr
 * Created on 11.09.2017
 */
public class FileReader {
    /**
     * The given file is read as plain text with UTF-8 encoding and converted to a <code>String</code>
     *
     * @param fileName Path to the file
     * @return Plain File content
     * @throws IOException Throws an error if the file was not found or could not be read
     * @see java.io.FileInputStream#FileInputStream(File)
     */
    public static String ReadPlainFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists())
            throw new FileNotFoundException("The file \"" + fileName + "\" does not exist");

        // Read bytes in file
        FileInputStream stream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        stream.close();

        return new String(data, "UTF-8");
    }

    // Default split characters for csv files
    private static final char DEFAULT_SEPARATOR = ',';
    // Default quote characters for csv files
    private static final char DEFAULT_QUOTE = '"';

    /**
     * Reads a CSV (Comma-separated values) file and converts it to a table.
     * Every line in the file represents a single row.
     * The columns are separated by a {@value #DEFAULT_SEPARATOR} character and quotes are marked with the {@value #DEFAULT_QUOTE} character.
     *
     * @param fileName String Path of the file to be read
     * @return The CSV table as two-dimensional string array
     * @throws IOException Throws exception if the document could not be read or does not has the expected format
     * @see <a href="https://tools.ietf.org/html/rfc4180">Common Format and MIME Type for Comma-Separated Values (CSV) Files</a>
     * @see com.audiophile.t2m.reader.FileReader#ReadPlainFile(String)
     */
    public static String[][] ReadCSVFile(String fileName) throws IOException {
        String content = ReadPlainFile(fileName);
        String[][] table = null;
        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String[] columns;
            try {
                columns = parseCSVLine(lines[i], DEFAULT_SEPARATOR, DEFAULT_QUOTE);
            } catch (Exception e) {
                throw new IOException("An error occurred in line " + i + " while reading the file \"" + fileName + "\"");
            }
            // Initialize array size with first row
            if (table == null)
                table = new String[lines.length][columns.length];
            else if (table[0].length != columns.length) // Throw error, if a row has to few columns
                throw new IOException("To few columns in line " + i + " in file \"" + fileName + "\"");

            // Copy values to array
            for (int j = 0; j < columns.length; j++)
                table[i][j] = columns[0];
        }
        // Return empty array if file is empty
        if (table == null)
            table = new String[0][0];
        return table;
    }

    /**
     * The function takes a line in CSV format and converts it to a column with individual cells.
     *
     * @param cvsLine     The column as string
     * @param separator   Character for splitting the line into columns. If no value is assigned the default value {@value #DEFAULT_SEPARATOR} is used.
     * @param customQuote Character that marks quotes. If no value is assigned the default value {@value #DEFAULT_QUOTE} is used.
     * @return Line split int columns
     * @throws IOException Throws exception if an error occurs while parsing the line
     */
    private static String[] parseCSVLine(String cvsLine, char separator, char customQuote) throws IOException {

        List<String> result = new ArrayList<>();

        // Empty string returns empty array
        if (cvsLine == null || cvsLine.isEmpty()) {
            return new String[0];
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separator == ' ') {
            separator = DEFAULT_SEPARATOR;
        }

        //  Buffer for building the column values
        StringBuffer curVal = new StringBuffer();
        // Defines if the cursor is currently in a quote
        boolean inQuotes = false;
        // Defines if the following chars are seen as cell values
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        // Cursor runs through string
        for (char ch : chars) {
            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    // If cursor is in quote, the quote ends now
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {
                    // Allow empty quote ("")
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {
                    inQuotes = true;
                    // Allow empty quote ("")
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }
                    // Allow double quotes
                    if (startCollectChar) {
                        curVal.append('"');
                    }
                } else if (ch == separator) {
                    result.add(curVal.toString());
                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    // Ignore
                } else if (ch == '\n') {
                    throw new IOException();
                } else {
                    curVal.append(ch);
                }
            }

        }
        result.add(curVal.toString());

        // Return result as Array
        String[] array = new String[result.size()];
        result.toArray(array);
        return array;
    }

}

package com.audiophile.t2m.io;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.*;

/**
 * A class that holds a collection of methods for file reading.
 *
 * @author Simon Niedermayr
 */
public class FileUtils {
    /**
     * The given file is read as plain text with UTF-8 encoding and converted to a <code>String</code>
     *
     * @param fileName Path to the file
     * @return Plain File content
     * @throws IOException Throws an error if the file was not found or could not be read
     * @see java.io.FileInputStream#FileInputStream(File)
     */
    public static String ReadPlainFile(String fileName) throws IOException {
        // Read bytes in file
        InputStream stream = new FileInputStream(fileName);
        byte[] data = new byte[stream.available()];
        stream.read(data);
        stream.close();

        return new String(data, "UTF-8");
    }

    /**
     * Writes the given <code>content</code> to the given file.
     * If <code>append</code> is true, the content is added to the end of the file.
     * Else the file is overridden.
     *
     * @param fileName The file to write to
     * @param content  The content to write to the file
     * @param append   Append content to end of file
     * @throws IOException Thrown if writing to file was not possible
     */
    static void WriteFile(String fileName, String content, boolean append) throws IOException {
        File file = new File(fileName);
        if (!file.exists())
            if (!file.createNewFile())
                throw new IOException("Could not create file\"" + fileName + "\"");
        FileWriter writer = new FileWriter(fileName);
        if (append)
            writer.append(content);
        else
            writer.write(content);
        writer.close();
    }

    /**
     * Loads a midi sequence from a file
     * @param file The path to the midi file
     * @return A midi sequence
     */
    public static Sequence LoadMidiFile(String file) {
        try {
            return MidiSystem.getSequence(new FileInputStream(file));
        } catch (InvalidMidiDataException | IOException e) {
            System.err.println("Midi file '"+file+"' not found");
            return null;
        }
    }

}

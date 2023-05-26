package ch.filippofinke;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple steganography tool to hide text in images.
 *
 * @author filippofinke
 */
public class Main {

    /**
     * Hides the specified text in a file.
     *
     * @param text        The text to hide.
     * @param source      The source file.
     * @param destination The destination file.
     * @throws IOException If an I/O error occurs.
     */
    public static void hideText(String text, Path source, Path destination) throws IOException {
        byte[] bytes = Files.readAllBytes(source);
        int textLength = text.length();

        // check if text is too long
        if (textLength > (bytes.length - 32) / 8) {
            throw new IllegalArgumentException("Text is too long to hide in the image.");
        }

        hideTextLength(bytes, textLength);
        hideTextContent(bytes, text);

        Files.write(destination, bytes);
    }

    /**
     * Hides the length of the text in the byte array.
     *
     * @param bytes      The byte array.
     * @param textLength The length of the text.
     */
    private static void hideTextLength(byte[] bytes, int textLength) {
        for (int i = 0; i < 32; i++) {
            byte b = (byte) (bytes[bytes.length - 1 - i] & 0xFE);
            b |= (textLength >> i) & 1;
            bytes[bytes.length - 1 - i] = b;
        }
    }

    /**
     * Hides the content of the text in the byte array.
     *
     * @param bytes The byte array.
     * @param text  The text to hide.
     */
    private static void hideTextContent(byte[] bytes, String text) {
        int textLength = text.length();
        for (int i = 0; i < textLength; i++) {
            char c = text.charAt(i);
            for (int j = 0; j < 8; j++) {
                byte b = (byte) (bytes[bytes.length - 1 - i * 8 - j - 32] & 0xFE);
                b |= (c >> j) & 1;
                bytes[bytes.length - 1 - i * 8 - j - 32] = b;
            }
        }
    }

    /**
     * Reveals the hidden text in a file.
     *
     * @param source The source file.
     * @return The revealed text, or null if no text is found.
     * @throws IOException If an I/O error occurs.
     */
    public static String revealText(Path source) throws IOException {
        byte[] bytes = Files.readAllBytes(source);

        int textLength = revealTextLength(bytes);

        if (textLength > 0) {
            return revealTextContent(bytes, textLength);
        }

        return null;
    }

    /**
     * Reveals the length of the hidden text from the byte array.
     *
     * @param bytes The byte array.
     * @return The length of the hidden text.
     */
    private static int revealTextLength(byte[] bytes) {
        int textLength = 0;
        for (int i = 0; i < 32; i++) {
            byte b = bytes[bytes.length - 1 - i];
            textLength |= (b & 1) << i;
        }
        return textLength;
    }

    /**
     * Reveals the content of the hidden text from the byte array.
     *
     * @param bytes      The byte array.
     * @param textLength The length of the hidden text.
     * @return The revealed text.
     */
    private static String revealTextContent(byte[] bytes, int textLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < textLength; i++) {
            char c = 0;
            for (int j = 0; j < 8; j++) {
                byte b = bytes[bytes.length - 1 - i * 8 - j - 32];
                c |= (b & 1) << j;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Main method.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {

        try {
            if (args.length == 0) {
                // check if we are running in a terminal
                if (System.console() == null) {
                    SwingUtilities.invokeLater(() -> {
                        StegaUI stegaUI = new StegaUI();
                        stegaUI.setVisible(true);
                    });
                } else {
                    System.out.println("Hide text:");
                    System.out.println("Usage: java -jar stega.jar [text] [source] [destination]");
                    System.out.println("Example: java -jar stega.jar \"Hello World\" image.jpg hidden.jpg");
                    System.out.println("Reveal text:");
                    System.out.println("Usage: java -jar stega.jar [source]");
                    System.out.println("Example: java -jar stega.jar hidden.jpg");
                }
            } else if (args.length == 1) {
                System.out.println(revealText(Path.of(args[0])));
            } else if (args.length == 3) {
                hideText(args[0], Path.of(args[1]), Path.of(args[2]));
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

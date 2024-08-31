package org.prototype;

/**
 * A utility class for ANSI escape codes used for coloring terminal text output.
 * <p>
 * This class provides constants for various ANSI escape codes that can be used to change the color of text
 * in terminal or console applications. These constants help in making the output more readable and visually distinct.
 * </p>
 */
public class Constants {
    /**
     * ANSI escape code to reset the text color to the default.
     */
    public static String RESET = "\033[0m";  // Reset to default color

    /**
     * ANSI escape code for red text color.
     */
    public static String RED = "\033[0;31m"; // Red text

    /**
     * ANSI escape code for green text color.
     */
    public static String GREEN = "\033[0;32m"; // Green text

    /**
     * ANSI escape code for yellow text color.
     */
    public static String YELLOW = "\033[0;33m"; // Yellow text
}

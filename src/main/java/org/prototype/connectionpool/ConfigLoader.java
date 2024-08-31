package org.prototype.connectionpool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A utility class to load configuration properties from a file.
 * <p>
 * The class loads a properties file specified by the file name passed to the constructor.
 * It provides methods to retrieve property values as strings or integers.
 * </p>
 */
public class ConfigLoader {
    private final Properties properties;

    /**
     * Constructs a ConfigLoader and loads properties from the specified file.
     *
     * @param fileName the name of the properties file to be loaded (must be on the classpath)
     * @throws IOException if an error occurs while reading the properties file
     */
    public ConfigLoader(String fileName) {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IOException("Unable to find " + fileName);
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the value of a property as a string.
     *
     * @param key the key of the property
     * @return the value of the property as a string, or {@code null} if the key is not found
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Retrieves the value of a property as an integer.
     *
     * @param key the key of the property
     * @return the value of the property as an integer
     * @throws NumberFormatException if the property value cannot be parsed as an integer
     */
    public int getIntProperty(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static Properties properties;
    private static final String CONFIG_FILE = "config.properties";

    static {
        properties = new Properties();
        try (InputStream input = Configuration.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Cannot find " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration", e);
        }
    }

    public static int getTotalRounds() {
        return Integer.parseInt(properties.getProperty("total_rounds", "2"));
    }

    public static int getQuestionsPerRound() {
        return Integer.parseInt(properties.getProperty("questions_per_round", "3"));
    }

    public static boolean showCategories() {
        return Boolean.parseBoolean(properties.getProperty("show_categories", "true"));
    }

    public static boolean allowSameCategory() {
        return Boolean.parseBoolean(properties.getProperty("allow_same_category", "true"));
    }

    public static boolean showRoundNumber() {
        return Boolean.parseBoolean(properties.getProperty("show_round_number", "true"));
    }
}
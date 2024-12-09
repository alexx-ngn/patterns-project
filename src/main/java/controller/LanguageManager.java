package controller;

import lombok.Getter;

import java.util.Locale;
import java.util.ResourceBundle;

@Getter
public class LanguageManager {
    private Locale currentLocale;
    private ResourceBundle resourceBundle;

    /**
     * A singleton instance of LanguageManager used to manage localization settings.
     * This instance allows access to language resources and facilitates
     * changing the application's locale and resource bundles.
     * The instance is lazily initialized and thread-safe.
     */
    private static LanguageManager instance;

    /**
     * Constructs a new LanguageManager instance with default locale and resource bundle.
     *
     * Initializes the LanguageManager with the default locale set to "en_CA".
     * The resource bundle is loaded for the "lang.Login" resource with the specified locale.
     */
    public LanguageManager() {
        currentLocale = Locale.of("en","CA");
        resourceBundle = ResourceBundle.getBundle("lang.Login", currentLocale);
    }

    /**
     * Returns the singleton instance of the LanguageManager class.
     * This method ensures that only one instance of LanguageManager is created and used throughout the application.
     * The double-checked locking pattern is used to ensure thread safety while minimizing synchronization overhead.
     *
     * @return the singleton instance of LanguageManager
     */
    public static LanguageManager getInstance() {
        if (instance == null) {
            synchronized (LanguageManager.class) {
                if (instance == null) {
                    instance = new LanguageManager();
                }
            }
        }
        return instance;
    }

    /**
     * Sets the current locale and updates the resource bundle for the specified scene.
     *
     * @param scene the name of the scene for which the locale and resource bundle should be set
     * @param locale the Locale to be set for the specified scene
     */
    public void setLocale(String scene, Locale locale) {
        currentLocale = locale;
        resourceBundle = ResourceBundle.getBundle("lang." + scene, currentLocale);
    }

    /**
     * Retrieves the resource bundle for the specified scene based on the current locale.
     *
     * @param scene the name of the scene for which the resource bundle is to be retrieved.
     * @return the resource bundle associated with the given scene and current locale.
     */
    public ResourceBundle getResourceBundle(String scene) {
        return ResourceBundle.getBundle("lang." + scene, currentLocale);
    }
}

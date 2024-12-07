package controller;

import lombok.Getter;

import java.util.Locale;
import java.util.ResourceBundle;

@Getter
public class LanguageManager {
    private Locale currentLocale;
    private ResourceBundle resourceBundle;

    private static LanguageManager instance;

    public LanguageManager() {
        currentLocale = Locale.of("en","CA");
        resourceBundle = ResourceBundle.getBundle("lang.Login", currentLocale);
    }

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

    public void setLocale(String scene, Locale locale) {
        currentLocale = locale;
        resourceBundle = ResourceBundle.getBundle("lang." + scene, currentLocale);
    }

    public ResourceBundle getResourceBundle(String scene) {
        return ResourceBundle.getBundle("lang." + scene, currentLocale);
    }
}

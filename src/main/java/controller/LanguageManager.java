package controller;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static Locale currentLocale = new Locale("en", "CA");
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.Login", currentLocale);

    public static void setLocale(String scene, Locale locale) {
        currentLocale = locale;
        resourceBundle = ResourceBundle.getBundle("lang." + scene, currentLocale);
    }

    public static ResourceBundle getResourceBundle(String scene) {
        return ResourceBundle.getBundle("lang." + scene, currentLocale);
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}

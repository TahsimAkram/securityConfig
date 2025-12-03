package com.bj.security.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class PropertiesUtil {

    private static Environment env;

    @Autowired
    public PropertiesUtil(Environment environment) {
        PropertiesUtil.env = environment;
    }

    public static String get(String key) {
        return env.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return env.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return env.getProperty(key, Integer.class, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return env.getProperty(key, Boolean.class, defaultValue);
    }

    /**
     * Return the active profile (first one if multiple).
     */
    public static String getActiveProfile() {
        String[] profiles = env.getActiveProfiles();
        return profiles.length > 0 ? profiles[0] : null;
    }

    /**
     * Return all active profiles
     */
    public static String[] getActiveProfiles() {
        return env.getActiveProfiles();
    }
}

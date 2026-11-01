package com.nidasehba.ui.config;

/** Run settings, overridable with -D system properties (e.g. -Dheadless=false -Dbrowser=firefox). */
public final class Config {

    private Config() {
    }

    public static String baseUrl() {
        return System.getProperty("baseUrl", "https://www.saucedemo.com");
    }

    /** chromium, firefox or webkit. */
    public static String browser() {
        return System.getProperty("browser", "chromium");
    }

    /** Optional installed-browser channel such as "chrome" or "msedge"; empty uses Playwright's bundled build. */
    public static String channel() {
        return System.getProperty("channel", "");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty("headless", "true"));
    }

    public static String password() {
        // Public demo password published on the Sauce Demo login page.
        return System.getProperty("demoPassword", "secret_sauce");
    }
}

package org.nwgb.wikibridge;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    private final FileConfiguration config;
    private final Logger logger;
    private final JavaPlugin plugin;

    private final String wikiName;
    private final String wikiURL;
    private final String apiEndpoint;
    private final int maxPreviewChars;
    private final String missingPreview;
    private final String existsColour;
    private final String missingColour;
    private final String dateFormat;


    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.config = plugin.getConfig();
        this.wikiName = config.getString("wiki-name");
        this.wikiURL = loadWikiURL(config);
        this.apiEndpoint = loadAPIEndpoint(config);
        this.maxPreviewChars = config.getInt("max-preview-chars", 250);
        this.missingPreview = config.getString("preview.missing-text", "This article does not exist (yet)!");
        this.existsColour = loadExistsColour(config, logger);
        this.missingColour = loadMissingColour(config, logger);
        this.dateFormat = config.getString("date-format", "dd mmm yy");

    }

    private String loadWikiURL(FileConfiguration config) {
        String wikiURL = config.getString("wiki-url","https://wikipedia.org");

        // Remove trailing / if present
        if (wikiURL.endsWith("/")) {
            wikiURL = wikiURL.substring(0, wikiURL.length() - 1);
        }
        return wikiURL;
    }

    private String loadAPIEndpoint(FileConfiguration config) {
        String apiEndpoint = config.getString("api-endpoint", "/w/api.php");

        // Add leading / if mising
        if (!apiEndpoint.startsWith("/")) {
            apiEndpoint = "/" + apiEndpoint;
        }
        return apiEndpoint;
    }

    private String validateColour(String colour, String defaultColour) {
        try {
            // Check if valid hex colour
            ChatColor.of(colour);
            return colour;
        } catch (IllegalArgumentException e) {
            return defaultColour;
        }
    }

    private String loadExistsColour(FileConfiguration config, Logger logger) {
        String defaultColour = "#5555FF";
        String colour = config.getString("link-colours.exists", defaultColour);
        return validateColour(colour,defaultColour);
    }

    private String loadMissingColour(FileConfiguration config, Logger logger) {
        String defaultColour = "#FF5555";
        String colour = config.getString("link-colours.missing", defaultColour);
        return validateColour(colour,defaultColour);
    }


    public String getWikiName() {
        return wikiName;
    }

    public String getURL() {
        return wikiURL;
    }
    public String getApiEndpoint() {
        return wikiURL + apiEndpoint;
    }

    public int getMaxPreviewChars() {
        return maxPreviewChars;
    }

    public String getMissingPreview() {
        return missingPreview;
    }

    public String getExistsColour() {
        return existsColour;
    }

    public String getMissingColour() {
        return missingColour;
    }

    public String getDateFormat() {
        return dateFormat;
    }
}
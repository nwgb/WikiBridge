package org.nwgb.wikibridge.Wiki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.logging.Logger;


public class WikiClient {
    private final String wikiName;
    private final String wikiUrl;
    private final Integer maxPreviewChars;
    private final String missingPreview;
    private final Logger logger;

    public WikiClient(String wikiName, String wikiUrl, Integer maxPreviewChars, String missingPreview, Logger logger) {
        this.wikiName = wikiName;
        this.wikiUrl = wikiUrl;
        this.maxPreviewChars = maxPreviewChars;
        this.missingPreview = missingPreview;
        this.logger = logger;
    }

    // Query the MediaWiki API for article info
    public ArticleData getArticle(String articleName) {
        String article = URLEncoder.encode(articleName.replace(" ", "_"), StandardCharsets.UTF_8);
        String url = String.format(
                "%s?action=query&prop=extracts|revisions&exchars=%d&explaintext=1&titles=%s&format=json&formatversion=2&exintro=1&rvprop=timestamp",
                wikiUrl, maxPreviewChars, article
        );

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", wikiName);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }


                // MW API results are in JSON format
                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonObject page = json.getAsJsonObject("query").getAsJsonArray("pages").get(0).getAsJsonObject();

                if (page.has("missing") && page.get("missing").getAsBoolean()) {
                    return new ArticleData(false, missingPreview, "");
                } else {
                    // Article intro
                    String extract = page.get("extract").getAsString();

                    // Last edit timestamp
                    String lastEdit = "";
                    if (page.has("revisions")) {
                        lastEdit = page.getAsJsonArray("revisions").get(0).getAsJsonObject().get("timestamp").getAsString();
                    }
                    return new ArticleData(true, extract, lastEdit);
                }
            }
        } catch (MalformedURLException e) {
            // Invalid URL
            logger.warning("Invalid URL: " + url);
            return new ArticleData(false, "Unable to load preview: Invalid Wiki URL", "");
        } catch (IOException e) {
            // Timed out
            logger.warning("Timed out while connecting to " + url);
            return new ArticleData(false, "Unable to load preview: Timed out", "");
        } catch (Exception e) {
            // Other error
            return new ArticleData(false, "Unable to load preview:" + e.getMessage(), "");
        }
    }
}


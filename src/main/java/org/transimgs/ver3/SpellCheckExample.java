package org.transimgs.ver3;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SpellCheckExample {
    public static String CorrectText(String textToCheck) {
        try {
            String url = "https://api.languagetool.org/v2/check?text=" + URLEncoder.encode(textToCheck, StandardCharsets.UTF_8) + "&language=en-US";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
            JsonArray matches = jsonResponse.getAsJsonArray("matches");

            StringBuilder correctedText = new StringBuilder(textToCheck);
            int offset = 0;

            for (int i = 0; i < matches.size(); i++) {
                JsonObject match = matches.get(i).getAsJsonObject();
                int fromPos = match.get("offset").getAsInt();
                int toPos = fromPos + match.get("length").getAsInt();
                JsonArray replacements = match.getAsJsonArray("replacements");

                if (replacements.size() > 0) {
                    String bestSuggestion = replacements.get(0).getAsJsonObject().get("value").getAsString();
                    correctedText.replace(fromPos + offset, toPos + offset, bestSuggestion);
                    offset += bestSuggestion.length() - (toPos - fromPos);
                }
            }

            return correctedText.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

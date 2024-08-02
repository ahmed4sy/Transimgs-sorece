package org.transimgs.ver3;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HuggingFaceTranslation {
    private static final String API_URL = "https://api-inference.huggingface.co/models/Helsinki-NLP/opus-mt-en-ar";
    private static final String API_TOKEN = "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";

    public static String translate(String text) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("inputs", text);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_TOKEN)
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBodyJson))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        String responseBody = response.body();

        // تحليل JSON لاستخراج النص المترجم
        JsonNode rootNode = objectMapper.readTree(responseBody);
        String translatedText = rootNode.get(0).get("translation_text").asText();
        return translatedText;
    }

    public static String translateGPT(String textToTranslate) {
        try {
            String translatedText = translate(textToTranslate);
            return translatedText;
        } catch (Exception e) {
            e.printStackTrace();
            return textToTranslate;
        }
    }
}

package org.transimgs.ver3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;

public class MyMemoryTranslator {
    private static final String API_URL = "https://api.mymemory.translated.net/get";

    public static String translate(String text, String langFrom, String langTo) throws Exception {
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
        String urlStr = API_URL + "?q=" + encodedText + "&langpair=" + langFrom + "|" + langTo;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // استخراج الترجمة من الاستجابة JSON باستخدام مكتبة org.json
            String jsonResponse = response.toString();
            return parseTranslation(jsonResponse);
        } else {
            throw new RuntimeException("HTTP GET Request Failed with Error code : " + responseCode);
        }
    }

    private static String parseTranslation(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        return jsonObject.getJSONObject("responseData").getString("translatedText");
    }

    public static void main(String[] args) {
        try {
            String translatedText = translate("Hello, world!", "en", "ar");
            System.out.println("Translated Text: " + translatedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

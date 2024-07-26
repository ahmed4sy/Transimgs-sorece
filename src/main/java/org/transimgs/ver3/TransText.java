/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.transimgs.ver3;

import net.suuft.libretranslate.Language;
import net.suuft.libretranslate.Translator;
import net.suuft.libretranslate.exception.BadTranslatorResponseException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ahmed4s
 */
public class TransText {
    private static final String API_URL = "https://api.mymemory.translated.net/get";

    public static String translateText(String text) throws IOException, BadTranslatorResponseException {
        String[] command = {"/bin/sh", "-c", "echo \"" + text + "\" | trans -b en:ar"};
        Process process = Runtime.getRuntime().exec(command);

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        String translatedText = "";
        while ((line = reader.readLine()) != null) {
            translatedText += line + "\n";
        }
        return translatedText;
    }

    public static ArrayList<ArrayList<String>> translistText(ArrayList<ArrayList<String>> text) throws Exception {
        System.out.print("Translator...");
        String fromLang = "en";
        String toLang = "ar";
        Translator.setUrlApi("https://libretranslate.de/translate");
        ArrayList<ArrayList<String>> res = text;
        List<Integer> positions = new ArrayList<>();
        for (ArrayList<String> txt : text) {
            String tmp = txt.get(0);
            positions.add(txt.get(0).split(" ").length);
            for (int i = 1; i < txt.size(); i++) {
                tmp += " " + txt.get(i);
                positions.add(txt.get(i).split(" ").length);
            }
            String trn = translate(BubbleTextDetection.clearSymbols(tmp.strip()), fromLang, toLang).strip();
            List<String> ty = splitTextByPos(trn, positions);
            positions.clear();
            ArrayList<String> ty1 = new ArrayList<>();
            for (String word : ty) {
                ty1.add(word);
            }
            res.set(res.indexOf(txt), ty1);
        }
        System.out.println("Done!");
        return res;
    }

    public static List<String> splitTextByPos(String text, List<Integer> positions) {
        List<String> result = new ArrayList<>();
        String[] words = text.split(" ");
        int start = 0;

        for (int pos : positions) {
            if (start >= words.length) {
                result.add("");
                continue;
            }

            StringBuilder segment = new StringBuilder();
            int end = Math.min(start + pos, words.length);
            for (int i = start; i < end; i++) {
                segment.append(words[i]).append(" ");
            }

            result.add(segment.toString().strip());
            start = end;
        }

        // إضافة النص المتبقي إذا كان هناك أي
        if (start < words.length) {
            StringBuilder remainingSegment = new StringBuilder();
            for (int i = start; i < words.length; i++) {
                remainingSegment.append(words[i]).append(" ");
            }
            result.add(remainingSegment.toString().strip());
        }

        return result;
    }

    public static List<String> splitTextByPositions(String text, List<Integer> positions) {
        List<String> result = new ArrayList<>();
        String[] words = text.split(" ");
        int start = 0;
        int fanStart = 0;
        for (int pos : positions) {
            String tmp = "";
            int timer = 0;
            for (int i = start; i < words.length; i++) {
                if (timer == pos) break;
                else {
                    tmp += words[i] + " ";
                    timer++;
                    fanStart++;
                }
            }
            result.add(tmp.strip());
            start = fanStart;


        }
        return result;
    }

    public static String translateClient(String text) throws Exception {

        String targetLanguage = "ar";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2"))
                .header("content-type", "application/x-www-form-urlencoded")
                .header("Accept-Encoding", "application/gzip")
                .header("X-RapidAPI-Key", "c9f9b852ddmsh708b51cc3b05464p17e62bjsnd7fdf8e65e3a")
                .header("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
                .POST(HttpRequest.BodyPublishers.ofString("q=" + text + "&target=" + targetLanguage))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        JSONObject jsonResponse = new JSONObject(response.body());
//        String translatedText = jsonResponse
//                .getJSONObject("data")
//                .getJSONArray("translations")
//                .getJSONObject(0)
//                .getString("translatedText");
        System.out.println(response.body());
        System.exit(0);
        return "";
    }

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
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.transimgs.ver3;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ahmed4s
 */
public class Translator {
    private static final String CLIENT_ID = "FREE_TRIAL_ACCOUNT";
    private static final String CLIENT_SECRET = "PUBLIC_SECRET";
    private static final String ENDPOINT = "http://api.whatsmate.net/v1/translation/translate";


    public static String translateText(String text) throws Exception {
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
        ArrayList<ArrayList<String>> res = text;
        for (ArrayList<String> txt : text) {
            String tmp = txt.get(0);
            for (int i = 1; i < txt.size(); i++) {
                tmp += " " + txt.get(i);
            }
            String trn = translate(fromLang, toLang, BubbleTextDetection.clearSymbols(tmp.strip())).strip();
            String[] ty = trn.split(" ");

            ArrayList<String> ty1 = new ArrayList<>();
            for (String word : ty) {
                ty1.add(word);
            }
            res.set(res.indexOf(txt), ty1);
        }
        System.out.println("Done!");
        return res;
    }

    public static String translate(String fromLang, String toLang, String text) throws Exception {
        // TODO: Should have used a 3rd party library to make a JSON string from an object
        String jsonPayload = new StringBuilder()
                .append("{")
                .append("\"fromLang\":\"")
                .append(fromLang)
                .append("\",")
                .append("\"toLang\":\"")
                .append(toLang)
                .append("\",")
                .append("\"text\":\"")
                .append(text)
                .append("\"")
                .append("}")
                .toString();

        URL url = new URL(ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("X-WM-CLIENT-ID", CLIENT_ID);
        conn.setRequestProperty("X-WM-CLIENT-SECRET", CLIENT_SECRET);
        conn.setRequestProperty("Content-Type", "application/json");

        OutputStream os = conn.getOutputStream();
        os.write(jsonPayload.getBytes());
        os.flush();
        os.close();

        int statusCode = conn.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 200) ? conn.getInputStream() : conn.getErrorStream()
        ));
        String output;
        String result = "";
        while ((output = br.readLine()) != null) {
            result += output + "\n";

        }
        conn.disconnect();
        return result;

    }
}

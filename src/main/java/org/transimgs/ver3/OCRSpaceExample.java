package org.transimgs.ver3;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class OCRSpaceExample {
    static final String apiKey;

    static {
        try {
            apiKey = BubbleTextDetection.readJsonFile("config.json").getString("key-api-OCR");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static String doOCR(BufferedImage bufferedImage) {
        try {
            byte[] imageBytes = bufferedImageToByteArray(bufferedImage);
            String result = sendImageToOCRSpace(apiKey, imageBytes);
            return result;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static byte[] bufferedImageToByteArray(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos); // أو يمكنك استخدام "png" أو أي تنسيق مناسب آخر
        return baos.toByteArray();
    }

    private static String sendImageToOCRSpace(String apiKey, byte[] imageBytes) throws IOException, JSONException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost("https://api.ocr.space/parse/image");
            uploadFile.setHeader("apikey", apiKey);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", imageBytes, ContentType.APPLICATION_OCTET_STREAM, "image.jpg");
            HttpEntity multipart = builder.build();

            uploadFile.setEntity(multipart);

            HttpResponse response = httpClient.execute(uploadFile);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity);
                JSONObject jsonResult = new JSONObject(result);
                String parsedText = jsonResult.getJSONArray("ParsedResults").getJSONObject(0).getString("ParsedText");

                return parsedText;
            } else {
                return null;
            }
        }
    }

    public static void test(BufferedImage img) {
        System.out.println("ON_TEST");
        System.out.println(img);
    }

    private static BufferedImage getBufferedImage() {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File("/home/ahmed4s/Pictures/work/inp/002.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }
}

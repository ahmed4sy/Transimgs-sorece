/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.transimgs.ver3;

/**
 * @author ahmed4s
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class BubbleTextDetection {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws TesseractException, IOException {
        System.out.println(detectWhiteRegions("/home/ahmed4s/Downloads/img.jpg").size());
    }

    public static void methodTess4j() throws TesseractException, IOException {

        BufferedImage original = ImageIO.read(new File("/home/ahmed4s/Pictures/chapter61/007.jpg"));
        BufferedImage grayImage = toGrayScale(original);
        List<BufferedImage> bubs = getBubblesWords(grayImage);
        saveBufferimgs(bubs, "crope/");


    }

    public static List<Word> getWords(BufferedImage original) throws TesseractException, IOException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata/");
        tesseract.setLanguage("eng");
        List<Word> words = tesseract.getWords(original, ITessAPI.TessPageIteratorLevel.RIL_WORD);
        for (Word word : words) {
            if (clearSymbols(word.getText()).isEmpty()) {
                words.set(words.indexOf(word), null);
            }
        }
        words.removeIf(word -> word == null);
        return words;
    }

    public static JSONObject readJsonFile(String fileName) throws JSONException {
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new JSONObject(jsonContent.toString());
    }

    public static void writeJsonToFile(String fileName, JSONObject jsonObject) throws JSONException {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonObject.toString(4)); // كتابة JSON بشكل منسق بأربع مسافات
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> parseStringToList(String input) {
        List<Integer> resultList = new ArrayList<>();

        // إزالة الأقواس المربعة والمسافات البيضاء
        input = input.replaceAll("[\\[\\]\\s]", "");

        // تقسيم النص إلى عناصر باستخدام الفاصلة كمحدد
        String[] elements = input.split(",");

        // تحويل كل عنصر إلى عدد صحيح باستخدام التقريب
        for (String element : elements) {
            try {
                // محاولة تحويل العنصر إلى عدد صحيح مباشرة
                int number = Integer.parseInt(element);
                resultList.add(number);
            } catch (NumberFormatException e) {
                // إذا لم يكن العنصر عددًا صحيحًا، قم بتحويله إلى عدد عشري ثم قربه
                float number = Float.parseFloat(element);
                resultList.add(Math.round(number));
            }
        }

        return resultList;
    }

    public static List<BufferedImage> getBubblesWords(BufferedImage original) throws TesseractException, IOException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata/");
        tesseract.setLanguage("eng");
        List<BufferedImage> bubs = new ArrayList<>();
        List<Word> words = tesseract.getWords(original, ITessAPI.TessPageIteratorLevel.RIL_WORD);
        for (Word word : words) {
            bubs.add(original.getSubimage(word.getBoundingBox().x, word.getBoundingBox().y, word.getBoundingBox().width, word.getBoundingBox().height));
        }

        return bubs;
    }

    public static List<Rectangle> bubbles(List<Word> words, int horizontalDistance, int verticalDistance) {
        if (words.isEmpty()) return new ArrayList<>();

        boolean descent = true;
        List<List<Rectangle>> list = new ArrayList<>();
        List<Rectangle> root = new ArrayList<>();
        Rectangle tmp = words.get(0).getBoundingBox();
        root.add(tmp);
        boolean[] Boolen = new boolean[words.size()];
        Boolen[0] = true;
        int n = 0, ls = 1;

        for (int i = 1; i < words.size(); i++) {
            Boolen[i] = false;
        }

        while (descent) {
            int s = 0;
            n += 1;
            for (int i = ls; i < words.size(); i++) {
                Rectangle wordRect = words.get(i).getBoundingBox();
                if (tmp.y <= wordRect.y && wordRect.y <= tmp.y + verticalDistance &&
                        wordRect.x <= tmp.x + horizontalDistance && wordRect.x >= tmp.x - horizontalDistance) {
                    Boolen[i] = true;
                    root.add(wordRect);
                    tmp = wordRect;
                }
            }
            list.add(new ArrayList<>(root));

            if (n == 100) {
                System.out.println("ERROR: LOOP");
                break;
            }

            for (int i = 0; i < Boolen.length; i++) {
                if (!Boolen[i]) {
                    tmp = words.get(i).getBoundingBox();
                    Boolen[i] = true;
                    root = new ArrayList<>();
                    root.add(tmp);
                    ls = i;
                    break;
                } else {
                    s += 1;
                    if (s == Boolen.length) {
                        descent = false;
                    }
                }
            }
        }

        List<Rectangle> result = new ArrayList<>();
        for (List<Rectangle> group : list) {
            if (!group.isEmpty()) {
                Rectangle unionRect = group.get(0);
                for (int i = 1; i < group.size(); i++) {
                    unionRect = unionRect.union(group.get(i));
                }
                result.add(unionRect);
            }
        }

        return result;
    }

    public static List<Rectangle> margeWords(List<Word> words, int horizontalDistance, int verticalDistance) {
        List<Rectangle> root = new ArrayList<>();
        List<Boolean> elements = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) elements.add(false);

        while (true) {
            int startIdx = -1;
            for (int i = 0; i < elements.size(); i++) {
                if (!elements.get(i)) {
                    startIdx = i;
                    break;
                }
            }

            if (startIdx == -1) break;

            Rectangle bubble = words.get(startIdx).getBoundingBox();
            elements.set(startIdx, true);

            boolean expanded;
            do {
                expanded = false;
                for (int i = 0; i < words.size(); i++) {
                    if (!elements.get(i)) {
                        String wordText = clearSymbols(words.get(i).getText());
                        if (!wordText.isEmpty()) {
                            Rectangle wordRect = words.get(i).getBoundingBox();
                            if (isWithinDistance(bubble, wordRect, horizontalDistance, verticalDistance)) {
                                bubble = bubble.union(wordRect);
                                elements.set(i, true);
                                expanded = true;
                            }
                        }
                    }
                }
            } while (expanded);

            root.add(bubble);
        }
        return root;
    }

    private static boolean isWithinDistance(Rectangle r1, Rectangle r2, int horizontalDistance, int verticalDistance) {
        return Math.abs(r1.getCenterX() - r2.getCenterX()) <= horizontalDistance
                && Math.abs(r1.getCenterY() - r2.getCenterY()) <= verticalDistance;
    }

    public static List<BufferedImage> getBubblesWords(BufferedImage original, List<Word> words) {
        List<BufferedImage> bubs = new ArrayList<>();
        for (Word word : words) {
            bubs.add(original.getSubimage(word.getBoundingBox().x, word.getBoundingBox().y, word.getBoundingBox().width, word.getBoundingBox().height));
        }

        return bubs;
    }

    public static List<BufferedImage> extractRegions(BufferedImage image, List<Rect> regions) {
        List<BufferedImage> extractedImages = new ArrayList<>();

        for (Rect rect : regions) {
            BufferedImage subImage = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
            extractedImages.add(subImage);
        }

        return extractedImages;
    }

    public static List<BufferedImage> extractRegionsM(BufferedImage image, List<Rectangle> regions) {
        List<BufferedImage> extractedImages = new ArrayList<>();

        for (Rectangle rect : regions) {
            BufferedImage subImage = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
            extractedImages.add(subImage);
        }

        return extractedImages;
    }

    public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_GRAYSCALE);
    }

    public static BufferedImage convertToBlackAndWhite(String inputPath) throws IOException {

        File input = new File(inputPath);
        BufferedImage image = ImageIO.read(input);

        // Create a black-and-white image of the same size.
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

        // Get the graphics context for the black-and-white image.
        Graphics2D graphic = result.createGraphics();

        // Render the input image on it.
        graphic.drawImage(image, 0, 0, Color.WHITE, null);

        // Save the resulting image using the PNG format.
        return result;

    }

    public static List<Rect> detectWhiteRegions(String imagePath) throws IOException {
        // تحميل الصورة
        Mat src = BufferedImage2Mat(convertToBlackAndWhite(imagePath));
        if (src.empty()) {
            System.out.println("Could not open or find the image!");
            return new ArrayList<>();
        }
        // تقليل التشويش باستخدام GaussianBlur
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(src, blurred, new Size(5, 5), 0);

        // تحديد المناطق البيضاء باستخدام العتبة
        Mat binary = new Mat();
        Imgproc.threshold(blurred, binary, 200, 255, Imgproc.THRESH_BINARY);

        // إيجاد الحدود للمناطق البيضاء
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Rect> whiteRegions = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            // التحقق من أن المنطقة أكبر من 50 بكسل
            if (rect.width * rect.height > 250) {
                whiteRegions.add(rect);
            }
        }

        return whiteRegions;
    }

    public static BufferedImage toGrayScale(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color c = new Color(original.getRGB(i, j));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                int grayValue = red + green + blue;
                int gray = new Color(grayValue, grayValue, grayValue).getRGB();
                grayImage.setRGB(i, j, gray);
            }
        }
        return grayImage;
    }

    public static void methodCV() throws TesseractException, IOException {
        Mat image = Imgcodecs.imread("/home/ahmed4s/Pictures/chapter61/007.jpg");
        List<MatOfPoint> contours = createContours(image);
        // List<MatOfPoint> contours = createContoursMAX(image);
        List<BufferedImage> bubs = bubsGenrator(image, contours);
        System.out.println(bubs.size());
        int numdiff = 0;
        while (true) {
            System.out.println("-----" + numdiff + "-----");
            bubs = filterOCR(bubs);
            if (bubs.size() == numdiff) {
                break;
            }
            numdiff = bubs.size();
        }

        bubs = filtercomprs(bubs);
        saveBufferimgs(bubs, "crope/");
        System.out.println(bubs.size());

    }

    public static List<BufferedImage> filterReptOcr(List<BufferedImage> bubs) throws TesseractException {
        int numdiff = 0;
        while (true) {
            System.out.println("-----" + numdiff + "-----");
            bubs = filterOCR(bubs);
            if (bubs.size() == numdiff) {
                break;
            }
            numdiff = bubs.size();
        }
        return bubs;
    }

    public static List<BufferedImage> filtercomprs(List<BufferedImage> bubs) throws TesseractException {
        List<String> texts = textBufferimg(bubs);
        int[] reptIndexs = findAllIndices(arrayListToStringArray(texts));
        for (int index : reptIndexs) {
            bubs.set(index, null);
        }
        bubs.removeIf(image -> image == null);
        return bubs;
    }

    static String[] arrayListToStringArray(List<String> list) {
        String[] arr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    public static int[] findAllIndices(String[] arr) {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i].equals(arr[j])) {
                    indices.add(j);
                }
            }
        }

        int[] result = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            result[i] = indices.get(i);
        }

        return result;
    }

    public static List<String> textBufferimg(List<BufferedImage> bubs) throws TesseractException {
        ITesseract instance = new Tesseract();
        instance.setLanguage("eng");
        instance.setDatapath("/usr/share/tesseract-ocr/4.00/tessdatafast/");
        instance.setTessVariable("user_defined_dpi", "300");
        List<String> texts = new ArrayList<>();
        for (BufferedImage bub : bubs) {
            texts.add(clearSymbols(instance.doOCR(bub)));
        }
        return texts;
    }

    public static List<Rect> convertToRectList(List<List<Integer>> list) {
        List<Rect> rectList = new ArrayList<>();

        for (List<Integer> innerList : list) {
            if (innerList.size() == 4) {
                int x = innerList.get(0);
                int y = innerList.get(1);
                int width = innerList.get(2);
                int height = innerList.get(3);
                Rect rect = new Rect(x, y, width, height);
                rectList.add(rect);
            } else {
                throw new IllegalArgumentException("Each inner list must contain exactly 4 integers.");
            }
        }

        return rectList;
    }

    public static List<BufferedImage> bubsGenrator(Mat image, List<MatOfPoint> contours) {
        List<BufferedImage> bubs = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            if (rect.width < 450 && rect.width > 100) { // تعديل هذه الشروط حسب الحاجة
                bubs.add(matToBufferedImage(new Mat(image, rect)));
//                Imgproc.rectangle(image, rect, new Scalar(0, 255, 0), 2);
//                 Imgcodecs.imwrite("crop/"+i+".jpg", new Mat(image,rect));
//                 i++;
            }
        }
        return bubs;
    }

    public static void textprintBufferimg(List<BufferedImage> bubs) throws TesseractException {
        ITesseract instance = new Tesseract();
        instance.setLanguage("eng");
        instance.setDatapath("/usr/share/tesseract-ocr/4.00/tessdatafast/");
        int i = 1;
        instance.setTessVariable("user_defined_dpi", "300");
        for (BufferedImage bub : bubs) {
            System.out.println(i + ": " + clearSymbols(instance.doOCR(bub)));
            i++;
        }

    }

    public static List<BufferedImage> filterOCR(List<BufferedImage> bubs) throws TesseractException {
        ITesseract instance = new Tesseract();
        instance.setLanguage("eng");
        instance.setDatapath("/usr/share/tesseract-ocr/4.00/tessdatafast/");
        instance.setTessVariable("user_defined_dpi", "300");
        List<BufferedImage> bube = bubs;
        forfilt:
        for (int i = 0; i < bubs.size(); i++) {
            String text = instance.doOCR(bubs.get(i));
            text = clearSymbols(text);
            if (text.length() < 2) {
                bube.remove(i);
            }

        }
        return bube;
    }

    public static String executeCommand(String command) {
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                output.append(System.lineSeparator());
                line = reader.readLine();
            }
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String clearSymbols(String txt) {
        String res = txt
                .toLowerCase()
                .strip()
                .replace("=", "")
                .replace("\\", "")
                .replace("/", "")
                .replace("|", "")
                .replace("-", "")
                .replace("—", "")
                .replace("_", "")
                .replace("+", "")
                .replace(")", "")
                .replace("(", "")
                .replace("[", "")
                .replace("]", "")
                .replace("}", "")
                .replace("{", "")
                .replace("‘", "")
                .replace("'", "")
                .replace("\"", "");
        return res;
    }

    public static List<BufferedImage> filtercompare(List<BufferedImage> bubs) {
        List<BufferedImage> bube = bubs;
        for (int i = 0; i < bubs.size(); i++) {
            int end = 1;
            for (int j = end; j < bubs.size(); j++) {
                if ((int) compareImages(bubs.get(i), bubs.get(j)) > 90) {
                    bube.remove(j);
                }
                end++;

            }

        }
        return bube;

    }

    public static void saveBufferimgs(List<BufferedImage> bube, String outfolder) throws IOException {
        int ine = 1;
        for (BufferedImage bu : bube) {
            ImageIO.write(bu, "jpg", new File(outfolder + ine + ".jpg"));
            ine++;
        }
    }

    public static double compareImages(BufferedImage image1, BufferedImage image2) {

        // Resize images to the same size if they are not already
        int width = Math.min(image1.getWidth(), image2.getWidth());
        int height = Math.min(image1.getHeight(), image2.getHeight());

        BufferedImage resizedImage1 = resizeImage(image1, width, height);
        BufferedImage resizedImage2 = resizeImage(image2, width, height);

        // Compare the images
        int totalPixels = width * height;
        int similarPixels = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel1 = resizedImage1.getRGB(x, y);
                int pixel2 = resizedImage2.getRGB(x, y);

                if (pixel1 == pixel2) {
                    similarPixels++;
                }
            }
        }

        // Calculate the percentage of similarity
        double similarityPercentage = ((double) similarPixels / totalPixels) * 100;

        return similarityPercentage;
    }

    public static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
            Mat bgr = new Mat();
            Imgproc.cvtColor(mat, bgr, Imgproc.COLOR_BGR2RGB); // Ensure the color order is RGB
            mat = bgr;
        }

        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] buffer = new byte[bufferSize];
        mat.get(0, 0, buffer); // get all the pixels
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                resizedImage.setRGB(x, y, originalImage.getRGB(x * originalImage.getWidth() / targetWidth, y * originalImage.getHeight() / targetHeight));
            }
        }
        return resizedImage;
    }

    public static boolean hasEnglishLetters(String text) {
        // التعبير المنتظم الذي يبحث عن الأحرف الإنجليزية
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(text);

        // إرجاع true إذا وجدنا تطابقًا، false إذا لم نجد
        return matcher.find();
    }

    public static List<MatOfPoint> createContours(Mat image) {
        // تحويل الصورة إلى تدرجات الرمادي
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // تطبيق Gaussian Blur لتقليل الضوضاء
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

        // الكشف عن الحواف باستخدام Canny edge detection
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 50, 150);

        // العثور على الحواف والكونتورات
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    public static List<MatOfPoint> createContoursMAX(Mat image) {
        // تحويل الصورة إلى تدرجات الرمادي
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // تطبيق Gaussian Blur لتقليل الضوضاء
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

        // تطبيق تحويل العتبة التكيفية
        Mat threshold = new Mat();
        Imgproc.adaptiveThreshold(gray, threshold, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);

        // تطبيق العمليات المورفولوجية لتحسين الصورة
        Mat morph = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.morphologyEx(threshold, morph, Imgproc.MORPH_CLOSE, kernel);
        Imgproc.morphologyEx(morph, morph, Imgproc.MORPH_OPEN, kernel);

        // الكشف عن الحواف باستخدام Canny edge detection
        Mat edges = new Mat();
        Imgproc.Canny(morph, edges, 50, 150);

        // العثور على الحواف والكونتورات
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        // تصفية الكونتورات بناءً على الشكل والحجم
        List<MatOfPoint> filteredContours = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            RotatedRect boundingBox = Imgproc.minAreaRect(contour2f);

            // التحقق من أن الشكل بيضاوي أو دائري
            double aspectRatio = Math.min(boundingBox.size.width, boundingBox.size.height) / Math.max(boundingBox.size.width, boundingBox.size.height);
            if (area > 100 && aspectRatio > 0.5) { // يمكن تعديل هذه المعاملات بناءً على الحجم والشكل المتوقع للفقاعات
                filteredContours.add(contour);
            }
        }

        return filteredContours;
    }


    public static int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1]
                                    + costOfSubstitution(s1.charAt(i - 1), s2.charAt(j - 1)),
                            Math.min(dp[i - 1][j] + 1,
                                    dp[i][j - 1] + 1));
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int similarityPercentage(String s1, String s2) {
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) {
            return 100; // Both strings are empty
        }
        int distance = levenshteinDistance(s1, s2);
        return (int) ((1 - (double) distance / maxLength) * 100);
    }

}

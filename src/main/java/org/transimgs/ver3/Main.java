package org.transimgs.ver3;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Rect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("+++++++++++++++++++++{ TRANSIMGs }++++++++++++++++++++");
        System.out.print("Author: ahmed Yousif || ");
        System.out.println("Version: 3.0.0\n");
        System.out.println("Mode List: (1-default, 2-ReaderOCR, 3-RedactImg, 4-Debug)");
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter mode:");
            String mode = sc.nextLine();
            System.out.println();
            switch (mode) {
                case "auto", "default", "1" -> modeExtra(sc);
                case "ReaderOCR", "2" -> imVmode(sc);
                case "RedactImg", "3" -> redactImg(sc);
                case "Debug", "4" -> modeDebug(sc);
            }
            System.out.println("______________________END_______________________");
        }
//        extraImage("/home/ahmed4s/Downloads/002.jpg", "so");
    }

    private static void modeDebug(Scanner sc) throws Exception {

        System.out.print("Enter image path:");
        String imagename = sc.nextLine();
        Reader rdr = new Reader(imagename);
        List<List<Integer>> resultReader = rdr.dataOCR();
        System.out.println("resultreader:" + resultReader.size() + "\n" + resultReader + "\n\n");
        ArrayList<ArrayList<String>> Text = rdr.getText(resultReader);
        System.out.println("text:" + Text.size() + "\n" + Text + "\n\n");
        resultReader = BubbleTextDetection.filterRect(ImageIO.read(new File(imagename)), resultReader);
        System.out.println("resultreader filter:" + resultReader.size() + "\n" + resultReader + "\n\n");

        Text = TransText.translistText(Text);
        System.out.println("text trans:" + Text.size() + "\n" + Text + "\n\n");

    }

    public static void redactImg(Scanner sc) throws TesseractException, IOException, JSONException, InterruptedException {
        System.out.print("Enter json-file-text path:");
        JSONObject jsonpath = BubbleTextDetection.readJsonFile(sc.nextLine());
        System.out.print("Enter image path:");
        String imagename = sc.nextLine();
        assert jsonpath != null;
        JSONArray texts = jsonpath.getJSONArray("text");
        ArrayList<ArrayList<String>> txall = new ArrayList<>();
        for (int i = 0; i < texts.length(); i++) {
            String text = texts.getString(i);
            txall.add(BubbleTextDetection.wrapWords(text));
        }
        Reader rdr = new Reader(imagename);
        List<List<Integer>> resultReader = rdr.dataOCR();
        Drawing dra = new Drawing(Reader.sorece, txall, resultReader);
        System.out.print("Enter output path:");
        String folderOut = sc.nextLine();
        dra.drawingTextandSave(imagename.split("/")[imagename.split("/").length - 1], folderOut);

    }

    public static void imVmode(Scanner sc) throws TesseractException, IOException, JSONException, InterruptedException {
        System.out.print("Enter image path:");
        String imgpath = sc.nextLine();
        readimg(imgpath);
    }

    public static void readimg(String imagename) throws TesseractException, IOException, InterruptedException, JSONException {
        Reader rdr = new Reader(imagename);
        List<List<Integer>> resultReader = rdr.dataOCR();
        rdr.displayImgReader(resultReader);
    }

    public static void modeExtra(Scanner sc) throws Exception {
        System.out.println("Mode Extra: 1-default, 2-initplace (sorece='inp', output='out')");
        System.out.print("Enter mode:");
        String mode = sc.nextLine();
        if (mode.equals("1")) {
            System.out.print("Enter Folder images: ");
            String imgfolder = sc.nextLine();
            System.out.print("Enter Folder Output: ");
            String folderout = sc.nextLine();
            System.out.println();
            String[] files = BubbleTextDetection.executeCommand("ls -m " + imgfolder).split(",");
            for (String file : files) {
                if (!file.isEmpty()) {
                    extraImage(imgfolder + file.strip().replace("\n", ""), folderout);
                }
            }
        } else if (mode.equals("2")) {
            if (doesFileExist("initfold.txt")) {
                String initfolder = readFileAsString("initfold.txt").replace("\n", "");
                String imgfolder = initfolder + "/inp/";
                String folderout = initfolder + "/out/";
                System.out.println();
                String[] files = BubbleTextDetection.executeCommand("ls -m " + imgfolder).split(",");
                for (String file : files) {
                    if (!file.isEmpty()) {
                        extraImage(imgfolder + file.strip().replace("\n", ""), folderout);
                    }
                }
            } else {
                System.out.print("Enter Folder init:");
                String initfolder = sc.nextLine();
                String imgfolder = initfolder + "/inp/";
                String folderout = initfolder + "/out/";
                System.out.println();
                String[] files = BubbleTextDetection.executeCommand("ls -m " + imgfolder).split(",");
                for (String file : files) {
                    if (!file.isEmpty()) {
                        extraImage(imgfolder + file.strip().replace("\n", ""), folderout);
                    }
                }
            }

        }


    }

    public static void extraImage(String imagename, String folderOut) throws Exception {
//        System.out.println("-------------" + imagename.split("/")[imagename.split("/").length - 1] + "-------------");
//        Reader rdr = new Reader(imagename);
//        List<List<Integer>> resultReader = rdr.dataOCR();
//        resultReader = BubbleTextDetection.filterRect(ImageIO.read(new File(imagename)), resultReader);
////        ReadImage im = new ReadImage(rdr.sorece);
////        ArrayList<ArrayList<String>> Text = im.getText(resultReader);
//        Text = TransText.translistText(Text);
//        Drawing dra = new Drawing(Reader.sorece, Text, resultReader);
//        dra.drawingTextandSave(imagename.split("/")[imagename.split("/").length - 1], folderOut);
    }

    public static void methodTess4j() throws TesseractException, IOException {
        BufferedImage original = ImageIO.read(new File("/home/ahmed4s/Pictures/chapter61/image.jpg"));
        List<Word> words = BubbleTextDetection.getWords(original);
        List<Rectangle> rectangles = BubbleTextDetection.margeWords(words, 100, 100);
        List<BufferedImage> bubs = BubbleTextDetection.extractRegionsM(original, rectangles);
        bubs = BubbleTextDetection.filterReptOcr(bubs);
        bubs = BubbleTextDetection.filtercomprs(bubs);
        BubbleTextDetection.saveBufferimgs(bubs, "cpr/");
    }

    public static boolean doesFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    public static String readFileAsString(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static void methodTool() throws Exception {
        BufferedImage original = ImageIO.read(new File("/home/ahmed4s/Downloads/img.jpg"));
        List<Rect> rects = BubbleTextDetection.detectWhiteRegions("/home/ahmed4s/Downloads/img.jpg");
        List<BufferedImage> bubs = BubbleTextDetection.extractRegions(original, rects);
        BubbleTextDetection.saveBufferimgs(bubs, "cpr/");
        System.out.println(bubs.size());
    }
}

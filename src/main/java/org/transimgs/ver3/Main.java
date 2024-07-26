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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("+++++++++++++++++++++{ TIA:v=3.0 }++++++++++++++++++++");
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("RUN-mode:");
            String mode = sc.nextLine();
            System.out.println();
            switch (mode) {
                case "auto", "default" -> modeExtra(sc);
                case "ocrimg" -> imVmode(sc);
                case "redactimg" -> redactImg(sc);
            }
            System.out.println("______________________END_______________________");
        }
//        extraImage("/home/ahmed4s/Downloads/002.jpg", "so");
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
    }

    public static void extraImage(String imagename, String folderOut) throws Exception {
        System.out.println("-------------" + imagename.split("/")[imagename.split("/").length - 1] + "-------------");
        Reader rdr = new Reader(imagename);
        List<List<Integer>> resultReader = rdr.dataOCR();
        resultReader = BubbleTextDetection.filterRect(ImageIO.read(new File(imagename)), resultReader);
        ArrayList<ArrayList<String>> Text = rdr.getText(resultReader);
        Text = TransText.translistText(Text);
        Drawing dra = new Drawing(Reader.sorece, Text, resultReader);
        dra.drawingTextandSave(imagename.split("/")[imagename.split("/").length - 1], folderOut);
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

    public static void methodTool() throws Exception {
        BufferedImage original = ImageIO.read(new File("/home/ahmed4s/Downloads/img.jpg"));
        List<Rect> rects = BubbleTextDetection.detectWhiteRegions("/home/ahmed4s/Downloads/img.jpg");
        List<BufferedImage> bubs = BubbleTextDetection.extractRegions(original, rects);
        BubbleTextDetection.saveBufferimgs(bubs, "cpr/");
        System.out.println(bubs.size());
    }
}

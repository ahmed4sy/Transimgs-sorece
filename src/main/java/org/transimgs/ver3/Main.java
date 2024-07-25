package org.transimgs.ver3;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import org.json.JSONException;
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
        try (Scanner sc = new Scanner(System.in)) {
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
//        extraImage("/home/ahmed4s/Downloads/002.jpg", "so");
    }

    public static void readimg(String imagename) throws TesseractException, IOException, InterruptedException, JSONException {
        Reader rdr = new Reader(imagename);
        List<List<Integer>> resultReader = rdr.dataOCR();
        rdr.displayImgReader(resultReader);
    }

    public static void extraImage(String imagename, String folderOut) throws Exception {
        System.out.println("-------------" + imagename.split("/")[imagename.split("/").length - 1] + "-------------");
        Reader rdr = new Reader(imagename);
        List<List<Integer>> resultReader = rdr.dataOCR();
        ArrayList<ArrayList<String>> Text = rdr.getText(resultReader);
        Text = TransText.translistText(Text);
        Drawing dra = new Drawing(Reader.sorece, Text, resultReader);
        dra.drawingTextandSave(imagename.split("/")[imagename.split("/").length - 1], folderOut);
        System.out.println("------------- END -------------");
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

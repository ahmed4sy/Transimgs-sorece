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

public class Main {
    public static void main(String[] args) throws Exception {
        extraImage("/home/ahmed4s/Pictures/chapter61/image.jpg");
//        Reader rdr = new Reader("/home/ahmed4s/Pictures/chapter61/image.jpg");
//        List<List<Integer>> resultReader = rdr.dataOCR();

//        BubbleTextDetection.saveBufferimgs(
//                BubbleTextDetection.extractRegionsRaeder(
//                        ImageIO.read(new File("/home/ahmed4s/Pictures/chapter61/image.jpg"))
//                        , BubbleTextDetection.convertToRectList(resultReader)
//                )
//                , "cpr/");
    }

    public static void readimg(String imagename) throws TesseractException, IOException, InterruptedException, JSONException {
        Reader rdr = new Reader(imagename);
        List<List<Integer>> resultReader = rdr.dataOCR();
        rdr.displayImgReader(resultReader);
    }

    public static void extraImage(String imagename) throws Exception {
        Reader rdr = new Reader(imagename);
        List<List<Integer>> resultReader = rdr.dataOCR();
        ArrayList<ArrayList<String>> Text = rdr.getText(resultReader);
        Text = Translator.translistText(Text);
        Drawing dra = new Drawing(Reader.sorece, Text, resultReader);
        dra.drawingTextandSave();
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

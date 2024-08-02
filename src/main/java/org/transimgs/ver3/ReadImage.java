package org.transimgs.ver3;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadImage {
    static String sorece;
    static String model;

    ReadImage(String sorece, String model) {
        ReadImage.model = model;
        ReadImage.sorece = sorece;

    }

    ArrayList<ArrayList<String>> getText(List<List<Integer>> lis)
            throws IOException, InterruptedException, TesseractException, Exception {

        System.out.println("GetTexT Loading..");
        ITesseract instance = new Tesseract();
        instance.setDatapath("Training/tessdata/");
        instance.setLanguage(
                BubbleTextDetection.readJsonFile("config.json")
                        .getString("training-type")
        );
        instance.setTessVariable("user_defined_dpi", "300");
        ArrayList<ArrayList<String>> label = new ArrayList<ArrayList<String>>();
        BufferedImage readimg = ImageIO.read(new File(sorece));
        int ine = 1;

        for (List<Integer> ele : lis) {
            BufferedImage img = new BufferedImage(ele.get(2) - ele.get(0) + 5, ele.get(3) - ele.get(1) + 5,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D Wimg = (Graphics2D) img.getGraphics();
            Wimg.drawImage(readimg, -ele.get(0) + 5, -ele.get(1) + 5, null);
            ImageIO.write(img, "jpg", new File("Extracting/" + sorece.split("/")[sorece.split("/").length - 1] + "/imgs/" + ine + ".jpg"));
            ine++;
            try {
                String text = "";
                if (model.equals("api-ocr")) text = DoorsOcr(img).toLowerCase();
                if (model.equals("tess-ocr")) text = instance.doOCR(img).toLowerCase();
                String Arrtext[] = SpellCheckExample.CorrectText(text)
                        .replace(" -", "-")
                        .replace("- ", "-")
                        .replace("_", "")
                        .replace("~", "")
                        .replace("/", "")
                        .replace("\\", "")
                        .split("\n");
                ArrayList<String> ArrtxtList = new ArrayList<String>();
                ArrtxtList.addAll(Arrays.asList(Arrtext));
                label.add(ArrtxtList);

            } catch (Exception e) {
            }
        }

        System.out.println("Done!");
        return label;
    }

    public static String DoorsOcr(BufferedImage img) {
        String text = OCRSpaceExample.doOCR(img);
        return text;
    }
}

package org.transimgs.ver3;

import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ManualApp {

    public static void main(String[] args) throws Exception {
        if (
                !BubbleTextDetection.readJsonFile("config.json")
                        .getString("init")
                        .equals("")
        ) {
            String initfolder = BubbleTextDetection.readJsonFile("config.json")
                    .getString("init");
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
            try (Scanner sc = new Scanner(System.in)) {
                String initfolder = sc.nextLine();
                JSONObject js = BubbleTextDetection.readJsonFile("config.json");
                js.put("init", initfolder);
                BubbleTextDetection.writeJsonToFile("config.json", js);
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

    public static boolean doesFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    public static String readFileAsString(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }


    public static void extraImage(String imagename, String folderOut) throws Exception {
        System.out.println("-------------" + imagename.split("/")[imagename.split("/").length - 1] + "-------------");
        Runtime.getRuntime().exec("mkdir Extracting/" + imagename.split("/")[imagename.split("/").length - 1]);
        Runtime.getRuntime().exec("mkdir Extracting/" + imagename.split("/")[imagename.split("/").length - 1] + "/imgs");
        Thread.sleep(100);
        JSONObject resdata = new JSONObject();
        Reader rdr = new Reader(imagename);
        List<List<Integer>> resultReader = rdr.dataOCR();
        resdata.put("dataOCR", resultReader.size());
        resultReader = BubbleTextDetection.filterRect(ImageIO.read(new File(imagename)), resultReader);
        resdata.put("filterRect", resultReader.size());
        ReadImage im = new ReadImage(rdr.sorece,
                BubbleTextDetection.readJsonFile("config.json")
                        .getString("model")
        );
        ArrayList<ArrayList<String>> Text = im.getText(resultReader);
        resdata.put("Text", Text);
        Text = TransText.translistText(Text);
        resdata.put("Trans-Text", Text);
        Drawing dra = new Drawing(Reader.sorece, Text, resultReader);
        dra.drawingTextandSave(imagename.split("/")[imagename.split("/").length - 1], folderOut);
        BubbleTextDetection.writeJsonToFile("Extracting/" + imagename.split("/")[imagename.split("/").length - 1] + "/data.json", resdata);
    }

}

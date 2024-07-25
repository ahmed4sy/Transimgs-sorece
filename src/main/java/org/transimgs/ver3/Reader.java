/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.transimgs.ver3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.json.JSONException;
import org.json.JSONObject;

public class Reader {

    static String sorece;

    static void print(String txt) {
        System.out.print(txt);
    }

    static void println(String txt) {
        System.out.println(txt);
    }

    Reader(String sorece) {
        this.sorece = sorece;
    }

    static boolean Debug = false;

    void modeDebug() {
        Debug = true;
    }

    List<List<Integer>> dataOCR() throws JSONException, InterruptedException, IOException {
        print("dataOCR..");
        JSONObject js = BubbleTextDetection.readJsonFile("Jython/marge.json");
        if (js == null) {
            return null;
        }
        if (js.getBoolean("State") == false) {
            js.put("sorece", sorece);
            BubbleTextDetection.writeJsonToFile("Jython/marge.json", js);
            Runtime.getRuntime().exec("python3.10 Jython/main.py");
        }
        int sec = 25;
        while (true) {
            js = BubbleTextDetection.readJsonFile("Jython/marge.json");
            if (js.getBoolean("State")) {
                break;
            }
            Thread.sleep(1000);
            sec--;
            if (sec >= 0) {
                System.out.print("\rdataOCR.running.." + sec);
            } else {
                System.out.print("\rdataOCR.running..");
            }

        }
        System.out.print("\rdataOCR..running..");
        List<List<Integer>> dataOCR = new ArrayList<>();
        for (int i = 0; i < js.getJSONArray("dataOCR").length(); i++) {
            dataOCR.add(
                    BubbleTextDetection.parseStringToList(js.getJSONArray("dataOCR")
                            .get(i).toString())
            );
        }
        if (Debug) {
            System.out.println("dataOCR: " + dataOCR);
        }
        JSONObject retjs = new JSONObject();
        retjs.put("dataOCR", new ArrayList());
        retjs.put("sorece", "");
        retjs.put("State", false);
        BubbleTextDetection.writeJsonToFile("Jython/marge.json", retjs);
        println("Done!");
        return dataOCR;
    }

    ArrayList<ArrayList<Integer>> bubsOcr(boolean pythonRun) throws IOException, InterruptedException {
        String res;
        print("bubOcr..");
        if (pythonRun) {
            print("python..");
            Runtime.getRuntime()
                    .exec("rm /home/ahmed4s/NetBeansProjects/Transimgs/Jython/out.txt");
            Runtime.getRuntime()
                    .exec("touch /home/ahmed4s/NetBeansProjects/Transimgs/Jython/out.txt");
            Runtime.getRuntime()
                    .exec("python3.10 /home/ahmed4s/NetBeansProjects/Transimgs/Jython/main.py "
                            + sorece);
        }

        outfile:
        while (true) {
            File file = new File("/home/ahmed4s/NetBeansProjects/Transimgs/Jython/out.txt");
            try (FileReader out = new FileReader(file)) {
                char[] ch = new char[(int) file.length()];
                out.read(ch);
                if (file.length() != 0) {
                    Thread.sleep(2500);
                    res = charsTostr(ch);
                    break outfile;
                }
            }

        }

        ArrayList<ArrayList<Integer>> done = stringToList(res);
        if (Debug) {
            System.out.println(
                    "out: " + res + "\n"
                            + "Bubs list: " + done);
        }
        println("Done!");
        return done;
    }

    static String charsTostr(char[] os) {
        String res = "";
        for (char w : os) {
            res += w;
        }
        return res;
    }

    static ArrayList<ArrayList<Integer>> stringToList(String text) {
        String r = text
                .replace("[", "#")
                .replace("]", "#")
                .replace("), (", "#")
                .replace(")", "")
                .replace("(", "");

        ArrayList<String> arrP = new ArrayList<String>();
        String tmp = "";
        for (int i = 0; i < r.length(); i++) {
            if (r.charAt(i) == '#') {
                if (tmp.equals("") == false) {
                    arrP.add(tmp);
                }
                tmp = "";

            } else {
                tmp += r.charAt(i);
            }
        }
        ArrayList<ArrayList<Integer>> end = new ArrayList<ArrayList<Integer>>();
        for (String ele : arrP) {
            ArrayList<Integer> tmps = new ArrayList<Integer>();
            for (String nm : ele.split(",")) {
                try {
                    tmps.add(Integer.parseInt(nm.strip()));
                } catch (Exception e) {
                    tmps.add((int) Float.parseFloat(nm.strip()));
                }

            }
            end.add(tmps);
        }
        return end;
    }


    ArrayList<ArrayList<Integer>> imgToPieces(String sorece, int[] SplitPos) throws IOException {
        print("imgToPieces loading...");
        BufferedImage img = ImageIO.read(new File(sorece));
        int initX = 0;
        int initY = 0;
        int Xlop = initX;
        int Ylop = initY;
        int numPage = 1;
        ArrayList<ArrayList<Integer>> PiecesPos = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < SplitPos[1]; i++) {
            for (int j = 0; j < SplitPos[0]; j++) {
                BufferedImage voidimg = new BufferedImage(img.getWidth() / SplitPos[0], img.getHeight() / SplitPos[1],
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D w = (Graphics2D) voidimg.getGraphics();
                w.drawImage(img, Xlop, Ylop, null);
                ArrayList<Integer> pose = new ArrayList<Integer>();
                pose.add(Math.abs(Xlop));
                pose.add(Math.abs(Ylop));
                PiecesPos.add(pose);
                ImageIO.write(voidimg, "jpg", new File("imgs/grids/" + (numPage) + ".jpg"));
                w.dispose();
                Xlop += -(img.getWidth() / SplitPos[0]);
                numPage++;
            }
            Xlop = initX;
            Ylop += -(img.getHeight() / SplitPos[1]);

        }
        println("Done.");
        if (Debug == true) {
            System.out.println("PiecesPos: " + PiecesPos);
        }
        return PiecesPos;
    }

    int scanWordsinPieces(String sorece, int piece) throws IOException, InterruptedException {
        Runtime.getRuntime()
                .exec("tesseract " + sorece + " imgs/out -l eng");
        Thread.sleep(500);
        Process ocrResult = Runtime.getRuntime()
                .exec("cat imgs/out.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                ocrResult.getInputStream()));
        ArrayList<String> res = new ArrayList<String>();
        String line = "";
        int type = -1;
        while ((line = reader.readLine()) != null) {
            res.add(line);
        }
        forwords:
        for (String word : res) {
            if (word.strip().equals("") == false) {
                type = piece;
                break forwords;
            }
        }

        return type;

    }

    void displayImgReader(List<List<Integer>> data) throws IOException, InterruptedException {
        print("displayImgReader Loading..");
        BufferedImage readimg = ImageIO.read(new File(sorece));
        BufferedImage img = new BufferedImage(readimg.getWidth(), readimg.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D Wimg = (Graphics2D) img.getGraphics();
        Wimg.drawImage(readimg, 0, 0, null);
        for (List<Integer> place : data) {
            if (Debug == true) {
                System.out.println("gps: " + place);
            }
            Wimg.setColor(Color.red);
            Wimg.fillRect(place.get(0), place.get(1), place.get(2) - place.get(0),
                    place.get(3) - place.get(1));
        }
        ImageIO.write(img, "jpg", new File("displayPlacesRead.jpg"));
        Thread.sleep(500);
        Runtime.getRuntime().exec(
                "open displayPlacesRead.jpg");
        println("Done!");
    }

    void displayImgReader(String sorece, int[] SplitPos,
                          ArrayList<ArrayList<Integer>> PiecesPos,
                          ArrayList<Integer> wordplace)
            throws IOException {

        System.out.print("displayImgReader Loading..");
        BufferedImage readimg = ImageIO.read(new File(sorece));
        BufferedImage img = new BufferedImage(readimg.getWidth(), readimg.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D Wimg = (Graphics2D) img.getGraphics();
        Wimg.drawImage(readimg, 0, 0, null);
        int gpsX = 0;
        int gpsY = 0;
        for (int place : wordplace) {
            gpsX = PiecesPos.get(place - 1).get(0);
            gpsY = PiecesPos.get(place - 1).get(1);
            if (Debug == true) {
                System.out.println("gps: " + gpsX + ", " + gpsY);
            }
            Wimg.setColor(Color.red);
            Wimg.fillRect(gpsX, gpsY, readimg.getWidth() / SplitPos[0], readimg.getWidth() / SplitPos[1]);
        }
        ImageIO.write(img, "jpg", new File("imgs/img_grid.jpg"));
        System.out.println("Done!");

    }

    ArrayList<ArrayList<String>> getText(List<List<Integer>> lis)
            throws IOException, InterruptedException, TesseractException {
        print("GetTexT Loading..");
        ArrayList<ArrayList<String>> label = new ArrayList<ArrayList<String>>();
        BufferedImage readimg = ImageIO.read(new File(sorece));
        ITesseract instance = new Tesseract();
        instance.setLanguage("eng");
        instance.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata/");
        instance.setTessVariable("user_defined_dpi", "300");
        if (Debug) {
            int i = 1;
            String com = "rm imgs/debug/*";
            Runtime.getRuntime()
                    .exec(com);
            for (List<Integer> ele : lis) {
                BufferedImage img = new BufferedImage(ele.get(2) - ele.get(0), ele.get(3) - ele.get(1),
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D Wimg = (Graphics2D) img.getGraphics();
                Wimg.drawImage(readimg, -ele.get(0), -ele.get(1), null);
                ImageIO.write(img, "jpg", new File("imgs/debug/" + i + ".jpg"));
                i++;
            }
        } else {
            for (List<Integer> ele : lis) {
                BufferedImage img = new BufferedImage(ele.get(2) - ele.get(0), ele.get(3) - ele.get(1),
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D Wimg = (Graphics2D) img.getGraphics();
                Wimg.drawImage(readimg, -ele.get(0), -ele.get(1), null);
                String text = instance.doOCR(img);
                String Arrtext[] = text.toLowerCase()
                        .replace(" -", "-")
                        .replace("- ", "-")
                        .replace("_", "")
                        .replace("~", "")
                        .replace("/", "")
                        .replace("\\", "")
                        .replace(" ?", "?")
                        .split("\n");
                ArrayList<String> ArrtxtList = new ArrayList<String>();
                ArrtxtList.addAll(Arrays.asList(Arrtext));
                label.add(ArrtxtList);
            }
        }
        println("Done!");
        return label;
    }
}

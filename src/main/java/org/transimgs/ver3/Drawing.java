/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.transimgs.ver3;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.Word;

/**
 * @author ahmed4s
 */
public class Drawing {

    private ArrayList<ArrayList<String>> text;
    private List<List<Integer>> resultReader;
    String PATH;

    static void print(String txt) {
        System.out.print(txt);
    }

    static void println(String txt) {
        System.out.println(txt);
    }

    Drawing(String PATH, ArrayList<ArrayList<String>> text, List<List<Integer>> resultReader) {
        this.text = text;
        this.resultReader = resultReader;
        this.PATH = PATH;
    }

    ArrayList<String> WarpString(String txt, String Up) {
        ArrayList<String> stat = new ArrayList<>();
        String[] ty = txt.split(" ");
        ArrayList<String> lisFilter = new ArrayList<String>();
        for (String word : ty) {
            if (word.strip().equals("") == false) {
                lisFilter.add(word);
            }
        }
        String[] sty = new String[lisFilter.size()];
        for (int i = 0; i < lisFilter.size(); i++) {
            sty[i] = lisFilter.get(i);
        }
        String tmp = "";
        for (int i = 0; i < sty.length; i++) {
            if (sty[i] != "") {
                if (Up.equals("big") && tmp.length() < 4) {
                    stat.add(tmp);
                    tmp = "";
                    tmp += sty[i] + " ";
                } else if (tmp.length() > 10) {
                    stat.add(tmp);
                    tmp = "";
                    tmp += sty[i] + " ";
                } else if (sty[i].length() > 7 && tmp.length() > 5) {
                    stat.add(tmp);
                    tmp = "";
                    tmp += sty[i] + " ";
                    stat.add(tmp);
                    tmp = "";
                } else if (i % 2 == 0 && i > 0 && sty[i].length() < 4 && tmp.length() < 5) {
                    tmp += sty[i] + " ";
                    stat.add(tmp);
                    tmp = "";
                } else if ((i % 2 == 0 && i > 0) || tmp.split(" ").length >= 2) {
                    tmp += sty[i] + " ";
                    stat.add(tmp);
                    tmp = "";
                } else if (sty.length - 1 == i) {
                    tmp += sty[i];
                    stat.add(tmp);

                } else {
                    tmp += sty[i] + " ";
                }

            }
        }
        return stat;
    }

    void drawingTextandSave(String Nameimg, String outfolder) throws IOException, IndexOutOfBoundsException, InterruptedException {
        print("drawingTextandSave...");
        BufferedImage image = ImageIO.read(new File(PATH));
        Graphics2D W = image.createGraphics();
        int i = 0;
        for (List<Integer> reRe : resultReader) {
            int ms = (reRe.get(3) - reRe.get(1));
            int fontsize = (int) (((double) ms / 10) + 12.5);
            if (!text.isEmpty()) {
                W.setFont(new Font("Batang", 2, fontsize));
                W.setColor(Color.white);
                W.fillRect(reRe.get(0) + 5, reRe.get(1) + 5, reRe.get(2) - reRe.get(0) - 5,
                        (reRe.get(3) - reRe.get(1)) - 5);
                W.setColor(Color.BLACK);
                int space = 0;
                FontMetrics fma = W.getFontMetrics();
                for (String txs : text.get(i)) {
                    if (!txs.isEmpty()) {
                        Rectangle2D rect = fma.getStringBounds(txs, W);
                        int centerX = (reRe.get(2) + reRe.get(0) - (int) rect.getWidth()) / 2;
                        W.drawString(txs, centerX, reRe.get(1) + 30 + space);
                        space += (int) rect.getHeight() + 6;
                    }
                }

            }
            i++;

        }
        ImageIO.write(image, "jpg", new File(outfolder + Nameimg));
        Thread.sleep(500);
        println("Done!");
    }

}

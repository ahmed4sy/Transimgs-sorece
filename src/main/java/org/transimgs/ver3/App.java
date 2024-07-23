/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.transimgs.ver3;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import net.sourceforge.tess4j.TesseractException;

public class App {
//
//    public static void main(String args[]) throws IOException, InterruptedException, TesseractException {
//        String pathimg = "imgs/sorece/img.jpg";
//        Reader rdr = new Reader(pathimg);
//        System.out.println("$RUN$");
//        try {
//            switch (args[0]) {
//                case "--readimg":
//                    if (args[1].split("=")[0].equals("rdr")) {
//                        if (args[1].split("=")[1].equals("on")) {
//
//                            try {
//                                if (args[2].split("=")[0].equals("file")) {
//                                    rdr.sorece = args[2].split("=")[1];
//                                    ArrayList<ArrayList<Integer>> resultReader = rdr.bubsOcr(true);
//                                    rdr.displayImgReader(resultReader);
//                                }
//
//                            } catch (Exception e) {
//                                ArrayList<ArrayList<Integer>> resultReader = rdr.bubsOcr(true);
//                                rdr.displayImgReader(resultReader);
//
//                            }
//
//                        } else {
//                            try {
//                                if (args[2].split("=")[0].equals("file")) {
//                                    rdr.sorece = args[2].split("=")[1];
//                                    ArrayList<ArrayList<Integer>> resultReader = rdr.bubsOcr(false);
//                                    rdr.displayImgReader(resultReader);
//                                }
//
//                            } catch (Exception e) {
//                                ArrayList<ArrayList<Integer>> resultReader = rdr.bubsOcr(false);
//                                rdr.displayImgReader(resultReader);
//                            }
//
//                        }
//                    } else {
//                        try {
//                            if (args[2].split("=")[0].equals("file")) {
//                                rdr.sorece = args[2].split("=")[1];
//                                ArrayList<ArrayList<Integer>> resultReader = rdr.bubsOcr(false);
//                                rdr.displayImgReader(resultReader);
//                            }
//
//                        } catch (Exception e) {
//                            ArrayList<ArrayList<Integer>> resultReader = rdr.bubsOcr(false);
//                            rdr.displayImgReader(resultReader);
//
//                        }
//                    }
//                    break;
//                case "--extrimg":
//                    if (args[1].split("=")[0].equals("rdr")) {
//                        if (args[1].split("=")[1].equals("on")) {
//                            try {
//                                if (args[2].split("=")[0].equals("file")) {
//                                    rdr.sorece = args[2].split("=")[1];
//                                    ArrayList<ArrayList<Integer>> resultReader = rdr.bubsOcr(true);
//                                    ArrayList<ArrayList<String>> Text = rdr.getText(resultReader);
//                                    Drawing dra = new Drawing(rdr.sorece, Text, resultReader);
//                                    dra.drawingTextandSave();
//                                }
//
//                            } catch (Exception e) {
//                                ArrayList<ArrayList<Integer>> resultReader = rdr.bubsOcr(true);
//                                ArrayList<ArrayList<String>> Text = rdr.getText(resultReader);
//                                Drawing dra = new Drawing(rdr.sorece, Text, resultReader);
//                                dra.drawingTextandSave();
//                            }
//
//                        } else {
//                            try {
//                                if (args[2].split("=")[0].equals("file")) {
//                                    rdr.sorece = args[2].split("=")[1];
//                                    ArrayList<ArrayList<Integer>> resultReader = rdr.bubsOcr(false);
//                                    ArrayList<ArrayList<String>> Text = rdr.getText(resultReader);
//                                    Drawing dra = new Drawing(rdr.sorece, Text, resultReader);
//                                    dra.drawingTextandSave();
//                                }
//
//                            } catch (Exception e) {
//                                ArrayList<ArrayList<Integer>> resultReader = rdr.bubsOcr(false);
//                                ArrayList<ArrayList<String>> Text = rdr.getText(resultReader);
//                                Drawing dra = new Drawing(rdr.sorece, Text, resultReader);
//                                dra.drawingTextandSave();
//                            }
//                        }
//                    } else {
//                        try {
//                            if (args[1].split("=")[0].equals("file")) {
//                                rdr.sorece = args[1].split("=")[1];
//                                ArrayList<ArrayList<Integer>> resultReader = rdr.bubsOcr(false);
//                                ArrayList<ArrayList<String>> Text = rdr.getText(resultReader);
//                                Drawing dra = new Drawing(rdr.sorece, Text, resultReader);
//                                dra.drawingTextandSave();
//                            }
//
//                        } catch (Exception e) {
//                            ArrayList<ArrayList<Integer>> resultReader = rdr.bubsOcr(false);
//                            ArrayList<ArrayList<String>> Text = rdr.getText(resultReader);
//                            Drawing dra = new Drawing(rdr.sorece, Text, resultReader);
//                            dra.drawingTextandSave();
//                        }
//                    }
//                    break;
//                case "--sorece":
//                    Runtime.getRuntime().exec("open imgs/sorece");
//                    break;
//                default:
//                    FileReader help = new FileReader(new File("help.txt"));
//                    char[] text = new char[(int) new File("help.txt").length()];
//                    help.read(text);
//                    System.out.println(text);
//                    break;
//            }
//        } catch (Exception e) {
//            FileReader help = new FileReader(new File("help.txt"));
//            char[] text = new char[(int) new File("help.txt").length()];
//            help.read(text);
//            System.out.println(text);
//        }
//        System.out.println("$END$");
//    }

}

// Reader rdr = new Reader();
// int[] SplitPos = { 15, 20 };
// String sorece = "imgs/img.jpg";
// ArrayList<ArrayList<Integer>> PiecesPos = rdr.imgToPieces(sorece, SplitPos);
// ArrayList<Integer> gridimgs = new ArrayList<>();
// System.out.print("Scan Loading..");
// for (int i = 0; i < SplitPos[0] * SplitPos[1]; i++) {
// int req = rdr.scanWordsinPieces("imgs/grids/" +
// (i + 1) + ".jpg", (i + 1));
// if (req != -1)
// gridimgs.add(req);
// switch (i + 1) {
// case 50:
// System.out.print("%25..");
// break;
// case 100:
// System.out.print("%50..");
// break;
// case 250:
// System.out.print("%75..");
// break;
// default:
// break;
// }
// }
// System.out.println("Done");
// rdr.displayImgReader(sorece, SplitPos, PiecesPos, gridimgs);

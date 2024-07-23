package org.transimgs.ver3;

import java.awt.Graphics2D;

import net.sourceforge.tess4j.*;

import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ReadingImage {

    public static List<Word> processImg(BufferedImage inputImage, float scaleFactor, float offset)
            throws IOException, TesseractException {
        // We will create an image buffer
        // for storing the image later on
        // and inputImage is an image buffer
        // of input image
        BufferedImage outputImage = new BufferedImage(1050, 1024, inputImage.getType());
        // Now, for drawing the new image
        // we will create a 2D platform
        // on the buffer image
        Graphics2D grp = outputImage.createGraphics();
        // drawing a new zoomed image starting
        // from 0 0 of size 1050 x 1024
        // and null is the ImageObserver class object
        grp.drawImage(inputImage, 0, 0, 1050, 1024, null);
        grp.dispose();
        // for the gray scaling of images
        // we'll use RescaleOp object
        RescaleOp rescaleOutput = new RescaleOp(scaleFactor, offset, null);
        // Here, we are going to perform
        // scaling of the image and then
        // writing on a .jpg file
        BufferedImage finalOutputimage = rescaleOutput.filter(outputImage, null);
        // Creating an instance of Tesseract class
        // that will be used to perform OCR
        Tesseract tesseractInstance = new Tesseract();
        tesseractInstance.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata/");
        tesseractInstance.setLanguage("eng");
        // finally performing OCR on the image
        // and then storing the result in 'str' string
        return tesseractInstance.getWords(finalOutputimage, ITessAPI.TessPageIteratorLevel.RIL_WORD);
    }

    public static List<Word> doOCRmaster(BufferedImage inputImage) throws Exception {

        double d = inputImage.getRGB(inputImage.getTileWidth() / 2,
                inputImage.getTileHeight() / 2);
        // now, we'll compare the values and
        // set up new scaling values
        // which will be use by RescaleOp later on
        if (d >= -1.4211511E7 && d < -7254228) {
            return processImg(inputImage, 3f, -10f);
        } else if (d >= -7254228 && d < -2171170) {
            return processImg(inputImage, 1.455f, -47f);
        } else if (d >= -2171170 && d < -1907998) {
            return processImg(inputImage, 1.35f, -10f);
        } else if (d >= -1907998 && d < -257) {
            return processImg(inputImage, 1.19f, 0.5f);
        } else if (d >= -257 && d < -1) {
            return processImg(inputImage, 1f, 0.5f);
        } else if (d >= -1 && d < 2) {
            return processImg(inputImage, 1f, 0.35f);
        }

        return null;
    }
}


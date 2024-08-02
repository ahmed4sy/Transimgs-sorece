package org.transimgs.ver3;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class text {
    public static void main(String[] args) throws IOException {

        System.out.println(
                OCRSpaceExample.doOCR(
                        ImageIO.read(new File("imgs/1.jpg"))
                )
        );
    }

    public static List<String> splitTextByPositions(String text, List<Integer> positions) {
        List<String> result = new ArrayList<>();
        String[] words = text.split(" ");
        int start = 0;
        int fanStart = 0;
        for (int pos : positions) {
            String tmp = "";
            int timer = 0;
            for (int i = start; i < words.length; i++) {
                if (timer == pos) break;
                else {
                    tmp += words[i] + " ";
                    timer++;
                    fanStart++;
                }
            }
            result.add(tmp.strip());
            start = fanStart;


        }
        return result;
    }
}

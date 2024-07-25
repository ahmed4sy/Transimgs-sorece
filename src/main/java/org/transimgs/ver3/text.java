package org.transimgs.ver3;

import java.util.ArrayList;
import java.util.List;

public class text {
    public static void main(String[] args) {
        String text = "hi iam ahmed and my name is ahmed";
        List<Integer> positions = List.of(2, 1, 2);

        List<String> result = splitTextByPositions(text, positions);
        System.out.println(result); // Output: [ahmed, hi iam]
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

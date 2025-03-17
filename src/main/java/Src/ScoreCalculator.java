package Src;

import java.util.Arrays;
import java.util.List;

public class ScoreCalculator {
    public static double calculateScore(String resumeText, String jobDescription) {
        List<String> resumeWords = Arrays.asList(resumeText.toLowerCase().split("\\s+"));
        List<String> jobWords = Arrays.asList(jobDescription.toLowerCase().split("\\s+"));

        int matchCount = 0;
        for (String word : jobWords) {
            if (resumeWords.contains(word)) {
                matchCount++;
            }
        }
        return (double) matchCount / jobWords.size() * 100;
    }
}

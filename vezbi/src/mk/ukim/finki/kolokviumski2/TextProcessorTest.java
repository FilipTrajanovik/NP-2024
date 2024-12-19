package mk.ukim.finki.kolokviumski2;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

class TextProcessor {

    private List<String> texts;  // List to store the raw texts
    private List<Map<String, Integer>> textsWordFrequency;  // List to store word counts per text
    private Map<String, Integer> corpusWordFrequency;  // To store aggregated word frequency across all texts

    public TextProcessor() {
        texts = new ArrayList<>();
        textsWordFrequency = new ArrayList<>();
        corpusWordFrequency = new HashMap<>();
    }

    public void readText(InputStream is) {
        Scanner sc = new Scanner(is);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            texts.add(line);  // Store the raw text
            // Remove punctuation and convert to lowercase
            line = line.replaceAll("[^a-zA-Zа-яА-Я ]", "").toLowerCase();
            String[] words = line.split("\\s+");

            Map<String, Integer> wordCountMap = new HashMap<>();
            for (String word : words) {
                if (!word.isEmpty()) {
                    wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                    corpusWordFrequency.put(word, corpusWordFrequency.getOrDefault(word, 0) + 1);
                }
            }
            textsWordFrequency.add(wordCountMap);
        }
    }

    public void printTextsVectors(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);

        // Sort vocabulary lexicographically (from the corpus word frequency)
        List<String> sortedVocabulary = new ArrayList<>(corpusWordFrequency.keySet());
        Collections.sort(sortedVocabulary);

        // Print the vector for each text
        for (Map<String, Integer> textWordCount : textsWordFrequency) {
            List<Integer> vector = new ArrayList<>();
            for (String word : sortedVocabulary) {
                // For words that don't appear in the text, use 0 as the frequency
                vector.add(textWordCount.getOrDefault(word, 0));
            }
            pw.println(vector);
        }
        pw.flush();
    }

    public void printCorpus(OutputStream os, int n, boolean ascending) {
        PrintWriter pw = new PrintWriter(os);

        // Sort the corpus word frequency map by frequency (ascending or descending)
        List<Map.Entry<String, Integer>> wordList = new ArrayList<>(corpusWordFrequency.entrySet());
        wordList.sort((entry1, entry2) -> {
            if (ascending) {
                return entry1.getValue().compareTo(entry2.getValue());
            } else {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        // Print the top n words in the correct order
        int count = 0;
        for (Map.Entry<String, Integer> entry : wordList) {
            if (count >= n) break;
            pw.println(entry.getKey() + " : " + entry.getValue());
            count++;
        }

        pw.flush();
    }

    public void mostSimilarTexts(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        double maxSimilarity = -1;
        int mostSimilarIndex1 = -1;
        int mostSimilarIndex2 = -1;

        // Create the vocabulary for comparison (same set of words for every text)
        Set<String> vocabulary = corpusWordFrequency.keySet();

        for (int i = 0; i < textsWordFrequency.size(); i++) {
            Map<String, Integer> vector1 = textsWordFrequency.get(i);
            for (int j = i + 1; j < textsWordFrequency.size(); j++) {
                Map<String, Integer> vector2 = textsWordFrequency.get(j);

                // Create vectors for cosine similarity, ensuring they have the same length
                List<Integer> vector1Values = new ArrayList<>();
                List<Integer> vector2Values = new ArrayList<>();

                for (String word : vocabulary) {
                    vector1Values.add(vector1.getOrDefault(word, 0));
                    vector2Values.add(vector2.getOrDefault(word, 0));
                }

                double similarity = CosineSimilarityCalculator.cosineSimilarity(vector1Values, vector2Values);

                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    mostSimilarIndex1 = i;
                    mostSimilarIndex2 = j;
                }
            }
        }

        // Print the full text for the two most similar texts
        pw.println(texts.get(mostSimilarIndex1));  // Full text 1
        pw.println(texts.get(mostSimilarIndex2));  // Full text 2
        pw.printf("%.10f\n", maxSimilarity);  // Print cosine similarity with required precision
        pw.flush();
    }
}

class CosineSimilarityCalculator {
    public static double cosineSimilarity(List<Integer> vector1, List<Integer> vector2) {
        double up = 0.0;
        double down1 = 0, down2 = 0;

        for (int i = 0; i < vector1.size(); i++) {
            up += (vector1.get(i) * vector2.get(i));
        }

        for (int i = 0; i < vector1.size(); i++) {
            down1 += (vector1.get(i) * vector1.get(i));
        }

        for (int i = 0; i < vector2.size(); i++) {
            down2 += (vector2.get(i) * vector2.get(i));
        }

        return up / (Math.sqrt(down1) * Math.sqrt(down2));
    }
}

public class TextProcessorTest {
    public static void main(String[] args) {
        TextProcessor textProcessor = new TextProcessor();

        textProcessor.readText(System.in);

        System.out.println("===PRINT VECTORS===");
        textProcessor.printTextsVectors(System.out);

        System.out.println("PRINT FIRST 20 WORDS SORTED ASCENDING BY FREQUENCY");
        textProcessor.printCorpus(System.out, 20, true);

        System.out.println("PRINT FIRST 20 WORDS SORTED DESCENDING BY FREQUENCY");
        textProcessor.printCorpus(System.out, 20, false);

        System.out.println("===MOST SIMILAR TEXTS===");
        textProcessor.mostSimilarTexts(System.out);
    }
}

package mk.ukim.finki.labs.lab6;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Anagrams {

    public static void main(String[] args) {
        findAll(System.in);
    }

    public static void findAll(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        Map<String, Set<String>> map = new LinkedHashMap<>();

        String line;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            // Sort the characters of the word to generate the key for anagram groups
            String sortedWord = sortCharacters(line);

            // Add the word to the set of anagrams corresponding to its sorted character key
            if(!map.containsKey(sortedWord)) {
                map.put(sortedWord, new LinkedHashSet<>());
            }
            map.get(sortedWord).add(line);
        }

        // Iterate through the map and print words with more than 5 anagrams
        for (Set<String> value : map.values()) {
            if (value.size() >= 5) {
                System.out.println(String.join(" ", value));
            }
        }
    }

    // Helper function to sort the characters of a string
    private static String sortCharacters(String word) {
        char[] characters = word.toCharArray();
        Arrays.sort(characters);
        return new String(characters);
    }
}

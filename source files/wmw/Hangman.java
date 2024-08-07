package wmw;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The Hangman class is where the AI Agent is housed.
 * All computation takes place in this class.
 * Methods: analyze, getPercentage, readDictionary, setup, getCurrentWord,
 * trimDictionary, newLetters, badLetter, getDictionaryLength, reset.
 */
public class Hangman {
    public static StringBuilder currentWord; // The actuator for the agent, the currentWord is updated by the agent
    public static List<String> currentDictionary; // The environment for the agent
    public static double percentage; // Used for aesthetic

    /**
     * This program is for analyzing the current word list and picking the best next guess.
     * @param words the list of possible words (currentDictionary)
     * @param knownLetters the list of letters that are already known
     * @return The best guess, a char
     */
    public static char analyze(List<String> words, String knownLetters) {
        Map<Character, Integer> characterFrequency = new HashMap<>();
        Set<Character> knownLettersSet = new HashSet<>();

        // Creates a set of unique characters in the knownLetters string
        for (char c : knownLetters.toCharArray()) {
            if (Character.isLetter(c)) {
                knownLettersSet.add(Character.toLowerCase(c));
            }
        }

        // Counts the number of words that contain each character, excluding knownLetters
        for (String word : words) {
            Set<Character> uniqueChars = new HashSet<>();
            for (char c : word.toCharArray()) {
                if (Character.isLetter(c)) {
                    uniqueChars.add(Character.toLowerCase(c));
                }
            }

            uniqueChars.removeAll(knownLettersSet);

            for (char c : uniqueChars) {
                characterFrequency.put(c, characterFrequency.getOrDefault(c, 0) + 1);
            }
        }

        // Find the character with the highest frequency
        char mostCommonCharacter = ' ';
        int maxFrequency = 0;
        for (char c : characterFrequency.keySet()) {
            int frequency = characterFrequency.get(c);
            if (frequency > maxFrequency) {
                mostCommonCharacter = c;
                maxFrequency = frequency;
            }
        }

        percentage = (double) maxFrequency / words.size() * 100;
        return mostCommonCharacter;
    }

    // Returns the percentage likelihood that a word contains a certain letter
    public static double getPercentage() {
        return percentage;
    }

    /**
     * This method is for reading in the dictionary. You can edit the file to be anything you want, such as a French dictionary.
     * @return List<String> of words
     */
    private static List<String> readDictionary() {
        List<String> dictionary = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("D:\\Artificial Intelligence Course\\EnglishWords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dictionary.add(line.toLowerCase()); // Store words in lowercase for case-insensitive filtering
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dictionary;
    }

    // Initializes the class
    public static void setup(int length) {
        currentDictionary = readDictionary();
        trimDictionary(length);

        StringBuilder wordBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            wordBuilder.append("_");
        }
        currentWord = wordBuilder;
    }

    // returns the currentWord
    public static String getCurrentWord() {
        return currentWord.toString();
    }

    // Trims the dictionary to just words that equal the user-inputted length, so that only valid words remain
    public static void trimDictionary(int length) {
        currentDictionary.removeIf(s -> s.length() != length);
    }

    // This method is used after the program has correctly guessed a letter.
    // The letter and the positions are then used to further trim the dictionary to only matching words.
    public static void newLetters(char guess, String[] positions) {
        // Filter words based on the provided positions and guessed letter
        List<String> filteredDictionary = new ArrayList<>();
        for (String word : currentDictionary) {
            boolean isValid = true;
            StringBuilder updatedWordBuilder = new StringBuilder(currentWord);
            for (String position : positions) {
                int index = Integer.parseInt(position.trim()) - 1;
                if (index < 0 || index >= word.length() || word.charAt(index) != guess) {
                    isValid = false;
                    break;
                }
                updatedWordBuilder.setCharAt(index, guess);
            }
            if (isValid) {
                filteredDictionary.add(word);
            }
            currentWord = updatedWordBuilder;
        }
        currentDictionary = filteredDictionary;

    }

    // Removes words from the dictionary that contain a letter that is known to not exist
    public static void badLetter(char guess) {
        currentDictionary.removeIf(word -> word.indexOf(guess) != -1);
    }

    // Returns the size of the dictionary, how many possible words there currently are
    public static int getDictionaryLength() {
        return currentDictionary.size();
    }

    // Used to reset the class if the user wishes to play again
    public static void reset() {
        currentWord = new StringBuilder();
        currentDictionary = new ArrayList<>();
    }
}

package lab3;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Translator {
    private final Map<String, String> dictionary;

    public Translator(Map<String, String> dictionary) {
        this.dictionary = dictionary;
    }

    public Translator(String dictionaryPath) throws InvalidFileFormatException, FileReadException {
        this.dictionary = loadDictionary(dictionaryPath);
    }

    public static Map<String, String> loadDictionary(String filename) throws InvalidFileFormatException, FileReadException {
        try {
            var lines = Files.readAllLines(Paths.get(filename));

            return makeDictionary(lines);
        } catch (IOException e) {
            throw new FileReadException("Ошибка чтения файла: " + filename, e);
        }
    }

    public static Map<String, String> parseDictionary(String rawText) throws InvalidFileFormatException  {
        return makeDictionary(List.of(rawText.split("\n")));
    }

    private static Map<String, String> makeDictionary(List<String> lines) throws InvalidFileFormatException {
        Map<String, String> dictionary = new HashMap<>();

        for (String line : lines) {
            if (!line.contains("|"))
                throw new InvalidFileFormatException("Неверный формат строки: " + line);

            String[] parts = line.split("\\|");
            if (parts.length != 2)
                throw new InvalidFileFormatException("Неверный формат строки: " + line);

            String key = parts[0].trim().toLowerCase();
            String value = parts[1].trim();
            dictionary.put(key, value);
        }

        return dictionary;
    }

    public String translate(String text) {
        String lowerText = text.toLowerCase();
        StringBuilder result = new StringBuilder();

        String[] words = lowerText.split(" ");
        int i = 0;

        while (i < words.length) {
            String match = null;
            String translation = null;

            // Поиск максимально длинной фразы из словаря
            for (int j = words.length; j > i; j--) {
                String phrase = String.join(" ", Arrays.copyOfRange(words, i, j));
                if (dictionary.containsKey(phrase)) {
                    match = phrase;
                    translation = dictionary.get(phrase);
                    i = j;
                    break;
                }
            }

            if (match != null) {
                result.append(translation).append(" ");
            } else {
                result.append(words[i]).append(" ");
                i++;
            }
        }

        return result.toString().trim();
    }
}
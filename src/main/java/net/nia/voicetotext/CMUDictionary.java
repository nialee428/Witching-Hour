package net.nia.voicetotext;

import java.io.*;
import java.util.*;

public class CMUDictionary {

    public final Map<String, List<String>> wordToPhonemes =
            new HashMap<>();


    public CMUDictionary() throws IOException {
        loadDictionary();
    }



    private void loadDictionary() throws IOException {

        InputStream inputStream =
                getClass()
                        .getClassLoader()
                        .getResourceAsStream("cmudict.txt");


        if (inputStream == null) {
            throw new FileNotFoundException(
                    "cmudict.txt not found"
            );
        }


        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(inputStream)
                );


        String line;


        while ((line = reader.readLine()) != null) {

            if (line.startsWith(";;;"))
                continue;


            String[] parts =
                    line.trim().split("\\s+", 2);


            if (parts.length != 2)
                continue;


            String word =
                    parts[0]
                            .toLowerCase()
                            .replaceAll("\\(\\d+\\)", "");


            List<String> phonemes =
                    Arrays.asList(
                            parts[1].split(" ")
                    );


            wordToPhonemes.putIfAbsent(
                    word,
                    phonemes
            );
        }


        reader.close();
    }



    public List<String> getPhonemes(String word) {

        return wordToPhonemes.getOrDefault(
                word.toLowerCase(),
                Collections.emptyList()
        );
    }
}
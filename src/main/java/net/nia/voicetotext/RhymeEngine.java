package net.nia.voicetotext;

import java.util.*;

public class RhymeEngine {

    public static boolean doWordsLooselyRhyme(String word1, String word2) {
        return rhymeScore(word1, word2) >= 0.65;
    }


    public static double rhymeScore(String word1, String word2) {

        CMUDictionary cmuDict = DictionaryHolder.get();

        if (cmuDict == null || tooSimilar(word1, word2))
            return 0;


        List<String> phonemes1 = cmuDict.getPhonemes(word1);
        List<String> phonemes2 = cmuDict.getPhonemes(word2);


        if (phonemes1.isEmpty() || phonemes2.isEmpty())
            return 0;


        String rhyme1 = extractRhymePhoneme(phonemes1);
        String rhyme2 = extractRhymePhoneme(phonemes2);


        if (rhyme1.isEmpty() || rhyme2.isEmpty())
            return 0;


        return compareRhymeParts(rhyme1, rhyme2);
    }



    private static double compareRhymeParts(String a, String b) {

        String[] p1 = normalizePhonemes(a);
        String[] p2 = normalizePhonemes(b);


        // Exact rhyme
        if (Arrays.equals(p1, p2))
            return 1.0;


        int matching = countMatchingEndingPhonemes(p1, p2);


        if (matching == 0)
            return 0;


        /*
         * One matching phoneme is too weak.
         * Prevents:
         *
         * to     UW1
         * undo   UW1
         *
         * from matching.
         */
        if (matching == 1)
            return 0.3;


        double score = 0.5;


        // reward additional matching sounds
        score += matching * 0.15;


        return Math.min(score, 0.95);
    }



    private static int countMatchingEndingPhonemes(
            String[] a,
            String[] b
    ) {

        int count = 0;

        int i = a.length - 1;
        int j = b.length - 1;


        while (i >= 0 && j >= 0) {

            if (!phonemeMatches(a[i], b[j]))
                break;


            count++;

            i--;
            j--;
        }


        return count;
    }



    private static boolean phonemeMatches(
            String a,
            String b
    ) {

        // exact match
        if (a.equals(b))
            return true;


        // vowel flexibility
        String baseA = removeStress(a);
        String baseB = removeStress(b);


        return baseA.equals(baseB);
    }



    public static String extractRhymePhoneme(List<String> phonemes) {

        int stressIndex = -1;


        // Prefer primary stress
        for (int i = phonemes.size() - 1; i >= 0; i--) {

            if (phonemes.get(i).matches(".*1")) {
                stressIndex = i;
                break;
            }
        }


        // fallback secondary stress
        if (stressIndex == -1) {

            for (int i = phonemes.size() - 1; i >= 0; i--) {

                if (phonemes.get(i).matches(".*2")) {
                    stressIndex = i;
                    break;
                }
            }
        }


        // no stress found
        if (stressIndex == -1)
            return phonemes.get(phonemes.size() - 1);


        return String.join(
                " ",
                phonemes.subList(
                        stressIndex,
                        phonemes.size()
                )
        );
    }



    private static String[] normalizePhonemes(String input) {

        return Arrays.stream(input.split(" "))
                .map(RhymeEngine::removeStress)
                .toArray(String[]::new);
    }



    private static String removeStress(String phoneme) {

        return phoneme.replaceAll("\\d", "");
    }



    private static boolean tooSimilar(String a, String b) {

        return a.equalsIgnoreCase(b)
                || a.equalsIgnoreCase(b + "s")
                || b.equalsIgnoreCase(a + "s");
    }
}
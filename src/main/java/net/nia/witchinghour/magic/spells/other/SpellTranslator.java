package net.nia.witchinghour.magic.spells.other;

import net.minecraft.client.MinecraftClient;
import net.nia.voicetotext.RhymeEngine;
import net.nia.witchinghour.magic.spells.create.SpellClient;

import java.util.Arrays;
import java.util.Set;

public class SpellTranslator {

    private static final Set<String> CONJUNCTIONS = Set.of(
            "and",
            "but",
            "so",
            "or",
            "nor",
            "yet",
            "make",
            "makes",
            "made",
            "making",
            "will",
            "willed",
            "wills",
            "be",
            "now",
            "let",
            "to",
            "thus",
            "for",
            "by",
            "then",
            "cast"
    );

    public static boolean isConjunction(String word) {
        return CONJUNCTIONS.contains(
                word.toLowerCase()
        );
    }

    /**
     * Checks whether the supplied text exactly matches
     * an ACTION spell keyword.
     *
     * This supports both:
     *
     * "fire"
     *
     * and:
     *
     * "snow golem"
     */
    public static boolean isActionWord(String text) {

        for (Spell spell : SpellRegistry.SPELLS) {

            if (spell.type != SpellType.ACTION
                    && spell.type != SpellType.BLACK_MAGIC
                    && spell.type != SpellType.BLUE_MAGIC
                    && spell.type != SpellType.GREEN_MAGIC
                    && spell.type != SpellType.PURPLE_MAGIC
                    && spell.type != SpellType.RED_MAGIC
                    && spell.type != SpellType.WHITE_MAGIC
            ) {
                continue;
            }

            for (String keyword : spell.keywords) {

                if (keyword.equalsIgnoreCase(text)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns the number of words in the longest ACTION
     * keyword beginning at the supplied position.
     *
     * Example:
     *
     * Keywords:
     *     "snow"
     *     "snow golem"
     *
     * Input:
     *     "snow golem"
     *
     * Returns:
     *     2
     *
     * This means "snow golem" wins over "snow".
     */
    private static int findActionPhraseLength(
            String[] words,
            int start
    ) {
        int longestMatch = 0;

        for (Spell spell : SpellRegistry.SPELLS) {

            if (spell.type != SpellType.ACTION
                    && spell.type != SpellType.BLACK_MAGIC
                    && spell.type != SpellType.BLUE_MAGIC
                    && spell.type != SpellType.GREEN_MAGIC
                    && spell.type != SpellType.PURPLE_MAGIC
                    && spell.type != SpellType.RED_MAGIC
                    && spell.type != SpellType.WHITE_MAGIC
            ) {
                continue;
            }

            for (String keyword : spell.keywords) {

                String[] keywordWords =
                        keyword.toLowerCase()
                                .trim()
                                .split("\\s+");

                int keywordLength = keywordWords.length;

                if (start + keywordLength > words.length) {
                    continue;
                }

                boolean matches = true;

                for (int i = 0; i < keywordLength; i++) {

                    if (!words[start + i]
                            .equals(keywordWords[i])) {

                        matches = false;
                        break;
                    }
                }

                if (matches && keywordLength > longestMatch) {
                    longestMatch = keywordLength;
                }
            }
        }

        return longestMatch;
    }

    /**
     * Gets the actual ACTION keyword beginning at start.
     */
    private static String findActionPhrase(
            String[] words,
            int start
    ) {
        int longestLength =
                findActionPhraseLength(words, start);

        if (longestLength == 0) {
            return null;
        }

        return String.join(
                " ",
                Arrays.copyOfRange(
                        words,
                        start,
                        start + longestLength
                )
        );
    }

    /**
     * Finds the beginning of the longest ACTION keyword.
     */
    private static int findActionStart(
            String[] words
    ) {
        for (int i = 0; i < words.length; i++) {

            if (findActionPhraseLength(words, i) > 0) {
                return i;
            }
        }

        return -1;
    }

    public static String translateSpell(String result) {

        assert MinecraftClient.getInstance().player != null;

        /*
         * Normalize the speech.
         *
         * Punctuation is removed, but words remain separate.
         */
        String[] words = result
                .toLowerCase()
                .replaceAll("[^a-zA-Z ]", " ")
                .trim()
                .split("\\s+");

        if (words.length == 0) {
            return "";
        }

        /*
         * The final word is still used as the rhyme word,
         * preserving your existing spell syntax.
         */
        String last = words[words.length - 1];

        String rhymeWord = null;
        int rhymeIndex = -1;

        /*
         * Find the first word that loosely rhymes with
         * the final word.
         */
        for (int i = words.length - 1; i >= 0; i--) {

            if (RhymeEngine.doWordsLooselyRhyme(
                    words[i],
                    last
            )) {

                rhymeIndex = i;
                rhymeWord = words[i];

                break;
            }
        }

        /*
         * Find an ACTION keyword.
         *
         * This supports:
         *
         * "fire"
         * "snow golem"
         * "wither skeleton"
         * "iron golem"
         *
         * and any other multi-word keyword.
         */
        int actionStart = findActionStart(words);

        if (actionStart == -1) {
            return "";
        }

        int actionLength =
                findActionPhraseLength(
                        words,
                        actionStart
                );

        String actionPhrase =
                findActionPhrase(
                        words,
                        actionStart
                );

        boolean hasAction = actionLength > 0;

        System.out.println(
                "[WitchingHour] Action: "
                        + actionPhrase
        );

        /*
         * We need a valid rhyme.
         */
        if (
                rhymeWord == null
                        || rhymeIndex == -1
                        || last.equals(rhymeWord)
                        || !hasAction
        ) {
            return "";
        }

        /*
         * The word immediately following the rhyme
         * determines whether this is a valid spell phrase.
         */
        int nextIndex = rhymeIndex + 1;

        if (nextIndex >= words.length) {
            return "";
        }

        String nextWord = words[nextIndex];

        boolean nextIsAction =
                isActionWord(nextWord);

        boolean nextIsConjunction =
                isConjunction(nextWord);

        System.out.println(
                "[WitchingHour] "
                        + "Next: " + nextWord
                        + " | Action: " + nextIsAction
                        + " | Conjunction: "
                        + nextIsConjunction
        );

        /*
         * Preserve your original validation.
         */
        if (!nextIsAction && !nextIsConjunction) {
            return "";
        }

        /*
         * Store the successful result.
         */
        SpellClient.updateLastResult(result);

        /*
         * Instead of:
         *
         * result.split(nextWord)[1]
         *
         * use the actual token index.
         *
         * This prevents partial-word matches such as:
         *
         * "go" -> "golem"
         */
        StringBuilder translated =
                new StringBuilder();

        for (int i = nextIndex; i < words.length; i++) {

            if (translated.length() > 0) {
                translated.append(" ");
            }

            translated.append(words[i]);
        }

        return translated.toString();
    }
}
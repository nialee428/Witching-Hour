package net.nia.witchinghour.magic.spells.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeywordHelper {


    public static String[] multipleSubjectsKeywords = new String[] {
            "all", "several", "multiple", "these", "us", "our", "any", "some", "many", "countless", "items", "every",
            "wall", "walls", "pillar", "sphere", "spheres", "circles", "circle", "square", "squares", "cube", "cubes",
            "dome", "domes", "rectangle", "rectangular", "rectangles", "floor", "ceiling", "floors", "ceilings", "square",
            "plane"
    };

    public static String[] wallKeywords = new String[] {
            "wall", "walls"
    };

    public static String[] pillarKeywords = new String[] {
            "pillar", "pillars"
    };

    public static String[] sphereKeyword = new String[] {
            "all", "several", "multiple", "these", "us", "our", "any", "some", "many", "countless", "items", "every",
            "sphere", "spheres", "circles", "circle"
    };

    public static String[] squareKeyword = new String[] {
            "square", "squares", "cube", "cubes"
    };

    public static String[] rectangleKeyword = new String[] {
            "rectangle", "rectangular", "rectangles"
    };

    public static String[] domeKeyword = new String[] {
            "dome", "domes"
    };

    public static String[] planeKeyword = new String[] {
            "floor", "ceiling", "floors", "ceilings", "square", "plane"
    };

    public static boolean isActionWord(String word) {
        if (word == null) {
            return false;
        }

        for (Spell spell : SpellRegistry.SPELLS) {
            if (spell.type == SpellType.ACTION) {
                for (String keyword : spell.keywords) {
                    if (keyword.equalsIgnoreCase(word)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isTargetWord(String word) {
        if (word == null) {
            return false;
        }

        for (Spell spell : SpellRegistry.SPELLS) {
            if (spell.type == SpellType.ENTITY || spell.type == SpellType.GENERAL || spell.type == SpellType.ITEM || spell.type == SpellType.SELF) {
                for (String keyword : spell.keywords) {
                    if (keyword.equalsIgnoreCase(word)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Spell hasTargetWord(String res, SpellType desiredTarget) {

        if (res == null) {
            return null;
        }

        String[] words = res
                .toLowerCase()
                .replaceAll("[^a-zA-Z ]", "")
                .split("\\s+");

        for (int i = words.length - 1; i >= 0; i--) {
            for (Spell sp : SpellRegistry.SPELLS) {
                for (String keyword : sp.keywords) {
                    if (keyword.equalsIgnoreCase(words[i])) {
                        if (sp.type == desiredTarget) {
                            return sp;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static String checkMS(String spell) {

        if (spell == null) {
            return "";
        }

        String[] words = spell
                .toLowerCase()
                .replaceAll("[^a-zA-Z ]", "")
                .split("\\s+");

        for (int i = words.length - 1; i >= 0; i--) {
            String w = words[i];
            if (Arrays.stream(multipleSubjectsKeywords).anyMatch(s -> s.equalsIgnoreCase(w))) {
                return w;
            }
        }

        return "";

    }

    public static boolean isModifierWord(String word) {
        if (word == null) {
            return false;
        }

        for (Spell spell : SpellRegistry.SPELLS) {
            if (spell.type == SpellType.MODIFIER) {
                for (String keyword : spell.keywords) {
                    if (keyword.equalsIgnoreCase(word)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<Spell> isKeyword(String res) {
        List<Spell> spells = new ArrayList<>();

        if (res == null) {
            return spells;
        }

        String[] words = res
                .toLowerCase()
                .replaceAll("[^a-zA-Z ]", "")
                .split("\\s+");
        String last = words[words.length - 1];

        for (String word : words) {
            for (Spell sp : SpellRegistry.SPELLS) {
                for (String keyword : sp.keywords) {
                    if (keyword.equalsIgnoreCase(word)) {
                        spells.add(sp);
                    }
                }
            }
        }

        return spells;
    }

}
package org.example.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author aram.azatyan | 2/14/2024 9:22 AM
 */
public class SentenceUtils {
    public static String NOT = "!";
    public static String AND = "&";
    public static String OR = "|";
    public static String IMPLICATION = "=>";
    public static String BICONDITIONAL = "<=>";

    public static char OPENING_PARENTHESES = '(';
    public static char CLOSING_PARENTHESES = ')';

    public static final Set<String> CONNECTIVE_AND_NEGATION_AND_PARENTHESES_SYMBOLS;

    static {
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_SYMBOLS = new HashSet<>();
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_SYMBOLS.add(NOT);
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_SYMBOLS.add(AND);
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_SYMBOLS.add(OR);
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_SYMBOLS.add(IMPLICATION);
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_SYMBOLS.add(BICONDITIONAL);
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_SYMBOLS.add(String.valueOf(OPENING_PARENTHESES));
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_SYMBOLS.add(String.valueOf(CLOSING_PARENTHESES));
    }

    public static String convertOnlineCalculatorString(String str) {
        if (Utils.isNullOrBlank(str)) throw new IllegalArgumentException("null param");
        return str.replace("∨", SentenceUtils.OR)
                .replace("∧", SentenceUtils.AND)
                .replace("¬", SentenceUtils.NOT)
                .replace("→", SentenceUtils.IMPLICATION)
                .replace("↔", SentenceUtils.BICONDITIONAL)
                .replace("~", SentenceUtils.NOT);
    }
}


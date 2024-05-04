package org.example.util;

import org.example.domain.FOLSentenceType;
import org.example.domain.sentence.*;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.temp_fol.FOLSentence;

import java.util.*;

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
    public static char COMMA = ',';

    public static final Set<String> CONNECTIVE_AND_NEGATION_AND_PARENTHESES_AND_COMMA_SYMBOLS;

    static {
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_AND_COMMA_SYMBOLS = new HashSet<>();
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_AND_COMMA_SYMBOLS.add(NOT);
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_AND_COMMA_SYMBOLS.add(AND);
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_AND_COMMA_SYMBOLS.add(OR);
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_AND_COMMA_SYMBOLS.add(IMPLICATION);
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_AND_COMMA_SYMBOLS.add(BICONDITIONAL);
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_AND_COMMA_SYMBOLS.add(String.valueOf(OPENING_PARENTHESES));
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_AND_COMMA_SYMBOLS.add(String.valueOf(CLOSING_PARENTHESES));
        CONNECTIVE_AND_NEGATION_AND_PARENTHESES_AND_COMMA_SYMBOLS.add(String.valueOf(COMMA));
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

    @SuppressWarnings("unchecked")
    public static LinkedHashSet<Sentence>[] splitLinkedHashSetOfSentencesIntoTwo(LinkedHashSet<Sentence> set) {
        if (set == null || set.size() < 2) throw new IllegalArgumentException("null param");

        LinkedHashSet<Sentence>[] arr = (LinkedHashSet<Sentence>[]) new LinkedHashSet[2];
        LinkedHashSet<Sentence> s1 = new LinkedHashSet<>();
        LinkedHashSet<Sentence> s2 = new LinkedHashSet<>();

        int halfSize = set.size() / 2;

        int count = 0;
        for (Iterator<Sentence> iterator = set.iterator(); iterator.hasNext(); count++) {
            if (count < halfSize) s1.add(iterator.next());
            else s2.add(iterator.next());
        }

        arr[0] = s1;
        arr[1] = s2;
        return arr;
    }

    @SuppressWarnings("unchecked")
    public static LinkedHashSet<FOLSentence>[] splitLinkedHashSetOfFOLSentencesIntoTwo(LinkedHashSet<FOLSentence> set) {
        if (set == null || set.size() < 2) throw new IllegalArgumentException("null param");

        LinkedHashSet<FOLSentence>[] arr = (LinkedHashSet<FOLSentence>[]) new LinkedHashSet[2];
        LinkedHashSet<FOLSentence> s1 = new LinkedHashSet<>();
        LinkedHashSet<FOLSentence> s2 = new LinkedHashSet<>();

        int halfSize = set.size() / 2;

        int count = 0;
        for (Iterator<FOLSentence> iterator = set.iterator(); iterator.hasNext(); count++) {
            if (count < halfSize) s1.add(iterator.next());
            else s2.add(iterator.next());
        }

        arr[0] = s1;
        arr[1] = s2;
        return arr;
    }
}


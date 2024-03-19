package org.example.domain;

import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aram.azatyan | 2/13/2024 9:55 PM
 */
public enum Connective {
    AND(3) {
        @Override
        public boolean evaluate(boolean b1, boolean b2) {
            return b1 && b2;
        }

        @Override
        public String toString() {
            return SentenceUtils.AND;
        }
    },
    OR(2) {
        @Override
        public boolean evaluate(boolean b1, boolean b2) {
            return b1 || b2;
        }

        @Override
        public String toString() {
            return SentenceUtils.OR;
        }
    },
    IMPLICATION(1) {
        @Override
        public boolean evaluate(boolean b1, boolean b2) {
            return !b1 || b2;
        }

        @Override
        public String toString() {
            return SentenceUtils.IMPLICATION;
        }
    },
    BICONDITIONAL(0) {
        @Override
        public boolean evaluate(boolean b1, boolean b2) {
            return b1 == b2;
        }

        @Override
        public String toString() {
            return SentenceUtils.BICONDITIONAL;
        }
    };

    private final int precedence;

    Connective(int precedence) {
        this.precedence = precedence;
    }

    public abstract boolean evaluate(boolean b1, boolean b2);

    public int getPrecedence() {
        return precedence;
    }

    public static List<Connective> getCommutatives() {
        var commutatives = new ArrayList<Connective>();
        commutatives.add(AND);
        commutatives.add(OR);
        commutatives.add(BICONDITIONAL);
        return commutatives;
    }

    public static List<String> getConnectiveSymbols() {
        var symbols = new ArrayList<String>();
        symbols.add(SentenceUtils.AND);
        symbols.add(SentenceUtils.OR);
        symbols.add(SentenceUtils.IMPLICATION);
        symbols.add(SentenceUtils.BICONDITIONAL);
        return symbols;
    }

    public static Connective fromValue(String value) {
        if (Utils.isNullOrBlank(value)) throw new IllegalArgumentException("null param");

        if (value.equals(SentenceUtils.AND)) return AND;
        if (value.equals(SentenceUtils.OR)) return OR;
        if (value.equals(SentenceUtils.IMPLICATION)) return IMPLICATION;
        if (value.equals(SentenceUtils.BICONDITIONAL)) return BICONDITIONAL;

        throw new IllegalArgumentException("invalid param");
    }
}

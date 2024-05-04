package org.example.temp_fol;

import org.example.util.SentenceUtils;
import org.example.util.Utils;

/**
 * @author aram.azatyan | 4/14/2024 9:26 PM
 */
public final class Variable implements Term {
    private final String name;

    public Variable(String name) {
        this.name = standardize(name);
    }

    @Override
    public TermType type() {
        return TermType.VARIABLE;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof Variable that)) return false;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private String standardize(String name) {
        if (Utils.isNullOrBlank(name)) throw new IllegalArgumentException("null param");
//        if (name.equalsIgnoreCase("true") || name.equalsIgnoreCase("false")) throw
//                new IllegalArgumentException("only one instance of TRUE and FALSE available");

        for (String symbol : SentenceUtils.CONNECTIVE_AND_NEGATION_AND_PARENTHESES_AND_COMMA_SYMBOLS) {
            if (name.contains(symbol)) throw new IllegalArgumentException("the " +
                    "name of the variable should not contain any reserved symbols");
        }

        return name.toLowerCase();
    }

    public String getName() {
        return name;
    }
}

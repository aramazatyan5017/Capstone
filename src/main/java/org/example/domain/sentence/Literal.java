package org.example.domain.sentence;

import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.util.Objects;

/**
 * @author aram.azatyan | 2/21/2024 11:50 AM
 */
public final class Literal implements Sentence {
    private final String name;
    private final boolean negated;

    public Literal(String name) {
        if (!isValidName(name)) throw new IllegalArgumentException("null param");
        this.name = name;
        this.negated = false;
    }

    public Literal(String name, boolean negated) {
        if (!isValidName(name)) throw new IllegalArgumentException("null param");
        this.name = name;
        this.negated = negated;
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    @Override
    public CNFSentence convertToCNF() {
        return new CNFSentence(new Clause(this));
    }

    @Override
    public String toString() {
        return (negated ? SentenceUtils.NOT : "") + name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof Literal that)) return false;
        return this.name.equals(that.name) && this.negated == that.negated;
    }

    public boolean equalsIgnoreNegation(Literal other) {
        if (other == null) return false;
        return name.equals(other.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, negated);
    }

    private boolean isValidName(String name) {
        if (Utils.isNullOrBlank(name)) return false;
        for (String symbol : SentenceUtils.CONNECTIVE_AND_NEGATION_AND_PARENTHESES_SYMBOLS) if (name.contains(symbol)) return false;
        return true;
    }

    public String getName() {
        return name;
    }

    public boolean isNegated() {
        return negated;
    }
}

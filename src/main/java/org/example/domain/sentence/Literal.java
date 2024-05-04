package org.example.domain.sentence;

import org.example.domain.SentenceType;
import org.example.domain.Sentences;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;
import java.util.Objects;

/**
 * @author aram.azatyan | 2/21/2024 11:50 AM
 */
public final class Literal extends AbstractSentence {
    private final String name;
    private final boolean negated;

    public static final Literal TRUE;
    public static final Literal FALSE;

    static {
        TRUE = new Literal(true);
        FALSE = new Literal(false);
    }

    public Literal(String expression) {
        if (Utils.isNullOrBlank(expression)) throw new IllegalArgumentException("null param");

        Literal literal;

        try {
            literal = Sentences.parseLiteralExpression(expression);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (literal.equals(TRUE) || literal.equals(FALSE)) throw
                new IllegalArgumentException("only one instance of TRUE and FALSE available");

        this.name = literal.getName();
        this.negated = literal.isNegated();
    }

    public Literal(String name, boolean negated) {
        validateName(name);
        this.name = name;
        this.negated = negated;
    }

    private Literal(boolean value) {
        this.name = value ? "TRUE" : "FALSE";
        this.negated = false;
    }

    @Override
    public SentenceType type() {
        return SentenceType.LITERAL;
    }

    @Override
    protected CNFSentence convertToMinimalCNF() throws TautologyException, ContradictionException {
        if (this == Literal.TRUE) throw new TautologyException();
        if (this == Literal.FALSE) throw new ContradictionException();
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

    private void validateName(String name) {
        if (Utils.isNullOrBlank(name)) throw new IllegalArgumentException("null param");
        if (name.equalsIgnoreCase("true") || name.equalsIgnoreCase("false")) throw
                new IllegalArgumentException("only one instance of TRUE and FALSE available");

        for (String symbol : SentenceUtils.CONNECTIVE_AND_NEGATION_AND_PARENTHESES_AND_COMMA_SYMBOLS) {
            if (name.contains(symbol)) throw new IllegalArgumentException("the " +
                    "name of a literal should not contain any reserved symbols");
        }
    }

    public String getName() {
        return name;
    }

    public boolean isNegated() {
        return negated;
    }
}

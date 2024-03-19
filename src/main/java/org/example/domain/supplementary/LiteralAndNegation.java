package org.example.domain.supplementary;

/**
 * @author aram.azatyan | 3/2/2024 1:22 PM
 */
public class LiteralAndNegation {
    private String literal;
    private boolean negated;

    public LiteralAndNegation(String literal, boolean negated) {
        this.literal = literal;
        this.negated = negated;
    }

    @Override
    public String toString() {
        return literal;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }
}

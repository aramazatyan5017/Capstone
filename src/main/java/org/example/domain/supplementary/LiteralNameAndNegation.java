package org.example.domain.supplementary;

/**
 * @author aram.azatyan | 3/2/2024 1:22 PM
 */
public class LiteralNameAndNegation {
    private String literalName;
    private boolean negated;

    public LiteralNameAndNegation(String literalName, boolean negated) {
        this.literalName = literalName;
        this.negated = negated;
    }

    @Override
    public String toString() {
        return literalName;
    }

    public String getLiteralName() {
        return literalName;
    }

    public void setLiteralName(String literalName) {
        this.literalName = literalName;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }
}

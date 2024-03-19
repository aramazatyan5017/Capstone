package org.example.domain.supplementary;

import org.example.domain.Connective;

/**
 * @author aram.azatyan | 2/26/2024 4:20 PM
 */
public class ConnectiveAndNegation {
    private Connective connective;
    private boolean negated;

    public ConnectiveAndNegation(Connective connective, boolean negated) {
        this.connective = connective;
        this.negated = negated;
    }

    @Override
    public String toString() {
        return connective.toString();
    }

    public Connective getConnective() {
        return connective;
    }

    public void setConnective(Connective connective) {
        this.connective = connective;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }
}

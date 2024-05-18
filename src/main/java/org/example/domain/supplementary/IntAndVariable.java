package org.example.domain.supplementary;

import org.example.Samp;
import org.example.domain.sentence.fol.term.Variable;

import java.util.Objects;

/**
 * @author aram.azatyan | 5/14/2024 8:01 PM
 */
public record IntAndVariable(int row, Variable variable) {

    public IntAndVariable {
        if (row < 0 || variable == null) throw new IllegalArgumentException("invalid values passed");
    }

    @Override
    public String toString() {
        return row + variable.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof IntAndVariable that)) return false;
        return row == that.row && variable.equals(that.variable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, variable);
    }
}

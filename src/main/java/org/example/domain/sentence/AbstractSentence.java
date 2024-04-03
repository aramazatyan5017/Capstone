package org.example.domain.sentence;

import org.example.domain.SatisfiabilityType;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.truth_table.TruthTable;

/**
 * @author aram.azatyan | 4/1/2024 2:11 PM
 */
public sealed abstract class AbstractSentence implements Sentence
                                              permits Literal, Clause, CNFSentence, GenericComplexSentence {
    private SatisfiabilityType satisfiabilityType;
    private CNFSentence minimalCNF;
    private TruthTable truthTable;

    protected abstract CNFSentence convertToMinimalCNF() throws TautologyException, ContradictionException;

    @Override
    public CNFSentence minimalCNF() throws ContradictionException, TautologyException {
        if (satisfiabilityType == null) {
            try {
                minimalCNF = convertToMinimalCNF();
                satisfiabilityType = SatisfiabilityType.CONTINGENCY;
                return minimalCNF;
            } catch (TautologyException e) {
                satisfiabilityType = SatisfiabilityType.TAUTOLOGY;
                throw new TautologyException();
            } catch (ContradictionException e) {
                satisfiabilityType = SatisfiabilityType.CONTRADICTION;
                throw new ContradictionException();
            }
        } else if (satisfiabilityType == SatisfiabilityType.TAUTOLOGY) {
            throw new TautologyException();
        } else if (satisfiabilityType == SatisfiabilityType.CONTRADICTION) {
            throw new ContradictionException();
        }

        return minimalCNF;
    }

    @Override
    public SatisfiabilityType satisfiabilityType() {
        if (satisfiabilityType == null) {
            try {
                minimalCNF();
            } catch (TautologyException | ContradictionException ignored) {}
        }
        return satisfiabilityType;
    }

    @Override
    public TruthTable truthTable() throws ContradictionException, TautologyException {
        if (satisfiabilityType == null) {
            try {
                minimalCNF();
            } catch (TautologyException | ContradictionException ignored) {}
        }

        if (satisfiabilityType == SatisfiabilityType.TAUTOLOGY) {
            throw new TautologyException();
        } else if (satisfiabilityType == SatisfiabilityType.CONTRADICTION) {
            throw new ContradictionException();
        } else if (truthTable == null) {
            try {
                truthTable = new TruthTable(this);
            } catch (TautologyException | ContradictionException ignored) {}
        }

        return truthTable;
    }
}

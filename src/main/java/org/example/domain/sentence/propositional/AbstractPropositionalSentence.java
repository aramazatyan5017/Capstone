package org.example.domain.sentence.propositional;

import org.example.domain.LogicType;
import org.example.domain.SatisfiabilityType;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.truth_table.TruthTable;

/**
 * @author aram.azatyan | 4/1/2024 2:11 PM
 */
public sealed abstract class AbstractPropositionalSentence implements PropositionalSentence
                                              permits Literal, PropositionalClause, PropositionalCNFSentence, GenericComplexPropositionalSentence {
    private SatisfiabilityType satisfiabilityType;
    private PropositionalCNFSentence minimalCNF;
    private TruthTable truthTable;

    protected abstract PropositionalCNFSentence convertToMinimalCNF() throws TautologyException, ContradictionException;

    @Override
    public LogicType logicType() {
        return LogicType.PROPOSITIONAL;
    }

    @Override
    public PropositionalCNFSentence minimalCNF() throws ContradictionException, TautologyException {
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

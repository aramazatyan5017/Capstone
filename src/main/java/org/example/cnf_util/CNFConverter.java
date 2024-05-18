package org.example.cnf_util;

import org.example.domain.FOLSentenceType;
import org.example.domain.LogicType;
import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Sentence;
import org.example.domain.sentence.fol.FOLSentence;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.GenericComplexPropositionalSentence;
import org.example.domain.PropositionalSentenceType;
import org.example.domain.sentence.propositional.PropositionalSentence;
import org.example.domain.supplementary.LeftAndRightCNF;
import org.example.exception.TautologyException;
import org.example.exception.ContradictionException;


/**
 * @author aram.azatyan | 3/4/2024 2:58 PM
 */
public class CNFConverter {

    public static CNFSentence toCNF(Sentence sentence) throws ContradictionException, TautologyException {
        if (sentence == null) throw new IllegalArgumentException("null param");

        LogicType logicType = sentence.logicType();

        if ((logicType == LogicType.PROPOSITIONAL && ((PropositionalSentence) sentence).type() != PropositionalSentenceType.GENERIC_COMPLEX) ||
                (logicType == LogicType.FOL && ((FOLSentence) sentence).type() != FOLSentenceType.GENERIC_COMPLEX)) {
            switch (logicType) {
                case PROPOSITIONAL -> {return ((PropositionalSentence) sentence).minimalCNF();}
                case FOL -> {return ((FOLSentence) sentence).minimalCNF();}
            }
        }

        GenericComplexPropositionalSentence complexSentence = (GenericComplexPropositionalSentence) sentence;

        LeftAndRightCNF info = new LeftAndRightCNF(complexSentence.getLeftSentence(), complexSentence.getRightSentence());

        if (info.isLeftDetermined() && info.isRightDetermined()) {
            boolean evaluation = complexSentence.getConnective().evaluate(info.leftValue(), info.rightValue());
            if (complexSentence.isNegated()) evaluation = !evaluation;

            if (evaluation) throw new TautologyException();
            else throw new ContradictionException();
        } else if (!info.isLeftDetermined() && !info.isRightDetermined()) {
            CNFSentence cnfLeft = info.getLeft();
            CNFSentence cnfRight = info.getRight();

            switch (complexSentence.getConnective()) {
                case OR -> {
                    return complexSentence.isNegated()
                            ? CNFRules.NEGATED_OR.apply(cnfLeft, cnfRight)
                            : CNFRules.OR.apply(cnfLeft, cnfRight);
                }
                case AND -> {
                    return complexSentence.isNegated()
                            ? CNFRules.NEGATED_AND.apply(cnfLeft, cnfRight)
                            : CNFRules.AND.apply(cnfLeft, cnfRight);
                }
                case IMPLICATION -> {
                    return complexSentence.isNegated()
                            ? CNFRules.NEGATED_IMPLIES.apply(cnfLeft, cnfRight)
                            : CNFRules.IMPLIES.apply(cnfLeft, cnfRight);
                }
                case BICONDITIONAL -> {
                    return complexSentence.isNegated()
                            ? CNFRules.NEGATED_BICONDITIONAL.apply(cnfLeft, cnfRight)
                            : CNFRules.BICONDITIONAL.apply(cnfLeft, cnfRight);
                }
            }
        } else {
            boolean value = info.isLeftDetermined() ? info.leftValue() : info.rightValue();

            switch (complexSentence.getConnective()) {
                case OR -> {
                    if (value) {
                        if (complexSentence.isNegated()) throw new ContradictionException();
                        else throw new TautologyException();
                    } else {
                        return complexSentence.isNegated()
                                ? CNFRules.negateCNF(info.isLeftDetermined() ? info.getRight() : info.getLeft())
                                : info.isLeftDetermined() ? info.getRight() : info.getLeft();
                    }
                }
                case AND -> {
                    if (!value) {
                        if (complexSentence.isNegated()) throw new TautologyException();
                        else throw new ContradictionException();
                    } else {
                        return complexSentence.isNegated()
                                ? CNFRules.negateCNF(info.isLeftDetermined() ? info.getRight() : info.getLeft())
                                : info.isLeftDetermined() ? info.getRight() : info.getLeft();
                    }
                }
                case IMPLICATION -> {
                    if (info.isLeftDetermined()) {
                        if (!value) {
                            if (complexSentence.isNegated()) throw new ContradictionException();
                            else throw new TautologyException();
                        } else {
                            return complexSentence.isNegated() ? CNFRules.negateCNF(info.getRight()) : info.getRight();
                        }
                    } else {
                        if (value) {
                            if (complexSentence.isNegated()) throw new ContradictionException();
                            else throw new TautologyException();
                        } else {
                            return complexSentence.isNegated() ? info.getLeft() : CNFRules.negateCNF(info.getLeft());
                        }
                    }
                }
                case BICONDITIONAL -> {
                    if (info.isLeftDetermined()) {
                        if (value) {
                            return complexSentence.isNegated() ? CNFRules.negateCNF(info.getRight()) : info.getRight();
                        } else {
                            return complexSentence.isNegated() ? info.getRight() : CNFRules.negateCNF(info.getRight());
                        }
                    } else {
                        if (value) {
                            return complexSentence.isNegated() ? CNFRules.negateCNF(info.getLeft()): info.getLeft();
                        } else {
                            return complexSentence.isNegated() ? info.getLeft() : CNFRules.negateCNF(info.getLeft());
                        }
                    }
                }
            }
        }
        return null;
    }
}

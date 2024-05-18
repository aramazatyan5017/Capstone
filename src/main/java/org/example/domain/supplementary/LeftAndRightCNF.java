package org.example.domain.supplementary;

import org.example.domain.LogicType;
import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Sentence;
import org.example.domain.sentence.fol.FOLCNFSentence;
import org.example.domain.sentence.fol.FOLSentence;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalSentence;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;

/**
 * @author aram.azatyan | 3/14/2024 4:39 PM
 */
public class LeftAndRightCNF {

    private CNFSentence cnfLeft = null;
    private CNFSentence cnfRight = null;
    private Boolean determinedLeft = null;
    private Boolean determinedRight = null;

    public LeftAndRightCNF(Sentence left, Sentence right) {
        if (left == null || right == null) throw new IllegalArgumentException("null param");
        if (left.logicType() != right.logicType()) throw new IllegalArgumentException("sentences of different logic types");

        try {
            cnfLeft = left.logicType() == LogicType.PROPOSITIONAL
                    ? ((PropositionalSentence) left).minimalCNF()
                    : ((FOLSentence) left).minimalCNF();
        } catch (ContradictionException e) {
            determinedLeft = false;
        } catch (TautologyException e) {
            determinedLeft = true;
        }

        try {
            cnfRight = right.logicType() == LogicType.PROPOSITIONAL
                    ? ((PropositionalSentence) right).minimalCNF()
                    : ((FOLSentence) right).minimalCNF();
        } catch (ContradictionException e) {
            determinedRight = false;
        } catch (TautologyException e) {
            determinedRight = true;
        }
    }

    public boolean isLeftDetermined() {
        return determinedLeft != null;
    }

    public boolean isRightDetermined() {
        return determinedRight != null;
    }

    public CNFSentence getLeft() {
        return cnfLeft;
    }

    public CNFSentence getRight() {
        return cnfRight;
    }

    public boolean leftValue() {
        return determinedLeft;
    }

    public boolean rightValue() {
        return determinedRight;
    }
}

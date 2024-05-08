package org.example.domain.supplementary;

import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalSentence;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;

/**
 * @author aram.azatyan | 3/14/2024 4:39 PM
 */
public class LeftAndRightPropositionalCNF {

    private PropositionalCNFSentence cnfLeft = null;
    private PropositionalCNFSentence cnfRight = null;
    private Boolean determinedLeft = null;
    private Boolean determinedRight = null;

    public LeftAndRightPropositionalCNF(PropositionalSentence left, PropositionalSentence right) {
        if (left == null || right == null) throw new IllegalArgumentException("null param");

        try {
            cnfLeft = left.minimalCNF();
        } catch (ContradictionException e) {
            determinedLeft = false;
        } catch (TautologyException e) {
            determinedLeft = true;
        }

        try {
            cnfRight = right.minimalCNF();
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

    public PropositionalCNFSentence getLeft() {
        return cnfLeft;
    }

    public PropositionalCNFSentence getRight() {
        return cnfRight;
    }

    public boolean leftValue() {
        return determinedLeft;
    }

    public boolean rightValue() {
        return determinedRight;
    }
}

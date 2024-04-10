package org.example.domain.supplementary;

import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Sentence;
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

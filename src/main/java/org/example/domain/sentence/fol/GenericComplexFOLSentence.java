package org.example.domain.sentence.fol;

import org.example.domain.Connective;
import org.example.domain.FOLSentenceType;
import org.example.domain.LogicType;
import org.example.domain.Sentences;
import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Sentence;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.supplementary.LeftAndRightCNF;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.util.SentenceUtils;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static org.example.util.SentenceUtils.NOT;

/**
 * @author aram.azatyan | 4/17/2024 2:41 PM
 */
public final class GenericComplexFOLSentence implements FOLSentence {
    private final FOLSentence leftSentence;
    private final FOLSentence rightSentence;
    private final Connective connective;
    private final boolean negated;
    private String stringRepresentation;

    public GenericComplexFOLSentence(FOLSentence leftSentence, FOLSentence rightSentence,
                                  Connective connective) {
        validateParams(leftSentence, rightSentence, connective);
        this.leftSentence = leftSentence;
        this.rightSentence = rightSentence;
        this.connective = connective;
        this.negated = false;
    }

    public GenericComplexFOLSentence(FOLSentence leftSentence, FOLSentence rightSentence,
                                  Connective connective, boolean negated) {
        validateParams(leftSentence, rightSentence, connective);
        this.leftSentence = leftSentence;
        this.rightSentence = rightSentence;
        this.connective = connective;
        this.negated = negated;
    }

    public GenericComplexFOLSentence(String expression) throws ParseException {
        GenericComplexFOLSentence generic = Sentences.parseGenericFOLExpression(expression);
        this.leftSentence = generic.getLeftSentence();
        this.rightSentence = generic.getRightSentence();
        this.connective = generic.getConnective();
        this.negated = generic.isNegated();
    }

    public GenericComplexFOLSentence(LinkedHashSet<FOLSentence> sentences, Connective connective, boolean negated) {
        if (sentences == null || sentences.isEmpty() || connective == null) throw
                new IllegalArgumentException("null param");
        sentences.remove(null);
        if (sentences.size() < 2) throw new IllegalArgumentException("unable to construct a generic complex FOL sentence");
        if (connective == Connective.IMPLICATION) throw new IllegalArgumentException("implication is not associative");

        LinkedHashSet<FOLSentence>[] arr = SentenceUtils.splitLinkedHashSetOfFOLSentencesIntoTwo(sentences);

        GenericComplexFOLSentence sentence = connectSentences(arr[0], arr[1], connective);
        this.leftSentence = sentence.getLeftSentence();
        this.rightSentence = sentence.getRightSentence();
        this.connective = connective;
        this.negated = negated;
    }

    private GenericComplexFOLSentence connectSentences(LinkedHashSet<FOLSentence> leftSubTree,
                                                    LinkedHashSet<FOLSentence> rightSubTree, Connective connective) {
        FOLSentence left = getComplexFromSubtree(leftSubTree, connective);
        FOLSentence right = getComplexFromSubtree(rightSubTree, connective);
        return new GenericComplexFOLSentence(left, right, connective);
    }

    private FOLSentence getComplexFromSubtree(LinkedHashSet<FOLSentence> leftSubTree, Connective connective) {
        if (leftSubTree.size() == 1) return leftSubTree.iterator().next();
        LinkedHashSet<FOLSentence>[] sets = SentenceUtils.splitLinkedHashSetOfFOLSentencesIntoTwo(leftSubTree);
        return connectSentences(sets[0], sets[1], connective);
    }

    public LinkedHashSet<Predicate> getPredicates() {
        LinkedHashSet<Predicate> predicates = new LinkedHashSet<>();
        findPredicates(this, predicates);
        return predicates;
    }

    private void findPredicates(FOLSentence root, Set<Predicate> predicateSet) {
        switch (root.type()) {
            case PREDICATE -> predicateSet.add((Predicate) root);
            case CLAUSE -> predicateSet.addAll(((FOLClause) root).getPredicates());
            case CNF -> {
                for (FOLClause clause : ((FOLCNFSentence) root).getClauses()) {
                    predicateSet.addAll(clause.getPredicates());
                }
            }
            case GENERIC_COMPLEX -> {
                findPredicates(((GenericComplexFOLSentence) root).leftSentence, predicateSet);
                findPredicates(((GenericComplexFOLSentence) root).rightSentence, predicateSet);
            }
        }
    }

    @Override
    public LogicType logicType() {
        return LogicType.FOL;
    }

    @Override
    public FOLSentenceType type() {
        return FOLSentenceType.GENERIC_COMPLEX;
    }

    @Override
    public FOLCNFSentence minimalCNF() throws TautologyException, ContradictionException {
        return (FOLCNFSentence) Sentences.toCNF(this);
    }

    @Override
    public String toString() {
        if (stringRepresentation == null) {
            stringRepresentation = constructToString(true);
        }

        return stringRepresentation;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof GenericComplexFOLSentence that)) return false;

        LeftAndRightCNF thisAndThatInfo = new LeftAndRightCNF(this, that);

        if (thisAndThatInfo.isLeftDetermined() && thisAndThatInfo.isRightDetermined()) {
            return thisAndThatInfo.leftValue() == thisAndThatInfo.rightValue();
        } else if (!thisAndThatInfo.isLeftDetermined() && !thisAndThatInfo.isRightDetermined()) {
            if (thisAndThatInfo.getLeft().equals(thisAndThatInfo.getRight())) return true;

            Sentence opt1 = thisAndThatInfo.getLeft();
            Sentence opt2 = thisAndThatInfo.getRight();

            if (thisAndThatInfo.getLeft().isCanonical()) {
                try {
                    opt1 = Sentences.optimizeCanonicalCNF(thisAndThatInfo.getLeft());
                } catch (TautologyException e) {
                    opt1 = Predicate.TRUE;
                } catch (ContradictionException e) {
                    opt1 = Predicate.FALSE;
                }
            }

            if (thisAndThatInfo.getRight().isCanonical()) {
                try {
                    opt2 = Sentences.optimizeCanonicalCNF(thisAndThatInfo.getRight());
                } catch (TautologyException e) {
                    opt2 = Predicate.TRUE;
                } catch (ContradictionException e) {
                    opt2 = Predicate.FALSE;
                }
            }



            if ((((FOLSentence) opt1).type() == FOLSentenceType.PREDICATE && ((FOLSentence) opt2).type() == FOLSentenceType.PREDICATE)
                    ||
                    (((FOLSentence) opt1).type() == FOLSentenceType.CNF && ((FOLSentence) opt2).type() == FOLSentenceType.CNF))
                return opt1.equals(opt2);
        }

        return false;
    }

    @Override
    public int hashCode() {
        try {
            FOLCNFSentence cnfSentence = minimalCNF();
            return cnfSentence.hashCode();
        } catch (TautologyException e) {
            return Objects.hash(true);
        } catch (ContradictionException e) {
            return Objects.hash(false);
        }
    }

    private String constructToString(boolean isFirstLevel) {
        String core = String.join(" ",
                leftSentence.type() != FOLSentenceType.GENERIC_COMPLEX
                        ?   leftSentence.toString()
                        :   ((GenericComplexFOLSentence) leftSentence).constructToString(false),
                connective.toString(),
                rightSentence.type() != FOLSentenceType.GENERIC_COMPLEX
                        ? rightSentence.toString()
                        : ((GenericComplexFOLSentence) rightSentence).constructToString(false));

        if (isFirstLevel && !isNegated()) return core;
        return addParentheses(core, negated);
    }

    private String addParentheses(String complexString, boolean negated) {
        return (negated ? NOT : "") + "(" + complexString + ")";
    }

    private void validateParams(FOLSentence leftSentence, FOLSentence rightSentence,
                                Connective connective) {
        if (leftSentence == null || rightSentence == null || connective == null) throw
                new IllegalArgumentException("null param");
    }

    public FOLSentence getLeftSentence() {
        return leftSentence;
    }

    public FOLSentence getRightSentence() {
        return rightSentence;
    }

    public Connective getConnective() {
        return connective;
    }

    public boolean isNegated() {
        return negated;
    }
}

package org.example.domain.sentence.propositional;

import org.example.domain.Connective;
import org.example.domain.PropositionalSentenceType;
import org.example.domain.Sentences;
import org.example.domain.sentence.Sentence;
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
 * @author aram.azatyan | 2/26/2024 11:42 AM
 */
public final class GenericComplexPropositionalSentence extends AbstractPropositionalSentence {
    private final PropositionalSentence leftSentence;
    private final PropositionalSentence rightSentence;
    private final Connective connective;
    private final boolean negated;
    private String stringRepresentation;

    public GenericComplexPropositionalSentence(PropositionalSentence leftSentence, PropositionalSentence rightSentence,
                                               Connective connective) {
        validateParams(leftSentence, rightSentence, connective);
        this.leftSentence = leftSentence;
        this.rightSentence = rightSentence;
        this.connective = connective;
        this.negated = false;
    }

    public GenericComplexPropositionalSentence(PropositionalSentence leftSentence, PropositionalSentence rightSentence,
                                               Connective connective, boolean negated) {
        validateParams(leftSentence, rightSentence, connective);
        this.leftSentence = leftSentence;
        this.rightSentence = rightSentence;
        this.connective = connective;
        this.negated = negated;
    }

    public GenericComplexPropositionalSentence(String expression) throws ParseException {
        GenericComplexPropositionalSentence genericComplexPropositionalSentence = Sentences.parseGenericExpression(expression);
        this.leftSentence = genericComplexPropositionalSentence.getLeftSentence();
        this.rightSentence = genericComplexPropositionalSentence.getRightSentence();
        this.connective = genericComplexPropositionalSentence.getConnective();
        this.negated = genericComplexPropositionalSentence.isNegated();
    }

    public GenericComplexPropositionalSentence(LinkedHashSet<PropositionalSentence> sentences, Connective connective, boolean negated) {
        if (sentences == null || sentences.isEmpty() || connective == null) throw
                new IllegalArgumentException("null param");
        sentences.remove(null);
        if (sentences.size() < 2) throw new IllegalArgumentException("unable to construct a generic complex sentence");
        if (connective == Connective.IMPLICATION) throw new IllegalArgumentException("implication is not associative");

        LinkedHashSet<PropositionalSentence>[] arr = SentenceUtils.splitLinkedHashSetOfPropositionalSentencesIntoTwo(sentences);

        GenericComplexPropositionalSentence sentence = connectSentences(arr[0], arr[1], connective);
        this.leftSentence = sentence.getLeftSentence();
        this.rightSentence = sentence.getRightSentence();
        this.connective = connective;
        this.negated = negated;
    }

    private GenericComplexPropositionalSentence connectSentences(LinkedHashSet<PropositionalSentence> leftSubTree,
                                                                 LinkedHashSet<PropositionalSentence> rightSubTree, Connective connective) {
        PropositionalSentence left = getComplexFromSubtree(leftSubTree, connective);
        PropositionalSentence right = getComplexFromSubtree(rightSubTree, connective);
        return new GenericComplexPropositionalSentence(left, right, connective);
    }

    private PropositionalSentence getComplexFromSubtree(LinkedHashSet<PropositionalSentence> leftSubTree, Connective connective) {
        if (leftSubTree.size() == 1) return leftSubTree.iterator().next();
        LinkedHashSet<PropositionalSentence>[] sets = SentenceUtils.splitLinkedHashSetOfPropositionalSentencesIntoTwo(leftSubTree);
        return connectSentences(sets[0], sets[1], connective);
    }

    public LinkedHashSet<Literal> getLiterals() {
        LinkedHashSet<Literal> literals = new LinkedHashSet<>();
        findLiterals(this, literals);
        return literals;
    }

    private void findLiterals(PropositionalSentence root, Set<Literal> literalSet) {
        switch (root.type()) {
            case LITERAL -> {
                literalSet.add((Literal) root);
            }
            case CLAUSE -> {
                literalSet.addAll(((PropositionalClause) root).getLiterals());
            }
            case CNF -> {
                for (PropositionalClause clause : ((PropositionalCNFSentence) root).getClauses()) {
                    literalSet.addAll(clause.getLiterals());
                }
            }
            case GENERIC_COMPLEX -> {
                findLiterals(((GenericComplexPropositionalSentence) root).leftSentence, literalSet);
                findLiterals(((GenericComplexPropositionalSentence) root).rightSentence, literalSet);
            }
        }
    }

    @Override
    public PropositionalSentenceType type() {
        return PropositionalSentenceType.GENERIC_COMPLEX;
    }

    @Override
    public PropositionalCNFSentence convertToMinimalCNF() throws TautologyException, ContradictionException {
        return (PropositionalCNFSentence) Sentences.toCNF(this);
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
        if (!(other instanceof GenericComplexPropositionalSentence that)) return false;

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
                    opt1 = Literal.TRUE;
                } catch (ContradictionException e) {
                    opt1 = Literal.FALSE;
                }
            }

            if (thisAndThatInfo.getRight().isCanonical()) {
                try {
                    opt2 = Sentences.optimizeCanonicalCNF(thisAndThatInfo.getRight());
                } catch (TautologyException e) {
                    opt2 = Literal.TRUE;
                } catch (ContradictionException e) {
                    opt2 = Literal.FALSE;
                }
            }

            if ((((PropositionalSentence) opt1).type() == PropositionalSentenceType.LITERAL && ((PropositionalSentence) opt2).type() == PropositionalSentenceType.LITERAL)
                    ||
                    (((PropositionalSentence) opt1).type() == PropositionalSentenceType.CNF && ((PropositionalSentence) opt2).type() == PropositionalSentenceType.CNF))
                return opt1.equals(opt2);
        }

        return false;
    }

    @Override
    public int hashCode() {
        try {
            PropositionalCNFSentence cnfSentence = minimalCNF();
            return cnfSentence.hashCode();
        } catch (TautologyException e) {
            return Objects.hash(true);
        } catch (ContradictionException e) {
            return Objects.hash(false);
        }
    }

    private String constructToString(boolean isFirstLevel) {
        String core = String.join(" ",
                leftSentence.type() != PropositionalSentenceType.GENERIC_COMPLEX
                        ? (leftSentence.type() == PropositionalSentenceType.CLAUSE || leftSentence.type() == PropositionalSentenceType.CNF
                            ? addParentheses(leftSentence.toString(), false)
                            : leftSentence.toString())
                        : ((GenericComplexPropositionalSentence) leftSentence).constructToString(false),
                connective.toString(),
                rightSentence.type() != PropositionalSentenceType.GENERIC_COMPLEX
                        ? (rightSentence.type() == PropositionalSentenceType.CLAUSE || rightSentence.type() == PropositionalSentenceType.CNF
                            ? addParentheses(rightSentence.toString(), false)
                            : rightSentence.toString())
                        : ((GenericComplexPropositionalSentence) rightSentence).constructToString(false));

        if (isFirstLevel && !isNegated()) return core;
        return addParentheses(core, negated);
    }

    private String addParentheses(String complexString, boolean negated) {
        return (negated ? NOT : "") + "(" + complexString + ")";
    }

    private void validateParams(PropositionalSentence leftSentence, PropositionalSentence rightSentence,
                                Connective connective) {
        if (leftSentence == null || rightSentence == null || connective == null) throw
                new IllegalArgumentException("null param");
    }

    public PropositionalSentence getLeftSentence() {
        return leftSentence;
    }

    public PropositionalSentence getRightSentence() {
        return rightSentence;
    }

    public Connective getConnective() {
        return connective;
    }

    public boolean isNegated() {
        return negated;
    }
}

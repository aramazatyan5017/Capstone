package org.example.domain.sentence;

import org.example.domain.Connective;
import org.example.domain.SentenceType;
import org.example.domain.Sentences;
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
public final class GenericComplexSentence extends AbstractSentence {
    private final Sentence leftSentence;
    private final Sentence rightSentence;
    private final Connective connective;
    private final boolean negated;
    private String stringRepresentation;

    public GenericComplexSentence(Sentence leftSentence, Sentence rightSentence,
                                  Connective connective) {
        validateParams(leftSentence, rightSentence, connective);
        this.leftSentence = leftSentence;
        this.rightSentence = rightSentence;
        this.connective = connective;
        this.negated = false;
    }

    public GenericComplexSentence(Sentence leftSentence, Sentence rightSentence,
                                  Connective connective, boolean negated) {
        validateParams(leftSentence, rightSentence, connective);
        this.leftSentence = leftSentence;
        this.rightSentence = rightSentence;
        this.connective = connective;
        this.negated = negated;
    }

    public GenericComplexSentence(String expression) throws ParseException {
        GenericComplexSentence genericComplexSentence = Sentences.parseGenericExpression(expression);
        this.leftSentence = genericComplexSentence.getLeftSentence();
        this.rightSentence = genericComplexSentence.getRightSentence();
        this.connective = genericComplexSentence.getConnective();
        this.negated = genericComplexSentence.isNegated();
    }

    public GenericComplexSentence(LinkedHashSet<Sentence> sentences, Connective connective, boolean negated) {
        if (sentences == null || sentences.isEmpty() || connective == null) throw
                new IllegalArgumentException("null param");
        sentences.remove(null);
        if (sentences.size() < 2) throw new IllegalArgumentException("unable to construct a generic complex sentence");
        if (connective == Connective.IMPLICATION) throw new IllegalArgumentException("implication is not associative");

        LinkedHashSet<Sentence>[] arr = SentenceUtils.splitLinkedHashSetOfSentencesIntoTwo(sentences);

        GenericComplexSentence sentence = connectSentences(arr[0], arr[1], connective);
        this.leftSentence = sentence.getLeftSentence();
        this.rightSentence = sentence.getRightSentence();
        this.connective = connective;
        this.negated = negated;
    }

    private GenericComplexSentence connectSentences(LinkedHashSet<Sentence> leftSubTree,
                                                    LinkedHashSet<Sentence> rightSubTree, Connective connective) {
        Sentence left = getComplexFromSubtree(leftSubTree, connective);
        Sentence right = getComplexFromSubtree(rightSubTree, connective);
        return new GenericComplexSentence(left, right, connective);
    }

    private Sentence getComplexFromSubtree(LinkedHashSet<Sentence> leftSubTree, Connective connective) {
        if (leftSubTree.size() == 1) return leftSubTree.stream().toList().get(0);
        LinkedHashSet<Sentence>[] sets = SentenceUtils.splitLinkedHashSetOfSentencesIntoTwo(leftSubTree);
        return connectSentences(sets[0], sets[1], connective);
    }

    public LinkedHashSet<Literal> getLiterals() {
        LinkedHashSet<Literal> literals = new LinkedHashSet<>();
        findLiterals(this, literals);
        return literals;
    }

    private void findLiterals(Sentence root, Set<Literal> literalSet) {
        switch (root.type()) {
            case LITERAL -> {
                literalSet.add((Literal) root);
            }
            case CLAUSE -> {
                literalSet.addAll(((Clause) root).getLiterals());
            }
            case CNF -> {
                for (Clause clause : ((CNFSentence) root).getClauses()) {
                    literalSet.addAll(clause.getLiterals());
                }
            }
            case GENERIC_COMPLEX -> {
                findLiterals(((GenericComplexSentence) root).leftSentence, literalSet);
                findLiterals(((GenericComplexSentence) root).rightSentence, literalSet);
            }
        }
    }

    @Override
    public SentenceType type() {
        return SentenceType.GENERIC_COMPLEX;
    }

    @Override
    protected CNFSentence convertToMinimalCNF() throws TautologyException, ContradictionException {
        return Sentences.toCNF(this);
    }

    @Override
    public String toString() {
        if (stringRepresentation == null) {
            stringRepresentation = constructToString(true);
        }

        return stringRepresentation;
    }

    // TODO: 4/2/2024 should I compare truth tables
    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof GenericComplexSentence that)) return false;

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

            if ((opt1.type() == SentenceType.LITERAL && opt2.type() == SentenceType.LITERAL)
                    ||
               (opt1.type() == SentenceType.CNF && opt2.type() == SentenceType.CNF)) return opt1.equals(opt2);
        }

        return false;
    }

    @Override
    public int hashCode() {
        try {
            CNFSentence cnfSentence = minimalCNF();
            return cnfSentence.hashCode();
        } catch (TautologyException e) {
            return Objects.hash(true);
        } catch (ContradictionException e) {
            return Objects.hash(false);
        }
    }

    private String constructToString(boolean isFirstLevel) {
        String core = String.join(" ",
                leftSentence.type() != SentenceType.GENERIC_COMPLEX
                        ? (leftSentence.type() == SentenceType.CLAUSE || leftSentence.type() == SentenceType.CNF
                            ? addParentheses(leftSentence.toString(), false)
                            : leftSentence.toString())
                        : ((GenericComplexSentence) leftSentence).constructToString(false),
                connective.toString(),
                rightSentence.type() != SentenceType.GENERIC_COMPLEX
                        ? (rightSentence.type() == SentenceType.CLAUSE || rightSentence.type() == SentenceType.CNF
                            ? addParentheses(rightSentence.toString(), false)
                            : rightSentence.toString())
                        : ((GenericComplexSentence) rightSentence).constructToString(false));

        if (isFirstLevel && !isNegated()) return core;
        return addParentheses(core, negated);
    }

    private String addParentheses(String complexString, boolean negated) {
        return (negated ? NOT : "") + "(" + complexString + ")";
    }

    private void validateParams(Sentence leftSentence, Sentence rightSentence,
                                Connective connective) {
        if (leftSentence == null || rightSentence == null || connective == null) throw
                new IllegalArgumentException("null param");
    }

    public Sentence getLeftSentence() {
        return leftSentence;
    }

    public Sentence getRightSentence() {
        return rightSentence;
    }

    public Connective getConnective() {
        return connective;
    }

    public boolean isNegated() {
        return negated;
    }
}

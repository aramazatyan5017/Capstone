package org.example.domain.sentence;

import org.example.domain.Connective;
import org.example.domain.supplementary.LeftAndRightCNF;
import org.example.exception.TautologyException;
import org.example.exception.UnsatisfiableException;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static org.example.util.SentenceUtils.NOT;

/**
 * @author aram.azatyan | 2/26/2024 11:42 AM
 */
public final class GenericComplexSentence implements Sentence {
    private final Sentence leftSentence;
    private final Sentence rightSentence;
    private final Connective connective;
    private final boolean negated;

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
        Sentence sentence = Sentences.parseGenericExpression(expression);
        if (sentence.isLiteral()) throw new ParseException("unable to construct a complex sentence", -1);
        GenericComplexSentence genericComplexSentence = (GenericComplexSentence) sentence;
        this.leftSentence = genericComplexSentence.getLeftSentence();
        this.rightSentence = genericComplexSentence.getRightSentence();
        this.connective = genericComplexSentence.getConnective();
        this.negated = genericComplexSentence.isNegated();
    }

    public LinkedHashSet<Literal> getLiterals() {
        LinkedHashSet<Literal> literals = new LinkedHashSet<>();
        findLiterals(this, literals);
        return literals;
    }

    private void findLiterals(Sentence root, Set<Literal> literalSet) {
        if (root.isLiteral()) {
            literalSet.add((Literal) root);
            return;
        }
        if (root.isClause()) {
            literalSet.addAll(((Clause) root).getLiterals());
            return;
        }
        if (root.isCnf()) {
            for (Clause clause : ((CNFSentence) root).getClauses()) literalSet.addAll(clause.getLiterals());
            return;
        }
        findLiterals(((GenericComplexSentence) root).leftSentence, literalSet);
        findLiterals(((GenericComplexSentence) root).rightSentence, literalSet);
    }

    @Override
    public boolean isGenericComplex() {
        return true;
    }

    @Override
    public CNFSentence convertToCNF() throws UnsatisfiableException, TautologyException {
        return Sentences.toCNF(this);
    }

    @Override
    public String toString() {
        return constructToString(true);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof GenericComplexSentence that)) return false;

        LeftAndRightCNF thisAndThatInfo = new LeftAndRightCNF(this, that);

        if (!thisAndThatInfo.isLeftDetermined() && !thisAndThatInfo.isRightDetermined()) {
            return thisAndThatInfo.getLeft().equals(thisAndThatInfo.getRight());
        } else if (thisAndThatInfo.isLeftDetermined() && thisAndThatInfo.isRightDetermined()) {
            return thisAndThatInfo.leftValue() == thisAndThatInfo.rightValue();
        }

        return false;
    }

    @Override
    public int hashCode() {
        try {
            CNFSentence cnf = this.convertToCNF();
            return cnf.hashCode();
        } catch (TautologyException e) {
            return Objects.hash(true);
        } catch (UnsatisfiableException e) {
            return Objects.hash(false);
        }
    }

    private String constructToString(boolean isFirstLevel) {
        String core = String.join(" ",
                !leftSentence.isGenericComplex()
                        ? (leftSentence.isClause() || leftSentence.isCnf()
                            ? addParentheses(leftSentence.toString(), false)
                            : leftSentence.toString())
                        : ((GenericComplexSentence) leftSentence).constructToString(false),
                connective.toString(),
                !rightSentence.isGenericComplex()
                        ? (rightSentence.isClause() || rightSentence.isCnf()
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

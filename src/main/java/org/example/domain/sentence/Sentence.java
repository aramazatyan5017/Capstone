package org.example.domain.sentence;

import org.example.domain.SatisfiabilityType;
import org.example.domain.SentenceType;
import org.example.domain.Sentences;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.truth_table.TruthTable;
import org.example.util.OptimizedSentenceParserHelper;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;

import static org.example.domain.SentenceType.*;

/**
 * @author aram.azatyan | 2/27/2024 5:58 PM
 */
public sealed interface Sentence permits AbstractSentence {
    SentenceType type();

    CNFSentence minimalCNF() throws ContradictionException, TautologyException;

    SatisfiabilityType satisfiabilityType();

    TruthTable truthTable() throws ContradictionException, TautologyException;

    // TODO: 4/3/2024 we assume that the each sentence has a unique minimal cnf
    static boolean isEquivalent(Sentence s1, Sentence s2) {
        if (s1 == null || s2 == null) throw new IllegalArgumentException("null param");
        if (s1.satisfiabilityType() != s2.satisfiabilityType()) return false;
        if (s1.satisfiabilityType() == SatisfiabilityType.TAUTOLOGY &&
                s2.satisfiabilityType() == SatisfiabilityType.TAUTOLOGY) return true;
        if (s1.satisfiabilityType() == SatisfiabilityType.CONTRADICTION &&
                s2.satisfiabilityType() == SatisfiabilityType.CONTRADICTION) return true;
        try {
            return s1.minimalCNF().equals(s2.minimalCNF()); // TODO: 4/3/2024 canonicaly amen meky inqy petq a handle ani
        } catch (TautologyException | ContradictionException ignored) {}

        return false;
    }

    static Sentence optimizedParse(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new IllegalArgumentException();

        Sentence sentence;

        if (expression.contains(SentenceUtils.IMPLICATION) || expression.contains(SentenceUtils.BICONDITIONAL)) {
            sentence = Sentences.parseGenericExpression(expression);
        } else if (expression.contains(SentenceUtils.AND)) {
            try {
                sentence = optimizeCNF(Sentences.parseCNFExpression(expression, false));
                if (sentence.type() == CNF) {
                    CNFSentence cnf = (CNFSentence) sentence;
                    if (cnf.size() == 1) {
                        Clause clause = cnf.getClauses().iterator().next();
                        if (clause.size() == 1) sentence = clause.getLiterals().iterator().next();
                        else sentence = clause;
                    }
                }
            } catch (ParseException e) {
                sentence = Sentences.parseGenericExpression(expression);
            }
        } else if (expression.contains(SentenceUtils.OR)) {
            try {
                sentence = optimizeCNF(new CNFSentence(Sentences.parseClauseExpression(expression)));
                if (sentence.type() == CNF) {
                    CNFSentence cnf = (CNFSentence) sentence;
                    if (cnf.size() == 1) {
                        Clause clause = cnf.getClauses().iterator().next();
                        if (clause.size() == 1) sentence = clause.getLiterals().iterator().next();
                        else sentence = clause;
                    }
                }
            } catch (ParseException e) {
                sentence = Sentences.parseGenericExpression(expression);
            }
        } else {
            sentence = Sentences.parseLiteralExpression(expression);
        }

        if (sentence.satisfiabilityType() == SatisfiabilityType.TAUTOLOGY) return Literal.TRUE;
        if (sentence.satisfiabilityType() == SatisfiabilityType.CONTRADICTION) return Literal.FALSE;
        return sentence.type() != GENERIC_COMPLEX ? sentence : tryOptimize((GenericComplexSentence) sentence);
    }

    private static Sentence tryOptimize(GenericComplexSentence genericSentence) throws ParseException {
        Sentence left = optimizedParse(genericSentence.getLeftSentence().toString());
        Sentence right = optimizedParse(genericSentence.getRightSentence().toString());

        // TODO: 4/3/2024 no true, false values inside a clause or cnf.

        switch (genericSentence.getConnective()) {
            case OR -> {
                if (genericSentence.isNegated()) {
                    if (thisAndThat(left, right, LITERAL)) return OptimizedSentenceParserHelper.literalNegatedOrLiteral(left, right);
                    if (thisOrThat(left, right, LITERAL, CLAUSE)) return OptimizedSentenceParserHelper.literalNegatedOrClause(left, right);
                    if (thisOrThat(left, right, LITERAL, CNF)) return OptimizedSentenceParserHelper.literalNegatedOrCNF(left, right);
                    if (thisOrThat(left, right, LITERAL, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.literalNegatedOrGeneric(left, right);
                    if (thisAndThat(left, right, CLAUSE)) return OptimizedSentenceParserHelper.clauseNegatedOrClause(left, right);
                    if (thisOrThat(left, right, CLAUSE, CNF)) return OptimizedSentenceParserHelper.clauseNegatedOrCNF(left, right);
                    if (thisOrThat(left, right, CLAUSE, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.clauseNegatedOrGeneric(left, right);
                    if (thisAndThat(left, right, CNF)) return OptimizedSentenceParserHelper.cnfNegatedOrCNF(left, right);
                    if (thisOrThat(left, right, CNF, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.cnfNegatedOrGeneric(left, right);
                    if (thisAndThat(left, right, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.genericNegatedOrGeneric(left, right);
                } else {
                    if (thisAndThat(left, right, LITERAL)) return OptimizedSentenceParserHelper.literalOrLiteral(left, right);
                    if (thisOrThat(left, right, LITERAL, CLAUSE)) return OptimizedSentenceParserHelper.literalOrClause(left, right);
                    if (thisOrThat(left, right, LITERAL, CNF)) return OptimizedSentenceParserHelper.literalOrCNF(left, right);
                    if (thisOrThat(left, right, LITERAL, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.literalOrGeneric(left, right);
                    if (thisAndThat(left, right, CLAUSE)) return OptimizedSentenceParserHelper.clauseOrClause(left, right);
                    if (thisOrThat(left, right, CLAUSE, CNF)) return OptimizedSentenceParserHelper.clauseOrCNF(left, right);
                    if (thisOrThat(left, right, CLAUSE, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.clauseOrGeneric(left, right);
                    if (thisAndThat(left, right, CNF)) return OptimizedSentenceParserHelper.cnfOrCNF(left, right);
                    if (thisOrThat(left, right, CNF, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.cnfOrGeneric(left, right);
                    if (thisAndThat(left, right, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.genericOrGeneric(left, right);
                }
            } case AND -> {
                if (genericSentence.isNegated()) {
                    if (thisAndThat(left, right, LITERAL)) return OptimizedSentenceParserHelper.literalNegatedAndLiteral(left, right);
                    if (thisOrThat(left, right, LITERAL, CLAUSE)) return OptimizedSentenceParserHelper.literalNegatedAndClause(left, right);
                    if (thisOrThat(left, right, LITERAL, CNF)) return OptimizedSentenceParserHelper.literalNegatedAndCNF(left, right);
                    if (thisOrThat(left, right, LITERAL, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.literalNegatedAndGeneric(left, right);
                    if (thisAndThat(left, right, CLAUSE)) return OptimizedSentenceParserHelper.clauseNegatedAndClause(left, right);
                    if (thisOrThat(left, right, CLAUSE, CNF)) return OptimizedSentenceParserHelper.clauseNegatedAndCNF(left, right);
                    if (thisOrThat(left, right, CLAUSE, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.clauseNegatedAndGeneric(left, right);
                    if (thisAndThat(left, right, CNF)) return OptimizedSentenceParserHelper.cnfNegatedAndCNF(left, right);
                    if (thisOrThat(left, right, CNF, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.cnfNegatedAndGeneric(left, right);
                    if (thisAndThat(left, right, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.genericNegatedAndGeneric(left, right);
                } else {
                    if (thisAndThat(left, right, LITERAL)) return OptimizedSentenceParserHelper.literalAndLiteral(left, right);
                    if (thisOrThat(left, right, LITERAL, CLAUSE)) return OptimizedSentenceParserHelper.literalAndClause(left, right);
                    if (thisOrThat(left, right, LITERAL, CNF)) return OptimizedSentenceParserHelper.literalAndCNF(left, right);
                    if (thisOrThat(left, right, LITERAL, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.literalAndGeneric(left, right);
                    if (thisAndThat(left, right, CLAUSE)) return OptimizedSentenceParserHelper.clauseAndClause(left, right);
                    if (thisOrThat(left, right, CLAUSE, CNF)) return OptimizedSentenceParserHelper.clauseAndCNF(left, right);
                    if (thisOrThat(left, right, CLAUSE, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.clauseAndGeneric(left, right);
                    if (thisAndThat(left, right, CNF)) return OptimizedSentenceParserHelper.cnfAndCNF(left, right);
                    if (thisOrThat(left, right, CNF, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.cnfAndGeneric(left, right);
                    if (thisAndThat(left, right, GENERIC_COMPLEX)) return OptimizedSentenceParserHelper.genericAndGeneric(left, right);
                }
            } case IMPLICATION -> {
                return new GenericComplexSentence(left, right, genericSentence.getConnective(), genericSentence.isNegated());
            } case BICONDITIONAL -> {
                return new GenericComplexSentence(left, right, genericSentence.getConnective(), genericSentence.isNegated());
            }
        }
        return null;
    }

    private static boolean thisOrThat(Sentence left, Sentence right, SentenceType type1,  SentenceType type2) {
        return (left.type() == type1 && right.type() == type2)
                    ||
               (left.type() == type2 && right.type() == type1);
    }

    private static boolean thisAndThat(Sentence left, Sentence right, SentenceType type) {
        return left.type() == type && right.type() == type;
    }

    private static Sentence optimizeCNF(CNFSentence cnf) {
        try {
            return cnf.minimalCNF();
        } catch (TautologyException e) {
            return Literal.TRUE;
        } catch (ContradictionException e) {
            return Literal.FALSE;
        }
    }

    static void main(String[] args) throws Exception {
        Sentence a = Sentence.optimizedParse("((A | !A) & (B & B)) => ((A | B) & (C | D | A))");
        System.out.println(a);
//
//        GenericComplexSentence gen = new GenericComplexSentence("true & false");
//        System.out.println(gen);

//        System.out.println(new GenericComplexSentence("A & B | C & (D | E | false)"));

        System.out.println(new CNFSentence("a | b"));
        System.out.println(new CNFSentence("(a | b)"));
        System.out.println(new CNFSentence("(a | b) & (c | d)"));
    }
}

package org.example.domain.sentence.propositional;

import org.example.domain.PropositionalSentenceType;
import org.example.domain.SatisfiabilityType;
import org.example.domain.Sentences;
import org.example.domain.sentence.*;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.truth_table.TruthTable;
import org.example.util.OptimizedPropositionalSentenceParserHelper;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;

import static org.example.domain.PropositionalSentenceType.*;
import static org.example.domain.PropositionalSentenceType.GENERIC_COMPLEX;

/**
 * @author aram.azatyan | 5/8/2024 11:00 AM
 */
public sealed interface PropositionalSentence extends Sentence permits AbstractPropositionalSentence {
    PropositionalSentenceType type();

    PropositionalCNFSentence minimalCNF() throws ContradictionException, TautologyException;

    SatisfiabilityType satisfiabilityType();

    TruthTable truthTable() throws ContradictionException, TautologyException;

    static boolean isEquivalent(PropositionalSentence s1, PropositionalSentence s2) {
        if (s1 == null || s2 == null) throw new IllegalArgumentException("null param");
        if (s1.satisfiabilityType() != s2.satisfiabilityType()) return false;
        if (s1.satisfiabilityType() == SatisfiabilityType.TAUTOLOGY &&
                s2.satisfiabilityType() == SatisfiabilityType.TAUTOLOGY) return true;
        if (s1.satisfiabilityType() == SatisfiabilityType.CONTRADICTION &&
                s2.satisfiabilityType() == SatisfiabilityType.CONTRADICTION) return true;

        try {
            Sentence opt1 = s1.minimalCNF();
            Sentence opt2 = s2.minimalCNF();

            if (((PropositionalCNFSentence) opt1).isCanonical()) {
                try {
                    opt1 = Sentences.optimizeCanonicalCNF((PropositionalCNFSentence) opt1);
                } catch (TautologyException e) {
                    opt1 = Literal.TRUE;
                } catch (ContradictionException e) {
                    opt1 = Literal.FALSE;
                }
            }

            if (((PropositionalCNFSentence) opt2).isCanonical()) {
                try {
                    opt2 = Sentences.optimizeCanonicalCNF((PropositionalCNFSentence) opt2);
                } catch (TautologyException e) {
                    opt2 = Literal.TRUE;
                } catch (ContradictionException e) {
                    opt2 = Literal.FALSE;
                }
            }

            if ((((PropositionalSentence) opt1).type() == PropositionalSentenceType.LITERAL && ((PropositionalSentence) opt2).type() == PropositionalSentenceType.LITERAL)
                    ||
                    (((PropositionalSentence) opt1).type() == PropositionalSentenceType.CNF && ((PropositionalSentence) opt2).type() == PropositionalSentenceType.CNF)) return opt1.equals(opt2);
        } catch (TautologyException | ContradictionException ignored) {}

        return false;
    }

    static PropositionalSentence optimizedParse(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new IllegalArgumentException();

        PropositionalSentence sentence;

        if (expression.contains(SentenceUtils.IMPLICATION) || expression.contains(SentenceUtils.BICONDITIONAL)) {
            sentence = Sentences.parseGenericExpression(expression);
        } else if (expression.contains(SentenceUtils.AND)) {
            try {
                sentence = optimizeCNF(Sentences.parseCNFExpression(expression));
                if (sentence.type() == CNF) {
                    PropositionalCNFSentence cnf = (PropositionalCNFSentence) sentence;
                    if (cnf.size() == 1) {
                        PropositionalClause clause = cnf.getClauses().iterator().next();
                        if (clause.size() == 1) sentence = clause.getLiterals().iterator().next();
                        else sentence = clause;
                    }
                }
            } catch (ParseException e) {
                sentence = Sentences.parseGenericExpression(expression);
            }
        } else if (expression.contains(SentenceUtils.OR)) {
            try {
                sentence = optimizeCNF(new PropositionalCNFSentence(Sentences.parseClauseExpression(expression)));
                if (sentence.type() == CNF) {
                    PropositionalCNFSentence cnf = (PropositionalCNFSentence) sentence;
                    if (cnf.size() == 1) {
                        PropositionalClause clause = cnf.getClauses().iterator().next();
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
        return sentence.type() != GENERIC_COMPLEX ? sentence : tryOptimize((GenericComplexPropositionalSentence) sentence);
    }

    private static PropositionalSentence tryOptimize(GenericComplexPropositionalSentence genericSentence) throws ParseException {
        PropositionalSentence left = optimizedParse(genericSentence.getLeftSentence().toString());
        PropositionalSentence right = optimizedParse(genericSentence.getRightSentence().toString());

        switch (genericSentence.getConnective()) {
            case OR -> {
                if (genericSentence.isNegated()) {
                    if (thisAndThat(left, right, LITERAL)) return OptimizedPropositionalSentenceParserHelper.literalNegatedOrLiteral(left, right);
                    if (thisOrThat(left, right, LITERAL, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.literalNegatedOrClause(left, right);
                    if (thisOrThat(left, right, LITERAL, CNF)) return OptimizedPropositionalSentenceParserHelper.literalNegatedOrCNF(left, right);
                    if (thisOrThat(left, right, LITERAL, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.literalNegatedOrGeneric(left, right);
                    if (thisAndThat(left, right, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.clauseNegatedOrClause(left, right);
                    if (thisOrThat(left, right, CLAUSE, CNF)) return OptimizedPropositionalSentenceParserHelper.clauseNegatedOrCNF(left, right);
                    if (thisOrThat(left, right, CLAUSE, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.clauseNegatedOrGeneric(left, right);
                    if (thisAndThat(left, right, CNF)) return OptimizedPropositionalSentenceParserHelper.cnfNegatedOrCNF(left, right);
                    if (thisOrThat(left, right, CNF, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.cnfNegatedOrGeneric(left, right);
                    if (thisAndThat(left, right, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.genericNegatedOrGeneric(left, right);
                } else {
                    if (thisAndThat(left, right, LITERAL)) return OptimizedPropositionalSentenceParserHelper.literalOrLiteral(left, right);
                    if (thisOrThat(left, right, LITERAL, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.literalOrClause(left, right);
                    if (thisOrThat(left, right, LITERAL, CNF)) return OptimizedPropositionalSentenceParserHelper.literalOrCNF(left, right);
                    if (thisOrThat(left, right, LITERAL, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.literalOrGeneric(left, right);
                    if (thisAndThat(left, right, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.clauseOrClause(left, right);
                    if (thisOrThat(left, right, CLAUSE, CNF)) return OptimizedPropositionalSentenceParserHelper.clauseOrCNF(left, right);
                    if (thisOrThat(left, right, CLAUSE, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.clauseOrGeneric(left, right);
                    if (thisAndThat(left, right, CNF)) return OptimizedPropositionalSentenceParserHelper.cnfOrCNF(left, right);
                    if (thisOrThat(left, right, CNF, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.cnfOrGeneric(left, right);
                    if (thisAndThat(left, right, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.genericOrGeneric(left, right);
                }
            } case AND -> {
                if (genericSentence.isNegated()) {
                    if (thisAndThat(left, right, LITERAL)) return OptimizedPropositionalSentenceParserHelper.literalNegatedAndLiteral(left, right);
                    if (thisOrThat(left, right, LITERAL, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.literalNegatedAndClause(left, right);
                    if (thisOrThat(left, right, LITERAL, CNF)) return OptimizedPropositionalSentenceParserHelper.literalNegatedAndCNF(left, right);
                    if (thisOrThat(left, right, LITERAL, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.literalNegatedAndGeneric(left, right);
                    if (thisAndThat(left, right, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.clauseNegatedAndClause(left, right);
                    if (thisOrThat(left, right, CLAUSE, CNF)) return OptimizedPropositionalSentenceParserHelper.clauseNegatedAndCNF(left, right);
                    if (thisOrThat(left, right, CLAUSE, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.clauseNegatedAndGeneric(left, right);
                    if (thisAndThat(left, right, CNF)) return OptimizedPropositionalSentenceParserHelper.cnfNegatedAndCNF(left, right);
                    if (thisOrThat(left, right, CNF, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.cnfNegatedAndGeneric(left, right);
                    if (thisAndThat(left, right, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.genericNegatedAndGeneric(left, right);
                } else {
                    if (thisAndThat(left, right, LITERAL)) return OptimizedPropositionalSentenceParserHelper.literalAndLiteral(left, right);
                    if (thisOrThat(left, right, LITERAL, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.literalAndClause(left, right);
                    if (thisOrThat(left, right, LITERAL, CNF)) return OptimizedPropositionalSentenceParserHelper.literalAndCNF(left, right);
                    if (thisOrThat(left, right, LITERAL, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.literalAndGeneric(left, right);
                    if (thisAndThat(left, right, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.clauseAndClause(left, right);
                    if (thisOrThat(left, right, CLAUSE, CNF)) return OptimizedPropositionalSentenceParserHelper.clauseAndCNF(left, right);
                    if (thisOrThat(left, right, CLAUSE, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.clauseAndGeneric(left, right);
                    if (thisAndThat(left, right, CNF)) return OptimizedPropositionalSentenceParserHelper.cnfAndCNF(left, right);
                    if (thisOrThat(left, right, CNF, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.cnfAndGeneric(left, right);
                    if (thisAndThat(left, right, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.genericAndGeneric(left, right);
                }
            } case IMPLICATION -> {
                if (genericSentence.isNegated()) {
                    if (thisAndThat(left, right, LITERAL)) return OptimizedPropositionalSentenceParserHelper.literalNegatedImplicationLiteral(left, right);
                    if (thisOrThat(left, right, LITERAL, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.literalNegatedImplicationClause(left, right);
                    if (thisOrThat(left, right, LITERAL, CNF)) return OptimizedPropositionalSentenceParserHelper.literalNegatedImplicationCNF(left, right);
                    if (thisOrThat(left, right, LITERAL, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.literalNegatedImplicationGeneric(left, right);
                    if (thisAndThat(left, right, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.clauseNegatedImplicationClause(left, right);
                    if (thisOrThat(left, right, CLAUSE, CNF)) return OptimizedPropositionalSentenceParserHelper.clauseNegatedImplicationCNF(left, right);
                    if (thisOrThat(left, right, CLAUSE, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.clauseNegatedImplicationGeneric(left, right);
                    if (thisAndThat(left, right, CNF)) return OptimizedPropositionalSentenceParserHelper.cnfNegatedImplicationCNF(left, right);
                    if (thisOrThat(left, right, CNF, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.cnfNegatedImplicationGeneric(left, right);
                    if (thisAndThat(left, right, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.genericNegatedImplicationGeneric(left, right);
                } else {
                    if (thisAndThat(left, right, LITERAL)) return OptimizedPropositionalSentenceParserHelper.literalImplicationLiteral(left, right);
                    if (thisOrThat(left, right, LITERAL, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.literalImplicationClause(left, right);
                    if (thisOrThat(left, right, LITERAL, CNF)) return OptimizedPropositionalSentenceParserHelper.literalImplicationCNF(left, right);
                    if (thisOrThat(left, right, LITERAL, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.literalImplicationGeneric(left, right);
                    if (thisAndThat(left, right, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.clauseImplicationClause(left, right);
                    if (thisOrThat(left, right, CLAUSE, CNF)) return OptimizedPropositionalSentenceParserHelper.clauseImplicationCNF(left, right);
                    if (thisOrThat(left, right, CLAUSE, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.clauseImplicationGeneric(left, right);
                    if (thisAndThat(left, right, CNF)) return OptimizedPropositionalSentenceParserHelper.cnfImplicationCNF(left, right);
                    if (thisOrThat(left, right, CNF, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.cnfImplicationGeneric(left, right);
                    if (thisAndThat(left, right, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.genericImplicationGeneric(left, right);
                }
            } case BICONDITIONAL -> {
                if (genericSentence.isNegated()) {
                    if (thisAndThat(left, right, LITERAL)) return OptimizedPropositionalSentenceParserHelper.literalNegatedBiconditionalLiteral(left, right);
                    if (thisOrThat(left, right, LITERAL, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.literalNegatedBiconditionalClause(left, right);
                    if (thisOrThat(left, right, LITERAL, CNF)) return OptimizedPropositionalSentenceParserHelper.literalNegatedBiconditionalCNF(left, right);
                    if (thisOrThat(left, right, LITERAL, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.literalNegatedBiconditionalGeneric(left, right);
                    if (thisAndThat(left, right, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.clauseNegatedBiconditionalClause(left, right);
                    if (thisOrThat(left, right, CLAUSE, CNF)) return OptimizedPropositionalSentenceParserHelper.clauseNegatedBiconditionalCNF(left, right);
                    if (thisOrThat(left, right, CLAUSE, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.clauseNegatedBiconditionalGeneric(left, right);
                    if (thisAndThat(left, right, CNF)) return OptimizedPropositionalSentenceParserHelper.cnfNegatedBiconditionalCNF(left, right);
                    if (thisOrThat(left, right, CNF, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.cnfNegatedBiconditionalGeneric(left, right);
                    if (thisAndThat(left, right, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.genericNegatedBiconditionalGeneric(left, right);
                } else {
                    if (thisAndThat(left, right, LITERAL)) return OptimizedPropositionalSentenceParserHelper.literalBiconditionalLiteral(left, right);
                    if (thisOrThat(left, right, LITERAL, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.literalBiconditionalClause(left, right);
                    if (thisOrThat(left, right, LITERAL, CNF)) return OptimizedPropositionalSentenceParserHelper.literalBiconditionalCNF(left, right);
                    if (thisOrThat(left, right, LITERAL, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.literalBiconditionalGeneric(left, right);
                    if (thisAndThat(left, right, CLAUSE)) return OptimizedPropositionalSentenceParserHelper.clauseBiconditionalClause(left, right);
                    if (thisOrThat(left, right, CLAUSE, CNF)) return OptimizedPropositionalSentenceParserHelper.clauseBiconditionalCNF(left, right);
                    if (thisOrThat(left, right, CLAUSE, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.clauseBiconditionalGeneric(left, right);
                    if (thisAndThat(left, right, CNF)) return OptimizedPropositionalSentenceParserHelper.cnfBiconditionalCNF(left, right);
                    if (thisOrThat(left, right, CNF, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.cnfBiconditionalGeneric(left, right);
                    if (thisAndThat(left, right, GENERIC_COMPLEX)) return OptimizedPropositionalSentenceParserHelper.genericBiconditionalGeneric(left, right);
                }
            }
        }
        return null;
    }

    private static boolean thisOrThat(PropositionalSentence left, PropositionalSentence right,
                                      PropositionalSentenceType type1, PropositionalSentenceType type2) {
        return (left.type() == type1 && right.type() == type2)
                ||
                (left.type() == type2 && right.type() == type1);
    }

    private static boolean thisAndThat(PropositionalSentence left, PropositionalSentence right,
                                       PropositionalSentenceType type) {
        return left.type() == type && right.type() == type;
    }

    private static PropositionalSentence optimizeCNF(PropositionalCNFSentence cnf) {
        try {
            return cnf.minimalCNF();
        } catch (TautologyException e) {
            return Literal.TRUE;
        } catch (ContradictionException e) {
            return Literal.FALSE;
        }
    }
}

package org.example.domain;

import org.example.algo.PropositionalResolution;
import org.example.cnf_util.CNFConverter;
import org.example.domain.sentence.Sentence;
import org.example.domain.sentence.fol.*;
import org.example.domain.sentence.fol.term.Function;
import org.example.domain.sentence.propositional.*;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.parser.CNFExpressionParser;
import org.example.parser.FOLCNFExpressionParser;
import org.example.parser.FOLInfixExpressionParser;
import org.example.parser.InfixExpressionParser;

import java.text.ParseException;
import java.util.*;

/**
 * @author aram.azatyan | 2/14/2024 3:42 PM
 */
public class Sentences {

    public static Literal parseLiteralExpression(String expression) throws ParseException {
        return InfixExpressionParser.parseLiteral(expression);
    }

    public static GenericComplexPropositionalSentence parseGenericExpression(String expression) throws ParseException {
        return InfixExpressionParser.parseGeneric(expression);
    }

    public static PropositionalClause parseClauseExpression(String expression) throws ParseException {
        return CNFExpressionParser.parseClause(expression);
    }

    public static PropositionalCNFSentence parseCNFExpression(String expression) throws ParseException {
        return CNFExpressionParser.parseCNF(expression);
    }

    public static Predicate parsePredicateExpression(String expression) throws ParseException {
        return FOLInfixExpressionParser.parsePredicate(expression);
    }

    public static Function parseFunctionExpression(String expression) throws ParseException {
        return FOLInfixExpressionParser.parseFunction(expression);
    }

    public static GenericComplexFOLSentence parseGenericFOLExpression(String expression) throws ParseException {
        return FOLInfixExpressionParser.parseGeneric(expression);
    }

    public static FOLClause parseFOLClauseExpression(String expression) throws ParseException {
        return FOLCNFExpressionParser.parseClause(expression);
    }

    public static FOLCNFSentence parseFOLCNFExpression(String expression) throws ParseException {
        return FOLCNFExpressionParser.parseCNF(expression);
    }

    public static CNFSentence toCNF(Sentence sentence) throws ContradictionException, TautologyException {
        return CNFConverter.toCNF(sentence);
    }

    public static PropositionalCNFSentence optimizeCNF(PropositionalCNFSentence sentence) throws ContradictionException, TautologyException {
        if (sentence == null) throw new IllegalArgumentException("null param");
        LinkedHashSet<PropositionalClause> clauses = new LinkedHashSet<>();
        for (PropositionalClause clause : sentence.getClauses()) {
            try {
                clauses.add(optimizeClause(clause));
            } catch (TautologyException ignored) {}
        }
        if (clauses.isEmpty()) throw new TautologyException();

        Set<PropositionalClause> toBeRemoved = new HashSet<>();
        Map<PropositionalClause, Set<Literal>> clauseRefinementMap = new HashMap<>();

        for (PropositionalClause compared : clauses) {
            for (PropositionalClause current : clauses) {
                if (compared.equals(current)) continue;
                if (compared.size() == 1 || current.size() == 1) 
                    optimizeForOneLiteralClause(compared, current, toBeRemoved, clauseRefinementMap);
                else {
                    if (current.getLiterals().containsAll(compared.getLiterals())) {
                        toBeRemoved.add(current);
                    } else if (compared.getLiterals().containsAll(current.getLiterals())) {
                        toBeRemoved.add(compared);
                    }
                }
            }
        }

        clauses.removeIf(toBeRemoved::contains);
        if (clauses.isEmpty()) throw new ContradictionException();


        clauseRefinementMap.entrySet().removeIf(entry -> !clauses.contains(entry.getKey()));
        if (clauseRefinementMap.isEmpty()) return new PropositionalCNFSentence(clauses);

        Set<PropositionalClause> toBeAdded = new HashSet<>();
        for (Iterator<PropositionalClause> iterator = clauses.iterator(); iterator.hasNext();) {
            PropositionalClause clause = iterator.next();
            if (clauseRefinementMap.containsKey(clause)) {
                Set<Literal> toBeRemovedLiterals = clauseRefinementMap.get(clause);
                LinkedHashSet<Literal> clauseLiterals = clause.getLiterals();
                clauseLiterals.removeIf(toBeRemovedLiterals::contains);
                if (!clauseLiterals.isEmpty()) toBeAdded.add(new PropositionalClause(clauseLiterals));
                iterator.remove();
            }
        }

        clauses.addAll(toBeAdded);
        if (clauses.isEmpty()) throw new ContradictionException();

        return new PropositionalCNFSentence(clauses);
    }

    // TODO: 4/3/2024 check
    public static PropositionalCNFSentence optimizeCanonicalCNF(PropositionalCNFSentence ccnf) throws TautologyException, ContradictionException {
        if (ccnf == null) throw new IllegalArgumentException("null param");
        if (!ccnf.isCanonical()) throw new IllegalArgumentException("not canonical cnf");

        Set<PropositionalCNFSentence> set = new HashSet<>();
        set.add(ccnf);
        return Sentences.optimizeCNF(new PropositionalCNFSentence(new LinkedHashSet<>(new PropositionalResolution(set, false).resolveAndGet())));
    }
    
    private static void optimizeForOneLiteralClause(PropositionalClause compared, PropositionalClause current,
                                                    Set<PropositionalClause> toBeRemoved,
                                                    Map<PropositionalClause, Set<Literal>> clauseRefinementMap)
            throws ContradictionException {
        if (compared.size() == 1 && current.size() == 1) {
            Literal l1 = current.getLiterals().iterator().next();
            Literal l2 = compared.getLiterals().iterator().next();
            if (l1.equalsIgnoreNegation(l2)) {
                throw new ContradictionException();
            }
        } else if (compared.size() == 1)
            potentialRefineClause(current, compared, toBeRemoved, clauseRefinementMap);
        else if (current.size() == 1)
            potentialRefineClause(compared, current, toBeRemoved, clauseRefinementMap);
    }
    
    private static void potentialRefineClause(PropositionalClause larger, PropositionalClause smaller,
                                              Set<PropositionalClause> toBeRemoved, Map<PropositionalClause, Set<Literal>> clauseRefinementMap)
            throws ContradictionException {
        Literal literal = smaller.getLiterals().iterator().next();
        if (larger.getLiterals().contains(literal)) toBeRemoved.add(larger);
        else if (larger.getLiterals().contains(new Literal(literal.getName(), !literal.isNegated()))) {
            if (clauseRefinementMap.containsKey(larger)) {
                clauseRefinementMap.get(larger).add(new Literal(literal.getName(), !literal.isNegated()));
                Set<Literal> literalSet = clauseRefinementMap.get(larger);
                if (larger.size() == literalSet.size()) {
                    throw new ContradictionException();
                }
            } else {
                Set<Literal> set = new HashSet<>();
                set.add(new Literal(literal.getName(), !literal.isNegated()));
                clauseRefinementMap.put(larger, set);
            }
        }
    }

    public static PropositionalClause optimizeClause(PropositionalClause clause) throws TautologyException, ContradictionException {
        if (clause == null) throw new IllegalArgumentException("null param");
        LinkedHashSet<Literal> literals = clause.getLiterals();
        literals.removeIf(l -> l == Literal.FALSE);
        if (literals.isEmpty()) throw new ContradictionException();

        for (Literal compared : literals) {
            for (Literal current : literals) {
                if (current == Literal.TRUE) throw new TautologyException();
                if (compared.equals(current)) continue;
                if (compared.equalsIgnoreNegation(current)) throw new TautologyException();
            }
        }
        return new PropositionalClause(literals);
    }

}

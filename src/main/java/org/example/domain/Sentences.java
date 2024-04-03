package org.example.domain;

import org.example.algo.Resolution;
import org.example.cnf_util.CNFConverter;
import org.example.domain.sentence.*;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.parser.CNFExpressionParser;
import org.example.parser.InfixExpressionParser;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 2/14/2024 3:42 PM
 */
public class Sentences {

    public static Literal parseLiteralExpression(String expression) throws ParseException {
        return InfixExpressionParser.parseLiteral(expression);
    }

    public static GenericComplexSentence parseGenericExpression(String expression) throws ParseException {
        return InfixExpressionParser.parseGeneric(expression);
    }

    public static Clause parseClauseExpression(String expression) throws ParseException {
        return CNFExpressionParser.parseClauseExpression(expression);
    }

    //-- a | b & c | d -> weak cnf expression, (a | b) & (c | d) -> strong cnf expression
    public static CNFSentence parseCNFExpression(String expression,
                                                 boolean isPossibleWeakCNFExpression) throws ParseException {
        return CNFExpressionParser.parseCNFExpression(expression, isPossibleWeakCNFExpression);
    }

    public static CNFSentence toCNF(Sentence sentence) throws ContradictionException, TautologyException {
        return CNFConverter.toCNF(sentence);
    }

    public static CNFSentence optimizeCNF(CNFSentence sentence) throws ContradictionException, TautologyException {
        if (sentence == null) throw new IllegalArgumentException("null param");
        LinkedHashSet<Clause> clauses = new LinkedHashSet<>();
        for (Clause clause : sentence.getClauses()) {
            try {
                clauses.add(optimizeClause(clause));
            } catch (TautologyException ignored) {}
        }
        if (clauses.isEmpty()) throw new TautologyException();

        Set<Clause> toBeRemoved = new HashSet<>();
        Map<Clause, Set<Literal>> clauseRefinementMap = new HashMap<>();

        for (Clause compared : clauses) {
            for (Clause current : clauses) {
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
        if (clauseRefinementMap.isEmpty()) return new CNFSentence(clauses);

        Set<Clause> toBeAdded = new HashSet<>();
        for (Iterator<Clause> iterator = clauses.iterator(); iterator.hasNext();) {
            Clause clause = iterator.next();
            if (clauseRefinementMap.containsKey(clause)) {
                Set<Literal> toBeRemovedLiterals = clauseRefinementMap.get(clause);
                LinkedHashSet<Literal> clauseLiterals = clause.getLiterals();
                clauseLiterals.removeIf(toBeRemovedLiterals::contains);
                if (!clauseLiterals.isEmpty()) toBeAdded.add(new Clause(clauseLiterals));
                iterator.remove();
            }
        }

        clauses.addAll(toBeAdded);
        if (clauses.isEmpty()) throw new ContradictionException();

        return new CNFSentence(clauses);
    }

    // TODO: 4/3/2024 check
    public static CNFSentence optimizeCanonicalCNF(CNFSentence ccnf) throws TautologyException, ContradictionException {
        if (ccnf == null) throw new IllegalArgumentException("null param");
        if (!ccnf.isCanonical()) throw new IllegalArgumentException("not canonical cnf");

        Set<CNFSentence> set = new HashSet<>();
        set.add(ccnf);
        return Sentences.optimizeCNF(new CNFSentence(new LinkedHashSet<>(new Resolution(set, false).resolveAndGet())));
    }
    
    private static void optimizeForOneLiteralClause(Clause compared, Clause current,
                                                    Set<Clause> toBeRemoved,
                                                    Map<Clause, Set<Literal>> clauseRefinementMap)
            throws ContradictionException {
        if (compared.size() == 1 && current.size() == 1) {
            Literal l1 = current.getLiteralList().get(0);
            Literal l2 = compared.getLiteralList().get(0);
            if (l1.equalsIgnoreNegation(l2)) {
                throw new ContradictionException();
            }
        } else if (compared.size() == 1 && current.size() != 1) 
            potentialRefineClause(current, compared, toBeRemoved, clauseRefinementMap);
        else if (current.size() == 1 && compared.size() != 1) 
            potentialRefineClause(compared, current, toBeRemoved, clauseRefinementMap);
    }
    
    private static void potentialRefineClause(Clause larger, Clause smaller,
                                              Set<Clause> toBeRemoved, Map<Clause, Set<Literal>> clauseRefinementMap)
            throws ContradictionException {
        Literal literal = smaller.getLiteralList().get(0);
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

    public static Clause optimizeClause(Clause clause) throws TautologyException, ContradictionException {
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
        return new Clause(literals);
    }

}

package org.example.domain;

import org.example.algo.Resolution;
import org.example.cnf_util.CNFConverter;
import org.example.domain.sentence.BasicLogicElement;
import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
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

    @SuppressWarnings("unchecked")
    public static CNFSentence optimizeCNF(CNFSentence sentence) throws ContradictionException, TautologyException {
        if (sentence == null) throw new IllegalArgumentException("null param");

        LogicType logicType = sentence.logicType();

        LinkedHashSet<Clause> clauses = new LinkedHashSet<>();
        for (Clause clause : sentence.clauses()) {
            try {
                clauses.add(optimizeClause(clause));
            } catch (TautologyException ignored) {}
        }
        if (clauses.isEmpty()) throw new TautologyException();

        Set<Clause> toBeRemoved = new HashSet<>();
        Map<Clause, Set<BasicLogicElement>> clauseRefinementMap = new HashMap<>();

        for (Clause compared : clauses) {
            for (Clause current : clauses) {
                if (compared.equals(current)) continue;
                if (compared.size() == 1 || current.size() == 1) 
                    optimizeForOneElementClause(compared, current, toBeRemoved, clauseRefinementMap);
                else {
                    if (current.basicElements().containsAll(compared.basicElements())) {
                        toBeRemoved.add(current);
                    } else if (compared.basicElements().containsAll(current.basicElements())) {
                        toBeRemoved.add(compared);
                    }
                }
            }
        }

        clauses.removeIf(toBeRemoved::contains);
        if (clauses.isEmpty()) throw new ContradictionException();

        clauseRefinementMap.entrySet().removeIf(entry -> !clauses.contains(entry.getKey()));
        if (clauseRefinementMap.isEmpty()) {
            LinkedHashSet<? extends Clause> temp = clauses;
            switch (logicType) {
                case PROPOSITIONAL -> {return new PropositionalCNFSentence((LinkedHashSet<PropositionalClause>) temp);}
                case FOL -> {return new FOLCNFSentence((LinkedHashSet<FOLClause>) temp);}
            }
        }

        Set<Clause> toBeAdded = new HashSet<>();
        for (Iterator<Clause> iterator = clauses.iterator(); iterator.hasNext();) {
            Clause clause = iterator.next();
            if (clauseRefinementMap.containsKey(clause)) {
                Set<BasicLogicElement> toBeRemovedLiterals = clauseRefinementMap.get(clause);
                LinkedHashSet<BasicLogicElement> clauseLiterals = clause.basicElements();
                clauseLiterals.removeIf(toBeRemovedLiterals::contains);
                if (!clauseLiterals.isEmpty()) {
                    LinkedHashSet<? extends BasicLogicElement> temp = clauseLiterals;
                    switch (logicType) {
                        case PROPOSITIONAL -> toBeAdded.add(new PropositionalClause((LinkedHashSet<Literal>) temp));
                        case FOL -> toBeAdded.add(new FOLClause((LinkedHashSet<Predicate>) temp));
                    }
                }
                iterator.remove();
            }
        }

        clauses.addAll(toBeAdded);
        if (clauses.isEmpty()) throw new ContradictionException();

        LinkedHashSet<? extends Clause> temp = clauses;

        switch (logicType) {
            case PROPOSITIONAL -> {return new PropositionalCNFSentence((LinkedHashSet<PropositionalClause>) temp);}
            case FOL -> {return new FOLCNFSentence((LinkedHashSet<FOLClause>) temp);}
            default -> {return null;}
        }
    }

    @SuppressWarnings("unchecked")
    public static CNFSentence optimizeCanonicalCNF(CNFSentence ccnf) throws TautologyException, ContradictionException {
        if (ccnf == null) throw new IllegalArgumentException("null param");
        if (!ccnf.isCanonical()) throw new IllegalArgumentException("not canonical cnf");

        Set<CNFSentence> set = new HashSet<>();
        set.add(ccnf);

        LogicType logicType = ccnf.logicType();

        Set<? extends Clause> temp = new Resolution(set, false).resolveAndGet();

        switch (logicType) {
            case PROPOSITIONAL -> {return Sentences.optimizeCNF(new PropositionalCNFSentence(new LinkedHashSet<>((HashSet<PropositionalClause>) temp)));}
            case FOL -> {return Sentences.optimizeCNF(new FOLCNFSentence(new LinkedHashSet<>((HashSet<FOLClause>) temp)));}
            default -> {return null;}
        }
    }

    private static void optimizeForOneElementClause(Clause compared, Clause current,
                                                    Set<Clause> toBeRemoved,
                                                    Map<Clause, Set<BasicLogicElement>> clauseRefinementMap)
            throws ContradictionException {
        if (compared.size() == 1 && current.size() == 1) {
            BasicLogicElement l1 = current.basicElements().iterator().next();
            BasicLogicElement l2 = compared.basicElements().iterator().next();
            if (l1.equalsIgnoreNegation(l2)) {
                throw new ContradictionException();
            }
        } else if (compared.size() == 1)
            potentialRefineClause(current, compared, toBeRemoved, clauseRefinementMap);
        else if (current.size() == 1)
            potentialRefineClause(compared, current, toBeRemoved, clauseRefinementMap);
    }

    private static void potentialRefineClause(Clause larger, Clause smaller,
                                              Set<Clause> toBeRemoved, Map<Clause, Set<BasicLogicElement>> clauseRefinementMap)
            throws ContradictionException {
        BasicLogicElement element = smaller.basicElements().iterator().next();
        if (larger.basicElements().contains(element)) toBeRemoved.add(larger);
        else if (larger.basicElements().contains(element.getNegated())) {
            if (clauseRefinementMap.containsKey(larger)) {
                clauseRefinementMap.get(larger).add(element.getNegated());
                Set<BasicLogicElement> basicLogicElementSet = clauseRefinementMap.get(larger);
                if (larger.size() == basicLogicElementSet.size()) throw new ContradictionException();
            } else {
                Set<BasicLogicElement> set = new HashSet<>();
                set.add(element.getNegated());
                clauseRefinementMap.put(larger, set);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static Clause optimizeClause(Clause clause) throws TautologyException, ContradictionException {
        if (clause == null) throw new IllegalArgumentException("null param");
        LinkedHashSet<BasicLogicElement> elements = clause.basicElements();
        elements.removeIf(l -> l == l.getFalse());
        if (elements.isEmpty()) throw new ContradictionException();

        for (BasicLogicElement compared : elements) {
            for (BasicLogicElement current : elements) {
                if (current == current.getTrue()) throw new TautologyException();
                if (compared.equals(current)) continue;
                if (compared.equalsIgnoreNegation(current)) throw new TautologyException();
            }
        }

        LinkedHashSet<? extends BasicLogicElement> temp = elements;

        switch (elements.iterator().next().logicType()) {
            case PROPOSITIONAL -> {return new PropositionalClause((LinkedHashSet<Literal>) temp);}
            case FOL -> {return new FOLClause((LinkedHashSet<Predicate>) temp);}
            default -> {return null;}
        }
    }

}

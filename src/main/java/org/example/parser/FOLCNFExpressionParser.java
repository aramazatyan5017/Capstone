package org.example.parser;

import org.example.domain.sentence.fol.*;
import org.example.domain.sentence.fol.term.Constant;
import org.example.domain.sentence.fol.term.Function;
import org.example.domain.sentence.fol.term.Term;
import org.example.domain.sentence.fol.term.Variable;
import org.example.domain.supplementary.PostfixAndFuncArgCountMap;
import org.example.parser.supplementary.Token;
import org.example.parser.supplementary.TokenType;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;
import java.util.*;

import static org.example.parser.supplementary.TokenType.*;

/**
 * @author aram.azatyan | 4/17/2024 8:35 PM
 */
public class FOLCNFExpressionParser extends FOLLogicExpressionParser {

    public static FOLClause parseClause(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);
        if (expression.contains(SentenceUtils.IMPLICATION) || expression.contains(SentenceUtils.BICONDITIONAL) ||
                expression.contains(SentenceUtils.AND)) throw new ParseException("not a clause", -1);

        PostfixAndFuncArgCountMap postfixAndMap = postfixTokens(infixTokens(expression));
        return getClauseFromPostfix(postfixAndMap.postfix(), postfixAndMap.argCountMap());
    }

    public static FOLCNFSentence parseCNF(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);
        if (expression.contains(SentenceUtils.IMPLICATION) || expression.contains(SentenceUtils.BICONDITIONAL))
            throw new ParseException("not a cnf sentence", -1);

        PostfixAndFuncArgCountMap postfixAndMap = postfixTokens(infixTokens(expression));
//        postfixAndMap.postfix().forEach(System.out::print);
//        System.out.println();
        return getCNFFromPostfix(postfixAndMap.postfix(), postfixAndMap.argCountMap());
    }

    private static FOLClause getClauseFromPostfix(List<Token> postfix, Map<String, Integer> argCountMap) throws ParseException {
        validateClausePostfixTokens(postfix, argCountMap);

        LinkedHashSet<Predicate> predicates = new LinkedHashSet<>();
        int startIndex;
        int endIndex = postfix.size();
        boolean isEnd = true;

        for (int i = postfix.size() - 1; i >= 0; i--) {
            Token token = postfix.get(i);
            if (token.getType() == TokenType.PREDICATE && isEnd) {
                endIndex = i + 1;
                if (endIndex < postfix.size() && postfix.get(endIndex).getType() == NEGATION)
                    endIndex = i + 2;

                isEnd = !isEnd;
            } else if (token.getType() == TokenType.PREDICATE && !isEnd) {
                startIndex = i + 1;
                if (postfix.get(startIndex).getType() == NEGATION)
                    startIndex = i + 2;

                List<Token> tempList = postfix.subList(startIndex, endIndex);
                tempList.removeIf(e -> e.getType() == TokenType.CONNECTIVE);
                predicates.add(clausePostfixToPredicate(tempList, argCountMap));
                endIndex = startIndex;
            }
        }

        predicates.add(clausePostfixToPredicate(postfix.subList(0, endIndex), argCountMap));
        List<Predicate> reversedPredicates = new ArrayList<>(predicates);
        Collections.reverse(reversedPredicates);

        return new FOLClause(new LinkedHashSet<>(reversedPredicates));
    }

    private static void validateClausePostfixTokens(List<Token> postfix, Map<String, Integer> argCountMap) throws ParseException {
        ParseException invalidClause = new ParseException("invalid clause", -1);

        int predicateCount = 0;
        int orCount = 0;
        int argCount = 0;

        Token previous = postfix.get(0);
        switch (previous.getType()) {
            case PREDICATE -> predicateCount++;
            case CONNECTIVE -> orCount++;
            case FUNCTION, CONSTANT, VARIABLE -> argCount++;
            default -> throw invalidClause;
        }

        for (int i = 1; i < postfix.size(); i++) {
            Token current = postfix.get(i);

            switch (current.getType()) {
                case PREDICATE -> predicateCount++;
                case CONNECTIVE -> orCount++;
                case FUNCTION, CONSTANT, VARIABLE -> argCount++;
                case NEGATION -> {continue;}
                default -> throw invalidClause;
            }

            if (current.getType() == TokenType.NEGATION && previous.getType() == TokenType.CONNECTIVE)
                throw invalidClause;
        }

        int mapArgCount = postfix.stream()
                .filter(t -> argCountMap.containsKey(t.getValue()))
                .map(t -> argCountMap.get(t.getValue()))
                .reduce(Integer::sum).get();

        if (argCount != mapArgCount) throw invalidClause;
        if (predicateCount - orCount != 1) throw invalidClause;
    }

    private static FOLCNFSentence getCNFFromPostfix(List<Token> postfix, Map<String, Integer> argCountMap) throws ParseException {
        ParseException invalidCNF = new ParseException("invalid cnf", -1);

        Token andToken = new Token(TokenType.CONNECTIVE, SentenceUtils.AND);
        Token orToken = new Token(TokenType.CONNECTIVE, SentenceUtils.OR);

        if (!postfix.contains(andToken)) {
            return new FOLCNFSentence(getClauseFromPostfix(postfix, argCountMap));
        }

        for (int i = 1; i < postfix.size(); i++) {
            Token previous = postfix.get(i - 1);
            Token current = postfix.get(i);
            if (current.getType() == NEGATION &&
                    previous.getType() != PREDICATE) throw invalidCNF;
            if (current.equals(orToken) &&
                    previous.equals(andToken)) throw invalidCNF;
        }

        Deque<Object> stack = new LinkedList<>();
        FOLCNFSentence possCNF;

        try {
            for (int i = 0; i < postfix.size(); i++) {
                Token token = postfix.get(i);

                //                        if (token.getValue().equalsIgnoreCase("true"))
//                            stack.push(negated ? Literal.FALSE : Literal.TRUE);
//                        else if (token.getValue().equalsIgnoreCase("false"))
//                            stack.push(negated ? Literal.TRUE : Literal.FALSE);

                switch (token.getType()) {
                    case CONSTANT -> stack.push(new Constant(token.getValue()));
                    case VARIABLE -> stack.push(new Variable(token.getValue()));
                    case FUNCTION, PREDICATE -> {
                        List<Term> terms = new ArrayList<>();

                        int argCount = argCountMap.get(token.getValue());

                        while (!stack.isEmpty() && argCount > 0) {
                            if (stack.peek() instanceof Constant) terms.add((Constant) stack.pop());
                            else if (stack.peek() instanceof Variable) terms.add((Variable) stack.pop());
                            else if (stack.peek() instanceof Function) terms.add((Function) stack.pop());
                            else throw invalidCNF;
                            --argCount;
                        }

                        Collections.reverse(terms);
                        boolean negated = i + 1 < postfix.size() && postfix.get(i + 1).getType() == NEGATION;

                        stack.push(token.getType() == FUNCTION
                                ? new Function(token.getValue(), terms)
                                : new Predicate(token.getValue(), negated, terms));

                        if (negated) ++i;
                    }
                    case NEGATION -> throw invalidCNF;
                    case CONNECTIVE -> {
                        if (token.equals(orToken)) {
                            if (stack.size() < 2) throw invalidCNF;
                            Object obj1 = stack.pop();
                            Object obj2 = stack.pop();

                            if ((!(obj1 instanceof Predicate) && !(obj1 instanceof FOLClause))
                                    ||
                                (!(obj2 instanceof Predicate) && !(obj2 instanceof FOLClause))) {
                                throw invalidCNF;
                            }

                            if (obj1 instanceof Predicate p1 && obj2 instanceof Predicate p2) {
                                FOLClause clause = new FOLClause(p2, p1);
                                stack.push(clause.size() > 1 ? clause : clause.getPredicates().iterator().next());
                            } else if (obj1 instanceof Predicate p1) {
                                LinkedHashSet<Predicate> predicates = ((FOLClause) obj2).getPredicates();
                                predicates.add(p1);
                                stack.push(predicates.size() > 1 ? new FOLClause(predicates) : predicates.iterator().next());
                            } else if (obj2 instanceof Predicate l2) {
                                LinkedHashSet<Predicate> predicates = ((FOLClause) obj1).getPredicates();
                                LinkedHashSet<Predicate> newPredicates = new LinkedHashSet<>();
                                newPredicates.add(l2);
                                newPredicates.addAll(predicates);
                                stack.push(newPredicates.size() > 1 ? new FOLClause(newPredicates) : newPredicates.iterator().next());
                            } else {
                                LinkedHashSet<Predicate> predicates1 = ((FOLClause) obj1).getPredicates();
                                LinkedHashSet<Predicate> predicates2 = ((FOLClause) obj2).getPredicates();
                                predicates2.addAll(predicates1);
                                stack.push(predicates2.size() > 1 ? new FOLClause(predicates2) : predicates2.iterator().next());
                            }
                        } else {
                            if (stack.size() < 2) throw invalidCNF;
                            Object obj1 = stack.pop();
                            Object obj2 = stack.pop();

                            if (obj1 instanceof Predicate p1) obj1 = new FOLClause(p1);
                            if (obj2 instanceof Predicate p2) obj2 = new FOLClause(p2);

                            if ((!(obj1 instanceof FOLClause) && !(obj1 instanceof FOLCNFSentence))
                                    ||
                                    (!(obj2 instanceof FOLClause) && !(obj2 instanceof FOLCNFSentence))) {
                                throw invalidCNF;
                            }

                            if (obj1 instanceof FOLClause c1 && obj2 instanceof FOLClause c2) {
                                FOLCNFSentence cnf = new FOLCNFSentence(c2, c1);
                                stack.push(cnf.size() > 1 ? cnf : cnf.getClauses().iterator().next());
                            } else if (obj1 instanceof FOLClause c1) {
                                LinkedHashSet<FOLClause> clauses = ((FOLCNFSentence) obj2).getClauses();
                                clauses.add(c1);
                                stack.push(clauses.size() > 1 ? new FOLCNFSentence(clauses) : clauses.iterator().next());
                            } else if (obj2 instanceof FOLClause c2) {
                                LinkedHashSet<FOLClause> clauses = ((FOLCNFSentence) obj1).getClauses();
                                LinkedHashSet<FOLClause> newClauses = new LinkedHashSet<>();
                                newClauses.add(c2);
                                newClauses.addAll(clauses);
                                stack.push(newClauses.size() > 1 ? new FOLCNFSentence(newClauses) : newClauses.iterator().next());
                            } else {
                                LinkedHashSet<FOLClause> clauses1 = ((FOLCNFSentence) obj1).getClauses();
                                LinkedHashSet<FOLClause> clauses2 = ((FOLCNFSentence) obj2).getClauses();
                                clauses2.addAll(clauses1);
                                stack.push(clauses2.size() > 1 ? new FOLCNFSentence(clauses2) : clauses2.iterator().next());
                            }
                        }
                    }
                }
            }

            if (stack.size() != 1 || !(stack.pop() instanceof FOLCNFSentence cnf)) throw invalidCNF;
            possCNF = cnf;
        } catch (Exception e) {
            throw invalidCNF;
        }

        return possCNF;
    }

    private static Predicate clausePostfixToPredicate(List<Token> postfix, Map<String, Integer> argCountMap) {
        Deque<Object> stack = new ArrayDeque<>();
        for (int i = 0; i < postfix.size(); i++) {
            Token token = postfix.get(i);

            switch (token.getType()) {
                case CONSTANT, VARIABLE -> {
                    stack.push(token.getType() == CONSTANT
                            ? new Constant(token.getValue())
                            : new Variable(token.getValue()));
                }
                case FUNCTION, PREDICATE -> {
                    List<Term> terms = new ArrayList<>();

                    int argCount = argCountMap.get(token.getValue());

                    while (!stack.isEmpty() && argCount > 0) {
                        terms.add((Term) stack.pop());
                        --argCount;
                    }

                    Collections.reverse(terms);
                    boolean negated = i + 1 < postfix.size() && postfix.get(i + 1).getType() == NEGATION;

                    stack.push(token.getType() == FUNCTION
                            ? new Function(token.getValue(), terms)
                            : new Predicate(token.getValue(), negated, terms));

                    if (negated) ++i;
                }
            }
        }

        return (Predicate) stack.pop();
    }
}

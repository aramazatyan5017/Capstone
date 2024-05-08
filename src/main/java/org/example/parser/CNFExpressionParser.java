package org.example.parser;

import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;
import org.example.domain.sentence.propositional.Literal;
import org.example.parser.supplementary.Token;
import org.example.parser.supplementary.TokenType;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;
import java.util.*;

/**
 * @author aram.azatyan | 3/1/2024 4:06 PM
 */
public class CNFExpressionParser extends PropositionalLogicExpressionParser {

    public static PropositionalClause parseClause(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);
        if (expression.contains(SentenceUtils.IMPLICATION) || expression.contains(SentenceUtils.BICONDITIONAL) ||
            expression.contains(SentenceUtils.AND)) throw new ParseException("not a cnf sentence", -1);
        return getClauseFromPostfix(postfixTokens(infixTokens(expression)));
    }

    public static PropositionalCNFSentence parseCNF(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);
        if (expression.contains(SentenceUtils.IMPLICATION) || expression.contains(SentenceUtils.BICONDITIONAL)) throw
                new ParseException("not a cnf sentence", -1);

        return getCNFFromPostfix(postfixTokens(infixTokens(expression)));
    }

    private static PropositionalCNFSentence getCNFFromPostfix(List<Token> postfix) throws ParseException {
        ParseException invalidCNF = new ParseException("invalid cnf", -1);

        Token andToken = new Token(TokenType.CONNECTIVE, SentenceUtils.AND);
        Token orToken = new Token(TokenType.CONNECTIVE, SentenceUtils.OR);

        if (!postfix.contains(andToken)) {
            return new PropositionalCNFSentence(getClauseFromPostfix(postfix));
        }

        for (int i = 1; i < postfix.size(); i++) {
            Token previous = postfix.get(i - 1);
            Token current = postfix.get(i);
            if (current.getType() == TokenType.NEGATION &&
                    previous.getType() == TokenType.CONNECTIVE) throw invalidCNF;
            if (current.equals(orToken) &&
                    previous.equals(andToken)) throw invalidCNF;
        }

        Deque<Object> stack = new LinkedList<>();
        PropositionalCNFSentence possCNF;

        try {
            for (int i = 0; i < postfix.size(); i++) {
                Token token = postfix.get(i);

                switch (token.getType()) {
                    case WORD -> {
                        boolean negated = i + 1 < postfix.size() && postfix.get(i + 1).getType() == TokenType.NEGATION;

                        if (token.getValue().equalsIgnoreCase("true"))
                            stack.push(negated ? Literal.FALSE : Literal.TRUE);
                        else if (token.getValue().equalsIgnoreCase("false"))
                            stack.push(negated ? Literal.TRUE : Literal.FALSE);
                        else
                            stack.push(new Literal(token.getValue(), negated));

                        if (negated) i++;
                    }
                    case NEGATION -> throw invalidCNF;
                    case CONNECTIVE -> {
                        if (token.equals(orToken)) {
                            if (stack.size() < 2) throw invalidCNF;
                            Object obj1 = stack.pop();
                            Object obj2 = stack.pop();

                            if ((!(obj1 instanceof Literal) && !(obj1 instanceof PropositionalClause))
                                    ||
                                    (!(obj2 instanceof Literal) && !(obj2 instanceof PropositionalClause))) {
                                throw invalidCNF;
                            }

                            if (obj1 instanceof Literal l1 && obj2 instanceof Literal l2) {
                                PropositionalClause clause = new PropositionalClause(l2, l1);
                                stack.push(clause.size() > 1 ? clause : clause.getLiterals().iterator().next());
                            } else if (obj1 instanceof Literal l1) {
                                LinkedHashSet<Literal> literals = ((PropositionalClause) obj2).getLiterals();
                                literals.add(l1);
                                stack.push(literals.size() > 1 ? new PropositionalClause(literals) : literals.iterator().next());
                            } else if (obj2 instanceof Literal l2) {
                                LinkedHashSet<Literal> literals = ((PropositionalClause) obj1).getLiterals();
                                LinkedHashSet<Literal> newLiterals = new LinkedHashSet<>();
                                newLiterals.add(l2);
                                newLiterals.addAll(literals);
                                stack.push(newLiterals.size() > 1 ? new PropositionalClause(newLiterals) : newLiterals.iterator().next());
                            } else {
                                LinkedHashSet<Literal> literals1 = ((PropositionalClause) obj1).getLiterals();
                                LinkedHashSet<Literal> literals2 = ((PropositionalClause) obj2).getLiterals();
                                literals2.addAll(literals1);
                                stack.push(literals2.size() > 1 ? new PropositionalClause(literals2) : literals2.iterator().next());
                            }
                        } else {
                            if (stack.size() < 2) throw invalidCNF;
                            Object obj1 = stack.pop();
                            Object obj2 = stack.pop();

                            if (obj1 instanceof Literal l1) obj1 = new PropositionalClause(l1);
                            if (obj2 instanceof Literal l2) obj2 = new PropositionalClause(l2);

                            if ((!(obj1 instanceof PropositionalClause) && !(obj1 instanceof PropositionalCNFSentence))
                                    ||
                                (!(obj2 instanceof PropositionalClause) && !(obj2 instanceof PropositionalCNFSentence))) {
                                throw invalidCNF;
                            }

                            if (obj1 instanceof PropositionalClause c1 && obj2 instanceof PropositionalClause c2) {
                                PropositionalCNFSentence cnf = new PropositionalCNFSentence(c2, c1);
                                stack.push(cnf.size() > 1 ? cnf : cnf.getClauses().iterator().next());
                            } else if (obj1 instanceof PropositionalClause c1) {
                                LinkedHashSet<PropositionalClause> clauses = ((PropositionalCNFSentence) obj2).getClauses();
                                clauses.add(c1);
                                stack.push(clauses.size() > 1 ? new PropositionalCNFSentence(clauses) : clauses.iterator().next());
                            } else if (obj2 instanceof PropositionalClause c2) {
                                LinkedHashSet<PropositionalClause> clauses = ((PropositionalCNFSentence) obj1).getClauses();
                                LinkedHashSet<PropositionalClause> newClauses = new LinkedHashSet<>();
                                newClauses.add(c2);
                                newClauses.addAll(clauses);
                                stack.push(newClauses.size() > 1 ? new PropositionalCNFSentence(newClauses) : newClauses.iterator().next());
                            } else {
                                LinkedHashSet<PropositionalClause> clauses1 = ((PropositionalCNFSentence) obj1).getClauses();
                                LinkedHashSet<PropositionalClause> clauses2 = ((PropositionalCNFSentence) obj2).getClauses();
                                clauses2.addAll(clauses1);
                                stack.push(clauses2.size() > 1 ? new PropositionalCNFSentence(clauses2) : clauses2.iterator().next());
                            }
                        }
                    }
                }
            }

            if (stack.size() != 1 || !(stack.pop() instanceof PropositionalCNFSentence cnf)) throw invalidCNF;
            possCNF = cnf;
        } catch (Exception e) {
            throw invalidCNF;
        }

        return possCNF;
    }

    private static PropositionalClause getClauseFromPostfix(List<Token> postfix) throws ParseException {
        validateClausePostfixTokens(postfix);

        LinkedHashSet<Literal> literals = new LinkedHashSet<>();
        boolean negated = false;
        String literalName = null;

        for (Token token : postfix) {
            switch (token.getType()) {
                case NEGATION -> negated = true;
                case WORD -> {
                    if (literalName != null) {
                        literals.add(getLiteral(literalName, negated));
                        negated = false;
                        literalName = token.getValue();
                    } else {
                        literalName = token.getValue();
                    }
                }
                case CONNECTIVE -> {
                    if (literalName != null) {
                        literals.add(getLiteral(literalName, negated));
                        negated = false;
                        literalName = null;
                    }
                }
            }
        }

        if (literalName != null) literals.add(getLiteral(literalName, negated));

        return new PropositionalClause(literals);
    }

    private static void validateClausePostfixTokens(List<Token> postfix) throws ParseException {
        ParseException invalidClause = new ParseException("invalid clause", -1);

        if (postfix.size() == 1 && postfix.get(0).getType() != TokenType.WORD) throw invalidClause;
        if (postfix.size() == 2 && (postfix.get(0).getType() != TokenType.WORD ||
                postfix.get(1).getType() != TokenType.NEGATION)) throw invalidClause;

        int literalCount = 0;
        int connectiveCount = 0;

        if (postfix.get(0).getType() == TokenType.NEGATION) throw invalidClause;
        if (postfix.get(0).getType() == TokenType.CONNECTIVE) throw invalidClause;
        if (postfix.get(0).getType() == TokenType.WORD) literalCount++;

        for (int i = 1; i < postfix.size(); i++) {
            if (postfix.get(i).getType() == TokenType.CONNECTIVE) connectiveCount++;
            if (postfix.get(i).getType() == TokenType.WORD) literalCount++;

            Token previous = postfix.get(i - 1);
            Token current = postfix.get(i);
            if (current.getType() == TokenType.NEGATION &&
                    previous.getType() == TokenType.CONNECTIVE) throw invalidClause;
        }

        if (literalCount - connectiveCount != 1) throw invalidClause;
    }

    private static Literal getLiteral(String literalName, boolean negated) {
        if (literalName.equalsIgnoreCase("true")) return negated ? Literal.FALSE : Literal.TRUE;
        if (literalName.equalsIgnoreCase("false")) return negated ? Literal.TRUE : Literal.FALSE;
        return new Literal(literalName, negated);
    }
}

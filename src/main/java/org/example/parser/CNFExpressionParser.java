package org.example.parser;

import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.Literal;
import org.example.parser.supplementary.Token;
import org.example.parser.supplementary.TokenType;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author aram.azatyan | 3/1/2024 4:06 PM
 */
public class CNFExpressionParser extends LogicalExpressionParser {

    public static Clause parseClauseExpression(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);
        if (expression.contains(SentenceUtils.IMPLICATION) || expression.contains(SentenceUtils.BICONDITIONAL) ||
            expression.contains(SentenceUtils.AND)) throw new ParseException("not a cnf sentence", -1);
        return getClauseFromPostfix(getPostfixTokens(tokenize(expression, true)));
    }

    public static CNFSentence parseCNFExpression(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);
        if (expression.contains(SentenceUtils.IMPLICATION) || expression.contains(SentenceUtils.BICONDITIONAL)) throw
                new ParseException("not a cnf sentence", -1);
        return getCNFFromPostfix(getPostfixTokens(tokenize(expression, true)));
    }

    private static CNFSentence getCNFFromPostfix(List<Token> postfix) throws ParseException {
        ParseException invalidCNF = new ParseException("invalid cnf", -1);

        Token andToken = new Token(TokenType.CONNECTIVE, SentenceUtils.AND);
        Token orToken = new Token(TokenType.CONNECTIVE, SentenceUtils.OR);
        List<Clause> reversedClauses = new ArrayList<>();

        if (!postfix.contains(andToken)) {
            validateClausePostfixTokens(postfix);
            return new CNFSentence(getClauseFromPostfix(postfix));
        }

        for (int i = 1; i < postfix.size(); i++) {
            Token previous = postfix.get(i - 1);
            Token current = postfix.get(i);
            if (current.getType() == TokenType.NEGATION &&
                    previous.getType() == TokenType.CONNECTIVE) throw invalidCNF;
        }

        int andCount = (int) postfix.stream().filter(t -> t.equals(andToken)).count();
        int index = postfix.size() - 1;
        int clauseCount = 0;

        try {
            int groupORCount = 0;
            while (index >= 0) {
                if (postfix.get(index).equals(andToken)) {
                    if (groupORCount != 0) throw invalidCNF;
                    index--;
                    continue;
                }
                if (postfix.get(index).equals(orToken)) {
                    groupORCount++;
                    index--;
                } else if (postfix.get(index).getType() == TokenType.LITERAL || postfix.get(index).getType() == TokenType.NEGATION) {
                    int literalCount = groupORCount + 1;
                    if (groupORCount == 0) {
                        Literal literal;
                        if (postfix.get(index).getType() == TokenType.LITERAL) {
                            if (postfix.get(index).getValue().equalsIgnoreCase("true")) literal = Literal.TRUE;
                            else if (postfix.get(index).getValue().equalsIgnoreCase("false")) literal = Literal.FALSE;
                            else literal = new Literal(postfix.get(index).getValue());
                            reversedClauses.add(new Clause(literal));
                            index--;
                        } else {
                            if (postfix.get(index).getType() != TokenType.NEGATION ||
                                    postfix.get(index - 1).getType() != TokenType.LITERAL) throw invalidCNF;
                            if (postfix.get(index - 1).getValue().equalsIgnoreCase("true")) literal = Literal.FALSE;
                            else if (postfix.get(index - 1).getValue().equalsIgnoreCase("false")) literal = Literal.TRUE;
                            else literal = new Literal(postfix.get(index - 1).getValue(), true);
                            reversedClauses.add(new Clause(literal));
                            index -= 2;
                        }
                        clauseCount++;
                    } else {
                        int connectiveIndex = index + groupORCount + 1;
                        while (literalCount > 0) {
                            if (postfix.get(index).getType() == TokenType.LITERAL) literalCount--;
                            if (postfix.get(index).equals(andToken) || postfix.get(index).equals(orToken)) throw invalidCNF;
                            if (literalCount > 0) index--;
                        }

                        validateClausePostfixTokens(postfix.subList(index, connectiveIndex));

                        clauseCount++;
                        reversedClauses.add(getClauseFromPostfix(postfix.subList(index, connectiveIndex)));
                        index--;
                        groupORCount = 0;
                    }
                }
            }
        } catch (Exception e) {
            throw invalidCNF;
        }


        if (clauseCount - andCount != 1) throw invalidCNF;
        Collections.reverse(reversedClauses);
        return new CNFSentence(new LinkedHashSet<>(reversedClauses));
    }

    private static Clause getClauseFromPostfix(List<Token> postfix) throws ParseException {
        validateClausePostfixTokens(postfix);

        LinkedHashSet<Literal> literals = new LinkedHashSet<>();
        boolean negated = false;
        String literalName = null;

        for (Token token : postfix) {
            switch (token.getType()) {
                case NEGATION -> negated = true;
                case LITERAL -> {
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

        return new Clause(literals);
    }

    private static void validateClausePostfixTokens(List<Token> postfix) throws ParseException {
        ParseException invalidClause = new ParseException("invalid clause", -1);

        if (postfix.size() == 1 && postfix.get(0).getType() != TokenType.LITERAL) throw invalidClause;
        if (postfix.size() == 2 && (postfix.get(0).getType() != TokenType.LITERAL ||
                postfix.get(1).getType() != TokenType.NEGATION)) throw invalidClause;

        int literalCount = 0;
        int connectiveCount = 0;

        if (postfix.get(0).getType() == TokenType.NEGATION) throw invalidClause;
        if (postfix.get(0).getType() == TokenType.CONNECTIVE) throw invalidClause;
        if (postfix.get(0).getType() == TokenType.LITERAL) literalCount++;

        for (int i = 1; i < postfix.size(); i++) {
            if (postfix.get(i).getType() == TokenType.CONNECTIVE) connectiveCount++;
            if (postfix.get(i).getType() == TokenType.LITERAL) literalCount++;

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

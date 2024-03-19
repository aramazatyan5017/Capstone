package org.example.parser;

import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.Literal;
import org.example.parser.supplementary.Token;
import org.example.parser.supplementary.TokenType;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author aram.azatyan | 3/1/2024 4:06 PM
 */
public class CNFExpressionParser extends LogicalExpressionParser {

    public static Clause parseClauseExpression(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);
        return getClause(tokenize(expression, true));
    }

    public static CNFSentence parseCNFExpression(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);
        return new CNFSentence(getClauses(preprocessExpression(expression)));
    }

    private static LinkedHashSet<Clause> getClauses(String expression) throws ParseException {
        if (expression.contains(SentenceUtils.IMPLICATION) || expression.contains(SentenceUtils.BICONDITIONAL)) throw
            new ParseException("not a cnf sentence", -1);

        Pattern pattern = Pattern.compile(SentenceUtils.AND + "{2,}");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find()) throw new ParseException("consecutive " + SentenceUtils.AND + " symbols", -1);

        LinkedHashSet<Clause> clauses = new LinkedHashSet<>();
        String[] possClauses = expression.split(SentenceUtils.AND);
        for (String possClause : possClauses) {
            try {
                clauses.add(getClause(tokenize(possClause, false)));
            } catch (Exception ex) {
                throw new ParseException("invalid sentence", -1);
            }
        }
        return clauses;
    }

    private static Clause getClause(List<Token> infixTokens) throws ParseException {
        for (Token token : infixTokens) {
            if (token.getType() == TokenType.CONNECTIVE) {
                if (token.getValue().equals(SentenceUtils.AND) || token.getValue().equals(SentenceUtils.IMPLICATION) ||
                        token.getValue().equals(SentenceUtils.BICONDITIONAL)) throw new ParseException("not a clause", -1);
            }
        }

        List<Token> postfixTokens = getPostfixTokens(infixTokens);
        validateClausePostfixTokens(postfixTokens);

        LinkedHashSet<Literal> literals = new LinkedHashSet<>();
        boolean negated = false;
        String literalName = null;

        for (Token token : postfixTokens) {
            switch (token.getType()) {
                case NEGATION -> negated = !negated;
                case LITERAL -> {
                    if (literalName != null) {
                        literals.add(new Literal(literalName, negated));
                        negated = false;
                        literalName = token.getValue();
                    } else {
                        literalName = token.getValue();
                    }
                }
                case CONNECTIVE -> {
                    if (literalName != null) {
                        literals.add(new Literal(literalName, negated));
                        negated = false;
                        literalName = null;
                    }
                }
            }
        }

        if (literalName != null) literals.add(new Literal(literalName, negated));

        return new Clause(literals);
    }

    private static void validateClausePostfixTokens(List<Token> postfixTokens) throws ParseException {
        ParseException invalidClause = new ParseException("invalid clause", -1);

        if (postfixTokens.size() == 1 && postfixTokens.get(0).getType() != TokenType.LITERAL) throw invalidClause;
        if (postfixTokens.size() == 2 && (postfixTokens.get(0).getType() != TokenType.LITERAL ||
                postfixTokens.get(1).getType() != TokenType.NEGATION)) throw invalidClause;

        for (int i = 1; i < postfixTokens.size(); i++) {
            Token previous = postfixTokens.get(i - 1);
            Token current = postfixTokens.get(i);
            if (current.getType() == TokenType.NEGATION &&
                    previous.getType() == TokenType.CONNECTIVE) throw invalidClause;
        }
    }

}

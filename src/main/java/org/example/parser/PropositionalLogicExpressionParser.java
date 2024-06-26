package org.example.parser;

import org.example.domain.Connective;
import org.example.parser.supplementary.Token;
import org.example.util.SentenceUtils;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.parser.supplementary.TokenType.*;

/**
 * @author aram.azatyan | 4/17/2024 1:43 PM
 */
abstract public class PropositionalLogicExpressionParser {

    private record MatcherIndices(int start, int end) {}

    protected static List<Token> infixTokens(String expression) throws ParseException {
        expression = preprocessExpression(expression);
        var tokenList = new ArrayList<Token>();
        var literalStringBuilder = new StringBuilder();
        for (int i = 0; i < expression.length();) {
            if (expression.charAt(i) == SentenceUtils.OPENING_PARENTHESES) {
                checkForLiteral(literalStringBuilder, tokenList);
                tokenList.add(new Token(OPENING_PARENTHESES));
            } else if (expression.charAt(i) == SentenceUtils.CLOSING_PARENTHESES) {
                checkForLiteral(literalStringBuilder, tokenList);
                tokenList.add(new Token(CLOSING_PARENTHESES));
            } else if (Connective.getConnectiveSymbols().contains(String.valueOf(expression.charAt(i))) ||
                    isReachedImplOrBicond(i, expression)) {
                checkForLiteral(literalStringBuilder, tokenList);
                if (isReachedImplOrBicond(i, expression)) {
                    if (expression.charAt(i) == '=') {
                        i += 2;
                        tokenList.add(new Token(CONNECTIVE, SentenceUtils.IMPLICATION));
                    } else {
                        i += 3;
                        tokenList.add(new Token(CONNECTIVE, SentenceUtils.BICONDITIONAL));
                    }
                    continue;
                } else {
                    tokenList.add(new Token(CONNECTIVE, String.valueOf(expression.charAt(i))));
                }
            } else if (String.valueOf(expression.charAt(i)).equals(SentenceUtils.NOT)) {
                checkForLiteral(literalStringBuilder, tokenList);
                tokenList.add(new Token(NEGATION));
            } else {
                literalStringBuilder.append(expression.charAt(i));
            }
            i++;
        }
        checkForLiteral(literalStringBuilder, tokenList);
        return tokenList;
    }

    private static void checkForLiteral(StringBuilder literalStringBuilder, List<Token> tokenList) {
        if (!literalStringBuilder.isEmpty()) {
            String val;
            String literalStr = literalStringBuilder.toString();

            if (literalStr.equalsIgnoreCase("true")) val = "TRUE";
            else if (literalStr.equalsIgnoreCase("false")) val = "FALSE";
            else val = literalStr;

            tokenList.add(new Token(WORD, val));
            literalStringBuilder.setLength(0);
        }
    }

    private static boolean isReachedImplOrBicond(int currentIndex, String expression) {
        if (expression.charAt(currentIndex) == '=') {
            return expression.charAt(currentIndex + 1) == '>';
        } else if (expression.charAt(currentIndex) == '<') {
            return expression.charAt(currentIndex + 1) == '=' && expression.charAt(currentIndex + 2) == '>';
        }
        return false;
    }

    private static String preprocessExpression(String expression) throws ParseException {
        validateParentheses(expression);
        var inputStr = new StringBuilder(expression.replaceAll("\\s+", ""));
        Pattern pattern = Pattern.compile(SentenceUtils.NOT + "+");
        Matcher matcher = pattern.matcher(inputStr.toString());
        Deque<MatcherIndices> stack = new ArrayDeque<>();
        while (matcher.find()) stack.push(new MatcherIndices(matcher.start(), matcher.end()));
        stack.forEach(i -> inputStr.replace(i.start(), i.end(),
                (i.end() - i.start()) % 2 == 1 ? SentenceUtils.NOT : ""));
        return inputStr.toString();
    }

    private static void validateParentheses(String expression) throws ParseException {
        Deque<Character> stack = new ArrayDeque<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == SentenceUtils.OPENING_PARENTHESES) {
                stack.push(c);
            } else if (c == SentenceUtils.CLOSING_PARENTHESES) {
                if (stack.isEmpty()) throw new ParseException("invalid parentheses", i);
                stack.pop();
            }
        }

        if (!stack.isEmpty()) throw new ParseException("invalid parentheses", -1);
    }

    protected static List<Token> postfixTokens(List<Token> infixTokens) throws ParseException {
        validateInfixTokenList(infixTokens);
        Deque<Token> stack = new ArrayDeque<>();
        LinkedList<Token> postfix = new LinkedList<>();
        for (Token token : infixTokens) {
            switch (token.getType()) {
                case WORD -> postfix.offer(token);
                case OPENING_PARENTHESES, NEGATION -> stack.push(token);
                case CLOSING_PARENTHESES -> {
                    var stackTop = stack.pop();
                    while (stackTop.getType() != OPENING_PARENTHESES) {
                        postfix.offer(stackTop);
                        stackTop = stack.pop();
                    }
                }
                case CONNECTIVE -> {
                    var currentOperator = Connective.fromValue(token.getValue());
                    var stackTop = stack.peek();
                    while ((!stack.isEmpty()) &&
                            ((stackTop.getType() == CONNECTIVE
                                    && Connective.fromValue(stackTop.getValue()).getPrecedence() >
                                    currentOperator.getPrecedence()) || (stackTop.getType() == NEGATION))) {
                        postfix.offer(stack.pop());
                        stackTop = stack.peek();
                    }
                    stack.push(token);
                }
            }
        }
        while (!stack.isEmpty()) postfix.offer(stack.pop());

        optimizeNegationsInPostfix(postfix);
        return postfix;
    }

    private static void validateInfixTokenList(List<Token> tokens) throws ParseException {
        if (tokens == null || tokens.isEmpty()) throw
                new ParseException("invalid expression", -1);
        if (tokens.get(0).getType() == CONNECTIVE || tokens.get(0).getType() == CLOSING_PARENTHESES ||
                tokens.get(tokens.size() - 1).getType() == CONNECTIVE || tokens.get(tokens.size() - 1).getType() == NEGATION ||
                tokens.get(tokens.size() - 1).getType() == OPENING_PARENTHESES) throw
                new ParseException("invalid expression, wrong token position", -1);

        ParseException possExc = new ParseException("invalid expression, unable to tokenize", -1);

        Token previous = tokens.get(0);
        for (int i = 1; i < tokens.size(); i++) {
            Token next = tokens.get(i);
            switch (previous.getType()) {
                case NEGATION, CONNECTIVE -> {
                    if (next.getType() == CONNECTIVE || next.getType() == CLOSING_PARENTHESES) throw possExc;
                }
                case WORD -> {
                    if (next.getType() == OPENING_PARENTHESES || next.getType() == NEGATION) throw possExc;
                }
                case OPENING_PARENTHESES -> {
                    if (next.getType() == CONNECTIVE) throw possExc;
                }
                case CLOSING_PARENTHESES -> {
                    if (next.getType() == WORD || next.getType() == OPENING_PARENTHESES ||
                            next.getType() == NEGATION) throw possExc;
                }
            }
            previous = next;
        }
    }

    private static void optimizeNegationsInPostfix(List<Token> postfix) {
        for (int i = 0; i < postfix.size();) {
            if (postfix.get(i).getType() == NEGATION) {
                int start = i;
                while (i < postfix.size() && postfix.get(i).getType() == NEGATION) i++;
                int end = i;
                postfix.subList(start, end).clear();
                if ((end - start) % 2 == 1) postfix.add(start, new Token(NEGATION));
            } else {
                i++;
            }
        }
    }
}

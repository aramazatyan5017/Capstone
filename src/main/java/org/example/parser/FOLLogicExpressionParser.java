package org.example.parser;

import org.example.domain.Connective;
import org.example.domain.supplementary.PostfixAndFuncArgCountMap;
import org.example.parser.supplementary.Token;
import org.example.parser.supplementary.TokenType;
import org.example.domain.sentence.fol.term.TermType;
import org.example.util.SentenceUtils;

import java.text.ParseException;
import java.util.*;

import static org.example.parser.supplementary.TokenType.*;

/**
 * @author aram.azatyan | 4/17/2024 2:02 PM
 */
public abstract class FOLLogicExpressionParser {
    protected static PostfixAndFuncArgCountMap postfixTokens(List<Token> infixTokens) throws ParseException {
        Map<String, Integer> argumentCountMap = new HashMap<>();
        validateFixedInfixTokens(infixTokens);
        Deque<Token> stack = new ArrayDeque<>();
        LinkedList<Token> queue = new LinkedList<>();

        Deque<Boolean> wereValuesStack = new ArrayDeque<>();
        Deque<Integer> argCountStack = new ArrayDeque<>();

        for (Token token : infixTokens) {
            switch (token.getType()) {
                case VARIABLE, CONSTANT -> {
                    queue.offer(token);
                    if (!wereValuesStack.isEmpty()) {
                        wereValuesStack.pop();
                        wereValuesStack.push(true);
                    }
                }
                case PREDICATE, FUNCTION -> {
                    stack.push(token);
                    argCountStack.push(0);
                    if (!wereValuesStack.isEmpty()) {
                        wereValuesStack.pop();
                        wereValuesStack.push(true);
                    }
                    wereValuesStack.push(false);
                }
                case CONNECTIVE, NEGATION -> {
                    if (!stack.isEmpty() && (stack.peek().getType() == CONNECTIVE || stack.peek().getType() == NEGATION)) {
                        Token stackTop = stack.peek();
                        int stackTopPrecedence = stackTop.getType() == NEGATION
                                ? Integer.MAX_VALUE
                                : Connective.fromValue(stackTop.getValue()).getPrecedence();
                        int currentOperatorPrecedence = token.getType() == NEGATION
                                ? Integer.MAX_VALUE
                                : Connective.fromValue(token.getValue()).getPrecedence();

                        while ((!stack.isEmpty() && stackTop.getType() != OPENING_PARENTHESES) &&
                                (stackTopPrecedence > currentOperatorPrecedence ||
                                        (stackTopPrecedence == currentOperatorPrecedence && token.getType() != NEGATION))) {
                            queue.offer(stack.pop());
                            stackTop = stack.peek();
                        }
                    }
                    stack.push(token);
                }
                case COMMA -> {
                    Token stackTop = stack.peek();
                    while (!stack.isEmpty() && stackTop.getType() != OPENING_PARENTHESES) {
                        queue.offer(stack.pop());
                        stackTop = stack.peek();
                    }
                    boolean wasValue = wereValuesStack.pop();
                    if (wasValue) {
                        int count = argCountStack.pop();
                        argCountStack.push(++count);
                    }
                    wereValuesStack.push(false);
                }
                case OPENING_PARENTHESES -> stack.push(token);
                case CLOSING_PARENTHESES -> {
                    if (stack.isEmpty()) throw new ParseException("invalid expression, unable to tokenize", -1);
                    while (stack.peek().getType() != OPENING_PARENTHESES) {
                        queue.offer(stack.pop());
                    }
                    if (stack.isEmpty() || stack.peek().getType() != OPENING_PARENTHESES)
                        throw new ParseException("invalid expression, unable to tokenize", -1);
                    stack.pop();
                    if (!stack.isEmpty() && (stack.peek().getType() == PREDICATE || stack.peek().getType() == FUNCTION)) {
                        Token f = stack.pop();
                        int count = argCountStack.pop();
                        boolean wasValue = wereValuesStack.pop();
                        if (wasValue) {
                            ++count;
                        }

                        if (argumentCountMap.containsKey(f.getValue()))
                            throw new ParseException("invalid expression, unable to tokenize", -1);

                        argumentCountMap.put(f.getValue(), count);
                        queue.offer(f);
                    }
                }
            }
        }
        while (!stack.isEmpty()) {
            if (stack.peek().getType() == OPENING_PARENTHESES) throw new ParseException("invalid expression, unable to tokenize", -1);
            queue.offer(stack.pop());
        }

        optimizeNegationsInPostfix(queue);
        validatePostfixTokens(queue);

        return new PostfixAndFuncArgCountMap(queue, argumentCountMap);
    }

    private static void validateFixedInfixTokens(List<Token> tokens) throws ParseException {
        ParseException possExc = new ParseException("invalid expression, unable to tokenize", -1);

        Token previous = tokens.get(0);
        for (int i = 1; i < tokens.size(); i++) {
            Token next = tokens.get(i);
            switch (previous.getType()) {
                case WORD -> {if (isNotOneOfThese(next.getType(), OPENING_PARENTHESES, CLOSING_PARENTHESES, COMMA)) throw possExc;}
                case CONNECTIVE -> {if (isNotOneOfThese(next.getType(), NEGATION, PREDICATE, OPENING_PARENTHESES)) throw possExc;}
                case OPENING_PARENTHESES -> {if (isOneOfThese(next.getType(), CONNECTIVE, COMMA)) throw possExc;}
                case CLOSING_PARENTHESES -> {if (isNotOneOfThese(next.getType(), CONNECTIVE, COMMA, CLOSING_PARENTHESES)) throw possExc;}
                case NEGATION -> {if (isNotOneOfThese(next.getType(), PREDICATE, OPENING_PARENTHESES, NEGATION)) throw possExc;}
                case CONSTANT, VARIABLE -> {if (isNotOneOfThese(next.getType(), CLOSING_PARENTHESES, COMMA)) throw possExc;}
                case FUNCTION, PREDICATE -> {if (isNotOneOfThese(next.getType(), OPENING_PARENTHESES)) throw possExc;}
                case COMMA -> {if (isNotOneOfThese(next.getType(), CONSTANT, VARIABLE, FUNCTION, OPENING_PARENTHESES)) throw possExc;}
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

    private static void validatePostfixTokens(List<Token> postfixTokens) throws ParseException {
        Token previous = postfixTokens.get(0);
        for (int i = 1; i < postfixTokens.size(); i++) {
            Token current = postfixTokens.get(i);
            if (current.getType() == NEGATION && isNotOneOfThese(previous.getType(), CONNECTIVE, PREDICATE))
                throw new ParseException("invalid expression, unable to tokenize", -1);
            previous = current;
        }
    }

    protected static List<Token> infixTokens(String expression) throws ParseException {
        expression = preprocessExpression(expression);
        List<Token> tokenList = new ArrayList<>();
        StringBuilder literalStringBuilder = new StringBuilder();
        for (int i = 0; i < expression.length();) {
            if (expression.charAt(i) == SentenceUtils.OPENING_PARENTHESES) {
                checkForWord(literalStringBuilder, tokenList);
                tokenList.add(new Token(OPENING_PARENTHESES));
            } else if (expression.charAt(i) == SentenceUtils.CLOSING_PARENTHESES) {
                checkForWord(literalStringBuilder, tokenList);
                tokenList.add(new Token(CLOSING_PARENTHESES));
            } else if (Connective.getConnectiveSymbols().contains(String.valueOf(expression.charAt(i))) ||
                    isReachedImplOrBicond(i, expression)) {
                checkForWord(literalStringBuilder, tokenList);
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
                checkForWord(literalStringBuilder, tokenList);
                tokenList.add(new Token(NEGATION));
            } else if (expression.charAt(i) == SentenceUtils.COMMA) {
                checkForWord(literalStringBuilder, tokenList);
                tokenList.add(new Token(COMMA));
            } else {
                literalStringBuilder.append(expression.charAt(i));
            }
            i++;
        }
        checkForWord(literalStringBuilder, tokenList);
        validateRawInfixTokens(tokenList);
        fixInfix(tokenList);
        return tokenList;
    }

    private static String preprocessExpression(String expression) throws ParseException {
        validateParentheses(expression);
        return expression.replaceAll("\\s+", "");
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

    private static void checkForWord(StringBuilder literalStringBuilder, List<Token> tokenList) {
        if (!literalStringBuilder.isEmpty()) {
            String literalStr = literalStringBuilder.toString();

//            if (literalStr.equalsIgnoreCase("true")) val = "TRUE";
//            else if (literalStr.equalsIgnoreCase("false")) val = "FALSE";

            tokenList.add(new Token(WORD, literalStr));
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

    private static void validateRawInfixTokens(List<Token> tokens) throws ParseException {
        if (tokens == null || tokens.isEmpty()) throw new ParseException("invalid expression", -1);
        if (tokens.get(0).getType() == CONNECTIVE || tokens.get(0).getType() == CLOSING_PARENTHESES ||
                tokens.get(tokens.size() - 1).getType() == CONNECTIVE || tokens.get(tokens.size() - 1).getType() == NEGATION ||
                tokens.get(tokens.size() - 1).getType() == OPENING_PARENTHESES) throw
                new ParseException("invalid expression, wrong token position", -1);

        ParseException possExc = new ParseException("invalid expression, unable to tokenize", -1);

        Token previous = tokens.get(0);
        for (int i = 1; i < tokens.size(); i++) {
            Token next = tokens.get(i);
            switch (previous.getType()) {
                case WORD -> {if (isNotOneOfThese(next.getType(), OPENING_PARENTHESES, CLOSING_PARENTHESES, COMMA)) throw possExc;}
                case CONNECTIVE -> {if (isNotOneOfThese(next.getType(), NEGATION, WORD, OPENING_PARENTHESES)) throw possExc;}
                case OPENING_PARENTHESES -> {if (isOneOfThese(next.getType(), CONNECTIVE, COMMA)) throw possExc;}
                case CLOSING_PARENTHESES -> {if (isNotOneOfThese(next.getType(), CONNECTIVE, COMMA, CLOSING_PARENTHESES)) throw possExc;}
                case NEGATION -> {if (isNotOneOfThese(next.getType(), WORD, OPENING_PARENTHESES, NEGATION)) throw possExc;}
                case COMMA -> {if (isNotOneOfThese(next.getType(), WORD, OPENING_PARENTHESES)) throw possExc;}
            }
            previous = next;
        }
    }

    private static void fixInfix(List<Token> tokens) throws ParseException {
        boolean isInsidePredicate = false;
        int predicateBracketCount = 0;
        ParseException exception = new ParseException("invalid FOL sentence", -1);

        Token previous = tokens.get(0);
        if (previous.getType() == WORD) previous.setType(PREDICATE);
        for (int i = 1; i < tokens.size(); i++) {
            Token current = tokens.get(i);
            if (current.getType() == WORD) {
                if (i + 1 == tokens.size()) throw exception;

                if (previous.getType() == NEGATION) {
                    if (isInsidePredicate || tokens.get(i + 1).getType() != OPENING_PARENTHESES) throw exception;
                    current.setType(PREDICATE);
                } else if (previous.getType() == COMMA || previous.getType() == OPENING_PARENTHESES) {
//                    if (!isInsidePredicate) throw exception;
                    current.setType(tokens.get(i + 1).getType() == OPENING_PARENTHESES
                            ? (isInsidePredicate ? FUNCTION : PREDICATE)
                            : (determineConstOrVar(current.getValue()) == TermType.CONSTANT
                                ? CONSTANT
                                : VARIABLE));
                } else if (previous.getType() == CONNECTIVE) {
                    if (isInsidePredicate || tokens.get(i + 1).getType() != OPENING_PARENTHESES) throw exception;
                    current.setType(PREDICATE);
                } else {
                    throw exception;
                }
            } else if (current.getType() == OPENING_PARENTHESES) {
                if (previous.getType() == PREDICATE || predicateBracketCount != 0) {
                    isInsidePredicate = true;
                    predicateBracketCount++;
                }
            } else if (current.getType() == CLOSING_PARENTHESES) {
                if (predicateBracketCount > 0) predicateBracketCount--;
                if (predicateBracketCount == 0) isInsidePredicate = false;
            }
            previous = current;
        }

        validateFixedInfixTokens(tokens);
    }

    private static boolean isNotOneOfThese(TokenType currentType, TokenType... types) {
        for (TokenType type : types) {
            if (currentType == type) return false;
        }
        return true;
    }

    private static boolean isOneOfThese(TokenType currentType, TokenType... types) {
        for (TokenType type : types) {
            if (currentType != type) return false;
        }
        return true;
    }

    private static TermType determineConstOrVar(String value) throws ParseException {
        if (value.equals(value.toUpperCase())) return TermType.CONSTANT;
        if (value.equals(value.toLowerCase())) return TermType.VARIABLE;
        throw new ParseException("unable to determine whether constant or variable", -1);
    }
}

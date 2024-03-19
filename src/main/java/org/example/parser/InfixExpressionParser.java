package org.example.parser;

import org.example.domain.*;
import org.example.domain.sentence.GenericComplexSentence;
import org.example.domain.sentence.Literal;
import org.example.domain.sentence.Sentence;
import org.example.domain.supplementary.ConnectiveAndNegation;
import org.example.domain.supplementary.LiteralAndNegation;
import org.example.domain.supplementary.Node;
import org.example.parser.supplementary.Token;
import org.example.util.Utils;

import static org.example.parser.supplementary.TokenType.*;

import java.text.ParseException;
import java.util.*;

/**
 * @author aram.azatyan | 2/22/2024 11:45 PM
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class InfixExpressionParser extends LogicalExpressionParser {

    public static Sentence parse(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);
        Literal literal = possibleLiteral(expression);
        if (literal != null) return literal;
        return treeToSentence(postfixToTree(getPostfixTokens(tokenize(expression, true))));
    }

    private static Sentence treeToSentence(Node root) {
        if (root.isExternal()) {
            LiteralAndNegation literalAndNegation = (LiteralAndNegation) root.getValue();
            return new Literal(literalAndNegation.getLiteral(), literalAndNegation.isNegated());
        }
        var connAndNeg = (ConnectiveAndNegation) root.getValue();
        return new GenericComplexSentence(treeToSentence(root.getLeft()), treeToSentence(root.getRight()),
                connAndNeg.getConnective(), connAndNeg.isNegated());
    }

    private static Node postfixToTree(List<Token> postfix) {
        Deque<Node> stack = new ArrayDeque<>();
        for (Token token : postfix) {
            if (token.getType() == LITERAL) {
                stack.push(new Node(new LiteralAndNegation(token.getValue(), false)));
            } else if (token.getType() == NEGATION) {
                Node topNode = stack.peek();
                assert topNode != null;
                if (topNode.isExternal()) {
                    LiteralAndNegation literalAndNegation = (LiteralAndNegation) topNode.getValue();
                    literalAndNegation.setNegated(!literalAndNegation.isNegated());
                } else {
                    ConnectiveAndNegation connectiveAndNegation = (ConnectiveAndNegation) topNode.getValue();
                    connectiveAndNegation.setNegated(!connectiveAndNegation.isNegated());
                }
            } else {
                Node c1 = stack.pop();
                Node c2 = stack.pop();
                Node operatorNode = new Node(new ConnectiveAndNegation(Connective.fromValue(token.getValue()),
                        false), null, c2, c1);
                c1.setParent(operatorNode);
                c2.setParent(operatorNode);
                stack.push(operatorNode);
            }
        }

        return stack.pop();
    }

    private static Literal possibleLiteral(String expression) throws ParseException {
        for (String connectiveSymbol : Connective.getConnectiveSymbols()) {
            if (expression.contains(connectiveSymbol)) return null;
        }
        expression = expression.replaceAll("\\s+", "");
        List<Token> tokens = getPostfixTokens(tokenize(expression, true));

        if (tokens.get(0).getType() != LITERAL) return null;
        return new Literal("random name");
    }
}

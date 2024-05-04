package org.example.parser;

import org.example.domain.*;
import org.example.domain.sentence.GenericComplexSentence;
import org.example.domain.sentence.Literal;
import org.example.domain.sentence.Sentence;
import org.example.domain.supplementary.ConnectiveAndNegation;
import org.example.domain.supplementary.LiteralNameAndNegation;
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
public class InfixExpressionParser extends PropositionalLogicExpressionParser {

    public static GenericComplexSentence parseGeneric(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);

        Node topNode = postfixToTree(postfixTokens(infixTokens(expression)));
        if (topNode.isExternal()) throw new ParseException("unable to construct a generic sentence", -1);

        return (GenericComplexSentence) treeToSentence(topNode);
    }

    public static Literal parseLiteral(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);
        return possibleLiteral(expression);
    }

    private static Sentence treeToSentence(Node root) {
        if (root.isExternal()) return getLiteral((LiteralNameAndNegation) root.getValue());
        var connectiveAndNegation = (ConnectiveAndNegation) root.getValue();
        return new GenericComplexSentence(treeToSentence(root.getLeft()), treeToSentence(root.getRight()),
                connectiveAndNegation.getConnective(), connectiveAndNegation.isNegated());
    }

    private static Node postfixToTree(List<Token> postfix) {
        Deque<Node> stack = new ArrayDeque<>();
        for (Token token : postfix) {
            if (token.getType() == WORD) {
                stack.push(new Node(new LiteralNameAndNegation(token.getValue(), false)));
            } else if (token.getType() == NEGATION) {
                Node topNode = stack.peek();
                assert topNode != null;
                if (topNode.isExternal()) {
                    LiteralNameAndNegation literalNameAndNegation = (LiteralNameAndNegation) topNode.getValue();
                    literalNameAndNegation.setNegated(!literalNameAndNegation.isNegated());
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
            if (expression.contains(connectiveSymbol)) throw
                    new ParseException("literal should not contain any connectives", -1);
        }

        List<Token> tokens = postfixTokens(infixTokens(expression));

        if (tokens.get(0).getType() != WORD) throw new ParseException("unable to construct a literal", -1);

        if (tokens.get(0).getValue().equalsIgnoreCase("true")) {
            return (tokens.size() - 1) % 2 == 0 ? Literal.TRUE : Literal.FALSE;
        }
        if (tokens.get(0).getValue().equalsIgnoreCase("false")) {
            return (tokens.size() - 1) % 2 == 0 ? Literal.FALSE : Literal.TRUE;
        }

        return new Literal(tokens.get(0).getValue(), (tokens.size() - 1) % 2 != 0);
    }

    private static Literal getLiteral(LiteralNameAndNegation literalNameAndNegation) {
        if (literalNameAndNegation.getLiteralName().equalsIgnoreCase("true")) {
            return literalNameAndNegation.isNegated() ? Literal.FALSE : Literal.TRUE;
        }
        if (literalNameAndNegation.getLiteralName().equalsIgnoreCase("false")) {
            return literalNameAndNegation.isNegated() ? Literal.TRUE : Literal.FALSE;
        }
        return new Literal(literalNameAndNegation.getLiteralName(), literalNameAndNegation.isNegated());
    }
}

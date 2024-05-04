package org.example.parser;

import org.example.domain.Connective;
import org.example.domain.supplementary.ConnectiveAndNegation;
import org.example.domain.supplementary.Node;
import org.example.domain.supplementary.PostfixAndFuncArgCountMap;
import org.example.parser.supplementary.Token;
import org.example.temp_fol.*;
import org.example.util.Utils;

import java.text.ParseException;
import java.util.*;

import static org.example.parser.supplementary.TokenType.*;

/**
 * @author aram.azatyan | 4/17/2024 2:04 PM
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class FOLInfixExpressionParser extends FOLLogicExpressionParser {

    public static Predicate parsePredicate(String expression) throws ParseException {
        Object val = parsePredicateOrFunction(expression, "predicate");
        if (!(val instanceof Predicate)) throw new ParseException("unable to construct a predicate", -1);
        return (Predicate) val;
    }

    public static Function parseFunction(String expression) throws ParseException {
        Object val = parsePredicateOrFunction(expression, "function");
        if (!(val instanceof Predicate predicate) || predicate.isNegated())
            throw new ParseException("unable to construct a function", -1);

        return new Function(predicate.getName(), predicate.getTerms());
    }

    public static GenericComplexFOLSentence parseGeneric(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);

        Node topNode = postfixToTree(postfixTokens(infixTokens(expression)));
        if (topNode.isExternal()) throw new ParseException("unable to construct a generic sentence", -1);

        return (GenericComplexFOLSentence) treeToFOLSentence(topNode);
    }

    private static Object parsePredicateOrFunction(String expression, String text) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new ParseException("null param", -1);

        for (String connectiveSymbol : Connective.getConnectiveSymbols()) {
            if (expression.contains(connectiveSymbol))
                throw new ParseException(text + " should not contain any connectives", -1);
        }

        Node topNode = postfixToTree(postfixTokens(infixTokens(expression)));
        if (!topNode.isExternal())
            throw new ParseException("unable to construct a " + text, -1);

        return topNode.getValue();
    }

    private static FOLSentence treeToFOLSentence(Node root) {
        if (root.isExternal()) return (Predicate) root.getValue();
        ConnectiveAndNegation connectiveAndNegation = (ConnectiveAndNegation) root.getValue();
        return new GenericComplexFOLSentence(treeToFOLSentence(root.getLeft()), treeToFOLSentence(root.getRight()),
                connectiveAndNegation.getConnective(), connectiveAndNegation.isNegated());
    }

    private static Node postfixToTree(PostfixAndFuncArgCountMap postfixAndMap) {
        List<Token> postfix = postfixAndMap.postfix();
        Map<String, Integer> argCountMap = postfixAndMap.argCountMap();

        Deque<Node> stack = new ArrayDeque<>();
        for (int i = 0; i < postfix.size(); i++) {
            Token token = postfix.get(i);

            switch (token.getType()) {
                case CONSTANT, VARIABLE -> {
                    stack.push(new Node(token.getType() == CONSTANT
                            ? new Constant(token.getValue())
                            : new Variable(token.getValue())));
                }
                case FUNCTION, PREDICATE -> {
                    LinkedHashSet<Term> terms = new LinkedHashSet<>();

                    int argCount = argCountMap.get(token.getValue());

                    while (!stack.isEmpty() && argCount > 0) {
                        terms.add((Term) stack.pop().getValue());
                        --argCount;
                    }

                    List<Term> termList = new ArrayList<>(terms);
                    Collections.reverse(termList);
                    terms = new LinkedHashSet<>(termList);

                    boolean negated = i + 1 < postfix.size() && postfix.get(i + 1).getType() == NEGATION;

                    stack.push(new Node(token.getType() == FUNCTION
                            ? new Function(token.getValue(), terms)
                            : new Predicate(token.getValue(), negated, terms)));

                    if (negated) ++i;
                }
                case CONNECTIVE -> {
                    Node n1 = stack.pop();
                    Node n2 = stack.pop();

                    boolean negated = i + 1 < postfix.size() && postfix.get(i + 1).getType() == NEGATION;

                    Node operatorNode = new Node(new ConnectiveAndNegation(Connective.fromValue(token.getValue()),
                            negated), null, n2, n1);
                    n1.setParent(operatorNode);
                    n2.setParent(operatorNode);
                    stack.push(operatorNode);

                    if (negated) ++i;
                }
            }
        }

        return stack.pop();
    }

    public static void main(String[] args) throws Exception {
        postfixTokens(infixTokens("(!(!(!Missile(x))) | !!!Sells(x, Wow(x, y)) | Kuku(a, b))")).postfix().forEach(System.out::print);
        System.out.println();
    }
}
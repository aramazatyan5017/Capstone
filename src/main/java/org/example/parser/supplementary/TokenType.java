package org.example.parser.supplementary;

import org.example.util.SentenceUtils;


import java.util.List;

import static org.example.util.SentenceUtils.AND;
import static org.example.util.SentenceUtils.OR;
import static org.example.util.SentenceUtils.IMPLICATION;
import static org.example.util.SentenceUtils.BICONDITIONAL;

/**
 * @author aram.azatyan | 2/22/2024 2:49 PM
 */
public enum TokenType {
    WORD(null),
    CONNECTIVE(new String[] {AND, OR, IMPLICATION, BICONDITIONAL}),
    OPENING_PARENTHESES(new String[] {String.valueOf(SentenceUtils.OPENING_PARENTHESES)}),
    CLOSING_PARENTHESES(new String[] {String.valueOf(SentenceUtils.CLOSING_PARENTHESES)}),
    NEGATION(new String[] {SentenceUtils.NOT}),
    CONSTANT(null),
    VARIABLE(null),
    FUNCTION(null),
    PREDICATE(null),
    COMMA(new String[] {String.valueOf(SentenceUtils.COMMA)});

    private final String[] permissibleValues;

    TokenType(String[] permissibleValues) {
        this.permissibleValues = permissibleValues;
    }

    public String[] getPermissibleValues() {
        return permissibleValues;
    }
}

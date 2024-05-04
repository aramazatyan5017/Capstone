package org.example.parser.supplementary;

import org.example.util.Utils;
import java.util.Arrays;
import static org.example.parser.supplementary.TokenType.*;

/**
 * @author aram.azatyan | 2/22/2024 2:49 PM
 */
public class Token {
    private TokenType type;
    private String value;

    public Token(TokenType type, String value) {
        if (type == null) throw new IllegalArgumentException("no type specified");
        if (type == OPENING_PARENTHESES || type == CLOSING_PARENTHESES || type == NEGATION || type == COMMA) {
            this.type = type;
            this.value = type.getPermissibleValues()[0];
            return;
        }

        if ((type == WORD || type == CONSTANT || type == VARIABLE || type == FUNCTION || type == PREDICATE) && Utils.isNullOrBlank(value))
            throw new IllegalArgumentException("no value for a token of type WORD");
        if (type == CONNECTIVE && !Arrays.asList(type.getPermissibleValues()).contains(value))
            throw new IllegalArgumentException("not a valid value for a connective");

        this.type = type;
        this.value = value;
    }

    public Token(TokenType type) {
        if (type == null) throw new IllegalArgumentException("no type specified");
        if (type == WORD || type == CONSTANT || type == VARIABLE || type == FUNCTION || type == PREDICATE)
            throw new IllegalArgumentException("no value for a token of type WORD");

        this.type = type;
        this.value = null;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        if (type == null) throw new IllegalArgumentException("null param");
        if (this.type != WORD) throw new IllegalStateException();
        if (type != CONSTANT && type != FUNCTION && type != VARIABLE && type != PREDICATE) throw new IllegalArgumentException("invalid alternate type");
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof Token that)) return false;
        boolean isEqual = this.type == that.type;
        if (!isEqual) return false;
        if (this.type == OPENING_PARENTHESES || this.type == CLOSING_PARENTHESES || this.type == NEGATION) return true;
        return this.value.equals(that.value);
    }

    @Override
    public String toString() {
        return switch (this.type) {
            case WORD, CONSTANT, VARIABLE, FUNCTION, PREDICATE -> value;
            case OPENING_PARENTHESES, CLOSING_PARENTHESES, NEGATION, COMMA -> this.type.getPermissibleValues()[0];
            case CONNECTIVE -> value == null ? null : value;
        };
    }
}

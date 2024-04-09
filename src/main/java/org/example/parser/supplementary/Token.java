package org.example.parser.supplementary;

import org.example.domain.sentence.Literal;
import static org.example.parser.supplementary.TokenType.*;

/**
 * @author aram.azatyan | 2/22/2024 2:49 PM
 */
public class Token {
    private TokenType type;
    private String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(TokenType type) {
        this.type = type;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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


}

package com.gotwo.lexer;

/**
 * Created by florian on 27/11/15.
 *
 * Base class for our tokens.
 * Tokens will always have an TYPE.
 * Tokens may carry additional information,
 * e.g. an INTEGER may contain its numeric value.
 */
public class Token {
    public enum TYPE {NEWLINE, OPERATOR, ASSIGNMENT, INTEGER, IDENTIFIER, KEYWORD, BRACKET}
    protected TYPE type;

    public Token(TYPE type) {
        this.type = type;
    }

    public TYPE getType() {
        return type;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName().toLowerCase() + " " + type;
    }

    @Override
    public boolean equals(Object o) {
        if(!o.getClass().equals(this.getClass())) {
            return false;
        }

        return true;
    }
}

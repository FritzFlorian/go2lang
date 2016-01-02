package com.gotwo.error;

import com.gotwo.lexer.Token;

/**
 * Created by florian on 23/12/15.
 */
public class CompilerException extends Exception {
    private com.gotwo.lexer.Token token;

    public CompilerException(String message, Token token) {
        super(message);
        this.token = token;
    }

    @Override
    public String getMessage() {
        if (token != null) {
            return String.format("%-20s", "(Line " + token.getLine() + ") ") + super.getMessage();
        } else {
            return String.format("%-20s", "(Line unknown) ") + super.getMessage();
        }
    }

    public Token getToken() {
        return token;
    }
}

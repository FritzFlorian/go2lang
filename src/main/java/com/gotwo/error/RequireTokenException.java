package com.gotwo.error;

import com.gotwo.lexer.Token;

/**
 * Created by florian on 04/12/15.
 */
public class RequireTokenException extends Exception {
    private Token[] tokens;

    public RequireTokenException(Token... tokens) {
        super("Require different token(list). Requiring " + tokens.length + " tokens to match expression.");
        this.tokens = tokens;
    }

    @Override
    public String getMessage() {
        if(tokens.length < 1) {
            return "Reached end of file, but still requires tokens";
        }


        String ret = super.getMessage();
        for(Token token : tokens) {
            ret += "\n require " + token;
        }
        return ret;
    }

    public Token[] getTokens() {
        return tokens;
    }
}
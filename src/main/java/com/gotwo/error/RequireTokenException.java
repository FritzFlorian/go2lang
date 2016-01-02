package com.gotwo.error;

import com.gotwo.lexer.Token;

/**
 * Created by florian on 04/12/15.
 *
 * Thrown if there is an unexpected token while parsing.
 * Can contain an expected token or null if different tokens would be possible.
 * Always contains the actual found token.
 */
public class RequireTokenException extends CompilerException {
    private Token[] tokens;

    public RequireTokenException(Token... tokens) {
        super("Require different token(list). Requiring " + tokens.length + " tokens to match expression.", null);
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

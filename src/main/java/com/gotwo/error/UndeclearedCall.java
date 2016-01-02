package com.gotwo.error;

import com.gotwo.lexer.Token;

/**
 * Created by florian on 09/12/15.
 *
 * Thrown if there is an call to an undeclared built in language function.
 */
public class UndeclearedCall extends CompilerException {

    public UndeclearedCall(String name, Token token) {
        super("Undeclared call: " + name, token);
    }
}

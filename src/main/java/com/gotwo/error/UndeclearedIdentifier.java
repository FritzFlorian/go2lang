package com.gotwo.error;

import com.gotwo.lexer.Token;

/**
 * Created by florian on 09/12/15.
 *
 * Thrown if there an undeclared identifier is used.
 */
public class UndeclearedIdentifier extends CompilerException {

    public UndeclearedIdentifier(String name, Token token) {
        this(name, " forward declaration.", token);
    }

    public UndeclearedIdentifier(String name, String expected, Token token) {
        super("Found identifier " + name + ". Possible/expected" + expected + "!", token);
    }
}

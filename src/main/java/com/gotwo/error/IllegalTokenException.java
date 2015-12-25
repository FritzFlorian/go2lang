package com.gotwo.error;

import com.gotwo.lexer.Token;

/**
 * Created by florian on 04/12/15.
 *
 * Expection thrown if there was another token then expected by the parser.
 */
public class IllegalTokenException extends CompilerException {
    public IllegalTokenException(Token expected, Token actual) {
        super("Found illegal token. Expected " + expected + ", found " + actual + "!");
    }
}

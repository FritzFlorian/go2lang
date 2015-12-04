package com.gotwo.error;

import com.gotwo.lexer.Token;

/**
 * Created by florian on 04/12/15.
 */
public class IllegalTokenException extends Exception {
    public IllegalTokenException(Token expected, Token actual) {
        super("Found illegal token. Expected " + expected + ", found " + actual + "!");
    }
}

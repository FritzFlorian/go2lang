package com.gotwo.lexer;

/**
 * Created by florian on 27/11/15.
 *
 * Exception thrown by the lexer or compiler.
 */
public class SyntaxErrorException extends Exception {
    public static SyntaxErrorException newEndOfLineException(String expected) {
        return new SyntaxErrorException("Reached end of line, expected " + expected);
    }

    public static SyntaxErrorException newExpectedException(String expected, String got) {
        return new SyntaxErrorException("Found '" + got + "', expected '" + expected + "'");
    }

    public SyntaxErrorException(String message) {
        super(message);
    }
}

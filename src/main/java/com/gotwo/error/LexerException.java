package com.gotwo.error;

/**
 * Created by florian on 27/11/15.
 *
 * Exception thrown by the lexer or compiler.
 */
public class LexerException extends Exception {
    public static LexerException newEndOfLineException(String expected) {
        return new LexerException("Reached end of line, expected " + expected);
    }

    public static LexerException newExpectedException(String expected, String got) {
        return new LexerException("Found '" + got + "', expected '" + expected + "'");
    }

    public LexerException(String message) {
        super(message);
    }
}

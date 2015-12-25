package com.gotwo.error;

/**
 * Created by florian on 27/11/15.
 *
 * Expection thrown if there was an unexpected character read by the lexer.
 */
public class LexerException extends CompilerException {
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

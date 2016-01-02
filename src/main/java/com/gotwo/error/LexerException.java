package com.gotwo.error;

import com.gotwo.lexer.Token;

/**
 * Created by florian on 27/11/15.
 *
 * Expection thrown if there was an unexpected character read by the lexer.
 */
public class LexerException extends CompilerException {
    public static LexerException newEndOfLineException(String expected, int line) {
        return new LexerException("Reached end of line, expected " + expected, line);
    }

    public static LexerException newExpectedException(String expected, String got, int line) {
        return new LexerException("Found '" + got + "', expected '" + expected + "'", line);
    }

    public LexerException(String message, int line) {
        super(message, new Token(Token.TYPE.UNKNOWN, line));
    }
}

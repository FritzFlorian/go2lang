package com.gotwo.lexer;

/**
 * Created by florian on 27/11/15.
 *
 * Represents a newline in the file.
 * This is needed as we only allow one statement per line.
 */
public class Newline extends Token {
    public Newline() {
        super(TYPE.NEWLINE);
    }

    @Override
    public String toString() {
        return "\n";
    }
}

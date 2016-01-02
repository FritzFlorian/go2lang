package com.gotwo.lexer;

/**
 * Created by florian on 27/11/15.
 *
 * Represents a assigment in the file.
 */
public class Assignment extends Token {
    public Assignment(int line) {
        super(TYPE.ASSIGNMENT, line);
    }

    @Override
    public String toString() {
        return "=";
    }
}

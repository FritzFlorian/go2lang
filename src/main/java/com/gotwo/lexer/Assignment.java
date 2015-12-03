package com.gotwo.lexer;

/**
 * Created by florian on 27/11/15.
 *
 * Represents a assigment in the file.
 */
public class Assignment extends Token {
    public Assignment() {
        super(TYPE.ASSIGMENT);
    }

    @Override
    public String toString() {
        return "=";
    }
}

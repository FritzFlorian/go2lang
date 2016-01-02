package com.gotwo.lexer;

/**
 * Created by florian on 27/11/15.
 *
 * Represents an integer number.
 * Holds its value for later processing.
 */
public class Integer extends Token {
    private int value;

    public Integer(int value, int line) {
        super(TYPE.INTEGER, line);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "INT(" + getValue() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if(super.equals(o) == false) {
            return false;
        }

        return value == ((Integer)o).value;
    }
}

package com.gotwo.lexer;

/**
 * Created by florian on 27/11/15.
 *
 * Represents an mathematical operator.
 * This includes arithmetical and logical calculations.
 * Booleans will be represented by simple 0 or not 0.
 * This way we keep up with simple operator precedence.
 */
public class Operator extends Token {
    public enum OP {ADD, SUB, DIV, MUL, EQU, NOTEQU, LESS, GREATER, LESSEQU, GREATEREQU, NOT}
    private OP op;

    public Operator(OP op) {
        super(TYPE.OPERATOR);
        this.op = op;
    }

    public OP getOp() {
        return op;
    }

    @Override
    public String toString() {
        return "OP(" + getOp().toString().toLowerCase() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if(super.equals(o) == false) {
            return false;
        }

        return op == ((Operator)o).op;
    }
}

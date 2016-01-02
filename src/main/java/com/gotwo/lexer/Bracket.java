package com.gotwo.lexer;

/**
 * Created by florian on 05/12/15.
 *
 * Represents all occurences of brackets in the syntax.
 * For now this will only be in Math expressions.
 */
public class Bracket extends Token {
    public enum BRACKET {OPEN, CLOSE}

    private BRACKET bracket;

    public Bracket(BRACKET bracket, int line) {
        super(TYPE.BRACKET, line);
        this.bracket = bracket;
    }

    public BRACKET getBracket() {
        return bracket;
    }

    @Override
    public String toString() {
        switch (bracket) {
            case OPEN:
                return "(";
            case CLOSE:
                return ")";
            default:
                return super.toString();
        }
    }
}

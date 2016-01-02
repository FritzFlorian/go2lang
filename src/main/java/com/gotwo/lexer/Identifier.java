package com.gotwo.lexer;

/**
 * Created by florian on 27/11/15.
 *
 * Represents all identifiers set by the user.
 * This includes variable and jump mark names.
 */
public class Identifier extends Token {
    private String name;

    public Identifier(String name, int line) {
        super(TYPE.IDENTIFIER, line);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ID(" + getName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if(super.equals(o) == false) {
            return false;
        }

        return name.equals(((Identifier)o).name);
    }
}

package com.gotwo.error;

/**
 * Created by florian on 09/12/15.
 *
 * Thrown if there an undeclared identifier is used.
 */
public class UndeclearedIdentifier extends CompilerException {

    public UndeclearedIdentifier(String name) {
        this(name, " forward declaration.");
    }

    public UndeclearedIdentifier(String name, String expected) {
        super("Found identifier " + name + ". Possible/expected" + expected + "!" );
    }
}

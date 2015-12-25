package com.gotwo.error;

/**
 * Created by florian on 09/12/15.
 *
 * Thrown if there is an call to an undeclared built in language function.
 */
public class UndeclearedCall extends CompilerException {

    public UndeclearedCall(String name) {
        super("Undeclared call: " + name);
    }
}

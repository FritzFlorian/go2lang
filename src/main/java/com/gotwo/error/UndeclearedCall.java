package com.gotwo.error;

/**
 * Created by florian on 09/12/15.
 */
public class UndeclearedCall extends Exception {

    public UndeclearedCall(String name) {
        super("Undeclared call: " + name);
    }
}

package com.gotwo.error;

/**
 * Created by florian on 09/12/15.
 */
public class DuplicatedIdentifier extends Exception {

    public DuplicatedIdentifier(String name) {
        super("Found duplicate identifier with name " + name + ".");
    }

}

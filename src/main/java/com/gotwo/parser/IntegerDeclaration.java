package com.gotwo.parser;

/**
 * Created by florian on 05/12/15.
 *
 * Represents an integer declaration.
 * This is different from an Integer Token,
 * as this should be unique per scope and
 * contain some additional information.
 */
public class IntegerDeclaration {
    private String name;
    private int value;
    private int id;

    public IntegerDeclaration(String name, int value, int id) {
        this.name = name;
        this.value = value;
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getId() {
        return id;
    }
}

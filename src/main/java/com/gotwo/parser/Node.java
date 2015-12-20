package com.gotwo.parser;

/**
 * Created by florian on 03/12/15.
 *
 * Represents a single node in our AST.
 */
public abstract class Node {
    public enum TYPE {SCOPE, CONDITION, EXPRESSION, LABEL, ASSIGNMENT, GOTO, GOTOSPECIAL};

    private TYPE type;

    public Node(TYPE type) {
        this.type = type;
    }

    public TYPE getType() {
        return type;
    }

}

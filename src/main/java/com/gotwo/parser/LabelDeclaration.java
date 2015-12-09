package com.gotwo.parser;

/**
 * Created by florian on 05/12/15.
 */
public class LabelDeclaration {
    private String name;
    private ScopeNode scope;
    private int id;

    public LabelDeclaration(String name, ScopeNode scope, int id) {
        this.name = name;
        this.scope = scope;
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public ScopeNode getScope() {
        return scope;
    }

    public int getId() {
        return id;
    }
}

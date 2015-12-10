package com.gotwo.parser;

/**
 * Created by florian on 05/12/15.
 */
public class LabelDeclaration {
    private String name;
    private ScopeNode scope;
    private int id;
    private int position;
    private boolean declared;

    public LabelDeclaration(String name, ScopeNode scope, int id,int position, boolean declared) {
        this.name = name;
        this.scope = scope;
        this.id = id;
        this.position = position;
        this.declared = declared;
    }

    public void markAsDeclared() {
        declared = true;
    }

    public boolean isDeclared() {
        return declared;
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

    public int getPosition() {
        return position;
    }
}

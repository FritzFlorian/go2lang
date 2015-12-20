package com.gotwo.parser;


import org.objectweb.asm.Label;

/**
 * Created by florian on 05/12/15.
 */
public class LabelDeclaration {
    private String name;
    private ScopeNode scope;
    private int id;
    private int position;
    private boolean declared;
    private Label label;

    public LabelDeclaration(String name, ScopeNode scope, int id,int position, boolean declared) {
        this.name = name;
        this.scope = scope;
        this.id = id;
        this.position = position;
        this.declared = declared;
    }

    public void markAsDeclared(ScopeNode scope) {
        this.scope = scope;
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


    public Label getLabel() {
        if(label == null) {
            label = new Label();
        }

        return label;
    }
}

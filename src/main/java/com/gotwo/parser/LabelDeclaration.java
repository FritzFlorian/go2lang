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
    private int numericValue;

    public LabelDeclaration(String name, ScopeNode scope, int id,int position, boolean declared, int numericValue) {
        this.name = name;
        this.scope = scope;
        this.id = id;
        this.position = position;
        this.declared = declared;
        this.numericValue = numericValue;
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

    public void incrementPosition() {
        position++;
    }

    @Override
    public boolean equals(Object other) {
        if(!this.getClass().equals(other.getClass())) {
            return false;
        }

        LabelDeclaration otherLabelDeclaration = (LabelDeclaration) other;

        if(otherLabelDeclaration.name != this.name && !otherLabelDeclaration.name.equals(this.name)) {
            return false;
        }
        if(otherLabelDeclaration.scope != this.scope && !otherLabelDeclaration.scope.equals(this.getScope())) {
            return false;
        }
        if(otherLabelDeclaration.id != this.id) {
            return false;
        }
        if(otherLabelDeclaration.position != this.position) {
            return false;
        }
        if(otherLabelDeclaration.declared != this.declared) {
            return false;
        }


        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;
        if(name != null) {
            code += name.hashCode();
        }
        if(scope != null) {
            code += scope.hashCode();
        }
        code += id;
        code += position;
        code += declared ? 1 : 0;

        return code;
    }

    public int getNumericValue() {
        return numericValue;
    }
}

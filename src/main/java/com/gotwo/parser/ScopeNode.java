package com.gotwo.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by florian on 03/12/15.
 *
 * A node representing a scope in our program.
 * Scopes consist out of a variable count of statements.
 *
 * Special statements to look for include variable definitions
 * and variable use(to determine local variables) and
 * other scopes that will be sub-scopes.
 */
public class ScopeNode extends Node {
    private Map<String, IntegerDeclaration> integerDeclarations;
    private List<LabelNode> labelNodes;
    private ScopeNode parentScope; //Needed for variable lookup
    private ParsingContext context;
    private List<Node> childNodes;
    private int id;
    private int height;

    public ScopeNode(ScopeNode parentScope, ParsingContext context) {
        super(TYPE.SCOPE);
        this.parentScope = parentScope;
        this.integerDeclarations = new HashMap<>();
        this.context = context;
        this.id = context.getNextScopeId();
        this.childNodes = new ArrayList<>();
        this.labelNodes = new ArrayList<>();

        if(parentScope == null) {
            height = 0;
        } else {
            height = parentScope.getHeight() + 1;
        }
    }

    public boolean addInteger(String name, int value) {
        if(integerDeclarations.containsKey(name)) {
            //We already defined this in the symbol table...
            //Usually only one declaration is allowed
            return false;
        }

        IntegerDeclaration integer = new IntegerDeclaration(name, value, context.getNextIntegerId());
        integerDeclarations.put(name, integer);

        return true;
    }

    public void addChildNode(Node newChild) {
        childNodes.add(newChild);
        if(newChild instanceof LabelNode) {
            labelNodes.add((LabelNode)newChild);
        }
    }

    public void addFirstChildNode(Node newChild) {
        childNodes.add(0, newChild);
        for(Node child : childNodes) {
            if(child instanceof  LabelNode) {
                LabelNode labelNode = (LabelNode)child;
                labelNode.getLabelDeclaration().incrementPosition();
            }
        }
    }

    public List<Node> getChildNodes() {
        return childNodes;
    }
    public Map<String, IntegerDeclaration> getIntegerDeclarations() {
        return integerDeclarations;
    }

    /**
     * For now we do not care too much about the performance.
     * Simply get it implemented for now.
     *
     * @param name The identifier name that we search an integer for.
     * @return The Integer Declaration or NULL if not found.
     */
    public IntegerDeclaration getIntegerDeclaration(String name) {
        IntegerDeclaration integerDeclaration = integerDeclarations.get(name);
        if(integerDeclaration != null) {
            return integerDeclaration;
        }

        if(parentScope == null) {
            return null;
        }

        return parentScope.getIntegerDeclaration(name);
    }

    public IntegerDeclaration getLocalIntegerDeclaration(String name) {
        return integerDeclarations.get(name);
    }

    public int getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public ScopeNode getParentScope() {
        return parentScope;
    }


    //TODO: better hash code and equals implementations
    @Override
    public boolean equals(Object other) {
        if(!other.getClass().equals(this.getClass())) {
            return false;
        }

        ScopeNode otherNode = (ScopeNode) other;

        return otherNode.id == this.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public List<LabelNode> getLabelNodes() {
        return labelNodes;
    }
}

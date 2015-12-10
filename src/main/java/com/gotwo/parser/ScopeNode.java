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
    private ScopeNode parentScope; //Needed for variable lookup
    private ParsingContext context;
    private List<Node> childNodes;

    public ScopeNode(ScopeNode parentScope, ParsingContext context) {
        super(TYPE.SCOPE);
        this.parentScope = parentScope;
        this.integerDeclarations = new HashMap<>();
        this.context = context;
        this.childNodes = new ArrayList<>();
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
    }

    public List<Node> getChildNodes() {
        return childNodes;
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
}

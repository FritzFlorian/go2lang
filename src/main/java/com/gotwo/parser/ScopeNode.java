package com.gotwo.parser;

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
    private ScopeNode parentScope; //Needed for variable lookup

    public ScopeNode(ScopeNode parentScope) {
        this.parentScope = parentScope;
    }
}

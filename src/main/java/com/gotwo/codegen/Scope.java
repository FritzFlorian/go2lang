package com.gotwo.codegen;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by florian on 12/12/15.
 *
 * This represents a runtime scope.
 * For now we will implement it quite inefficient,
 * most important thing for now is that variable lookup works.
 */
public class Scope {
    private Scope logicalParent;
    private Map<String,Integer> integers;


    public Scope(Scope logicalParent) {
        this.logicalParent = logicalParent;
        this.integers = new HashMap<>();
    }

    /**
     * Used to set a local integer variable of this scope.
     * This will only be used at scope creation to init
     * variables of this scope.
     * There will be generated java code that calls and
     * there fore initialises an scope at runtime.
     *
     * @param name The name of the target variable
     * @param value The new value for the variable
     */
    public void setLocalIntegerVariable(String name, int value) {
        System.out.println("Init integer " + name + " with value " + value);
        integers.put(name, value);
    }


    /**
     * Used to set an integer value at runtime.
     * Note that we do not need error handling,
     * as the go2lang is typesave and all accessed
     * variables must be defined at the callinc scope.
     *
     * @param name The name of the target variable
     * @param value The new value for the variable
     */
    public void setIntegerValue(String name, int value) {
        System.out.println("Update integer " + name + " to value " + value);
        Scope currentScope = this;
        while(!currentScope.integers.containsKey(name)) {
            currentScope = currentScope.logicalParent;
        }

        currentScope.integers.put(name, value);
    }

    /**
     * Used to get an integer value at runtime.
     * Note that we do not need error hanling.
     *
     * @param name The name of the target variable
     * @return the variables integer value
     */
    public int getIntegerValue(String name) {
        Scope currentScope = this;
        while(!currentScope.integers.containsKey(name)) {
            currentScope = currentScope.logicalParent;
        }

        return currentScope.integers.get(name);
    }
}

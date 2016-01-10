package com.gotwo.codegen;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by florian on 12/12/15.
 *
 * This represents a runtime scope.
 * For now we will implement it quite inefficient,
 * most important thing for now is that variable lookup works.
 */
public class Scope {
    public Scope logicalParent;
    public Map<String,Integer> integers;
    public int id;
    public int lastLabel = -1;
    public String externalBackLabel = null;
    public String externalBackFile = null;


    public Scope(Scope logicalParent, int id) {
        this.logicalParent = logicalParent;
        this.integers = new HashMap<>();
        this.id = id;
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
        //System.out.println("Init integer " + name + " with value " + value);
        integers.put(name, value);
    }


    /**
     * Used to set an integer value at runtime.
     *
     * @param name The name of the target variable
     * @param value The new value for the variable
     */
    public void setIntegerValue(String name, int value) {
        //System.out.println("Update integer " + name + " to value " + value);
        Scope currentScope = this;
        while(currentScope != null) {
            if(currentScope.integers.containsKey(name)) {
                currentScope.integers.put(name, value);
                return;
            }
            currentScope = currentScope.logicalParent;
        }
    }

    /**
     * Used to get an integer value at runtime.
     *
     * @param name The name of the target variable
     * @return the variables integer value
     */
    public int getIntegerValue(String name) {
        Scope currentScope = this;
        while(currentScope != null) {
            if(currentScope.integers.containsKey(name)) {
                return currentScope.integers.get(name);
            }
            currentScope = currentScope.logicalParent;
        }

        return 0;
    }

    /**
     * Returns a parent scope with a given id.
     * Needed to find the right Scopes at go to instructions.
     *
     * @param id
     * @return
     */
    public Scope getParentWithId(int id) {
        Scope currentScope = this;
        while(currentScope.id != id) {
            currentScope = currentScope.logicalParent;
        }

        return currentScope;
    }

    /**
     * Merges two scopes like supposed when going to an label.
     * Uses the given scope to change variable of this scope.
     *
     * @param oldScope The old scope to merge variables into this scope.
     */
    public void mergeForGoTo(Scope oldScope) {
        if(oldScope == null) {
            return;
        }

        Set<Map.Entry<String, Integer>> oldIntegers = oldScope.integers.entrySet();

        for(Map.Entry<String, Integer> oldInteger : oldIntegers) {
           if(integers.containsKey(oldInteger.getKey())) {
               integers.put(oldInteger.getKey(), oldInteger.getValue());
           }
        }
    }

    /**
     * A debug method to get variables printed to console.
     */
    public void printRun() {
        Set<Map.Entry<String, Integer>> integerSet = integers.entrySet();

        for(Map.Entry<String, Integer> integer : integerSet) {
            System.out.println(integer.getKey() + " = " + integer.getValue());
        }
    }

    /**
     * Gets the next external back label string.
     */
    public String getNextExternalBackLabel() {
        Scope currentScope = this;
        while(currentScope != null) {
            if(currentScope.externalBackLabel != null) {
                return currentScope.externalBackLabel;
            }
            currentScope = currentScope.logicalParent;
        }

        return null;
    }

    /**
     * Gets the next external back file string.
     */
    public String getNextExternalBackFile() {
        Scope currentScope = this;
        while(currentScope != null) {
            if(currentScope.externalBackFile != null) {
                return currentScope.externalBackFile;
            }
            currentScope = currentScope.logicalParent;
        }

        return null;
    }

    public void printScopeStructure() {
        Scope currentScope = this;
        while(currentScope != null) {
            currentScope.printScope();
            currentScope = currentScope.logicalParent;
        }
    }

    public void printScope() {
        System.out.println("=====SCOPE" + id + "=====");
        System.out.println("LogicalParent: " + logicalParent);
        System.out.println("BackFile:  " + externalBackFile);
        System.out.println("BackLabel: " + externalBackLabel);
        System.out.println("NextBackFile:  " + getNextExternalBackFile());
        System.out.println("NextBackLabel: " + getNextExternalBackLabel());
        for(Map.Entry<String, Integer> entry : integers.entrySet()) {
            System.out.println("Integer " + entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("================");
    }

    public void removeBottomDuplicate() {
        Scope currentScope = this;
        while(currentScope != null) {
            if(currentScope.id == 0
                    && currentScope.logicalParent != null
                    && currentScope.logicalParent.id == 0) {
                currentScope.logicalParent = currentScope.logicalParent.logicalParent;
                return;
            }
            currentScope = currentScope.logicalParent;
        }
    }
}

package com.gotwo.codegen;


/**
 * Created by florian on 11/12/15.
 */
public class LearnASM {

    private Scope currentScope;

    public static void main(String[] args) {
        LearnASM learn = new LearnASM();
        learn.run();
    }

    public void run() {
        currentScope = initScopeWithId(1, null);
        System.out.println(currentScope);
        Scope loc = currentScope;

        int i = 315 * currentScope.getIntegerValue("test");
        currentScope.setIntegerValue("name", i);
    }

    public Scope initScopeWithId(int id, Scope logicalParent) {
        Scope result = new Scope(logicalParent);

        switch (id) {
            case 1:
                result.setLocalIntegerVariable("a", 10);
                result.setLocalIntegerVariable("c", 1);
                break;
            case 2:
                result.setLocalIntegerVariable("x", 3434);
                break;
        }

        return result;
    }
}
package com.gotwo.codegen;


/**
 * Created by florian on 11/12/15.
 */
public class LearnASM extends GoTwoBase{

    public static void main(String[] args) {
        LearnASM learn = new LearnASM();

        learn.start();
    }

    public void run() {
        this.currentScope = null;
        this.targetFile = null;
        this.targetSpeed = null;
        if(this.oldScope != null) {
            oldScope.printRun();
        } else {
            System.out.println("No old scope given...");
        }

        this.currentScope = oldScope;
        this.targetFile = "com.gotwo.Dummy";
    }

}

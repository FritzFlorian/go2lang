package com.gotwo.codegen;

/**
 * Created by florian on 29/12/15.
 */
public abstract class GoTwoBase {
    public Scope currentScope = null;
    public String targetFile = null;
    public String targetLabel = null;
    public Scope oldScope = null;
    public SPEED targetSpeed;

    public void start() {
        GoTwoBase current = this;

        current.run();
        System.out.println("Finished first run...");
        while(current.targetFile != null) {
            System.out.println("Jump to file..." + current.targetFile + " and " + current.targetLabel);
            GoTwoBase next = instantiate(current.targetFile, GoTwoBase.class);
            next.oldScope = current.currentScope;
            next.targetLabel = current.targetLabel;
            next.targetSpeed = current.targetSpeed;

            current = next;

            if(current.targetSpeed == SPEED.SPRINT || current.targetSpeed == SPEED.RUN || current.targetSpeed == SPEED.GO) {
                current.currentScope = null;
            } else {
                current.currentScope = current.oldScope;
            }

            current.run();
        }
        System.out.println("stop go two program..." + current.targetFile + " and " + current.targetLabel);
    }

    public abstract void run();

    public <T> T instantiate(final String className, final Class<T> type){
        try{
            return type.cast(Class.forName(className).newInstance());
        } catch(final InstantiationException e){
            System.out.println("Could not init: " + e.getClass().getName());
            throw new IllegalStateException(e);
        } catch(final IllegalAccessException e){
            System.out.println("Could not access: " + e.getClass().getName());
            throw new IllegalStateException(e);
        } catch(final ClassNotFoundException e){
            System.out.println("Could not get: " + e.getClass().getName());
            throw new IllegalStateException(e);
        }
    }
}

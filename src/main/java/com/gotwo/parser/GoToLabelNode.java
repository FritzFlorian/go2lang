package com.gotwo.parser;

/**
 * Created by florian on 10/12/15.
 */
public class GoToLabelNode extends Node {
    public enum SPEED {GO, RUN, WALK, SPRINT}

    private LabelDeclaration target;
    private SPEED speed;

    public GoToLabelNode(SPEED speed, LabelDeclaration target) {
        super(TYPE.GOTO);
        this.speed = speed;
        this.target = target;
    }

    public LabelDeclaration getTarget() {
        return target;
    }

    public SPEED getSpeed() {
        return speed;
    }

    @Override
    public boolean equals(Object other) {
        if(!other.getClass().equals(this.getClass())) {
            return false;
        }

        GoToLabelNode otherNode = (GoToLabelNode) other;
        if(otherNode.target != this.target && !otherNode.target.equals(this.target)) {
            return false;
        }
        if(otherNode.speed != this.speed) {
            return false;
        }

        return true;
    }
}

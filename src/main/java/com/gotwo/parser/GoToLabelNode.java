package com.gotwo.parser;

import com.gotwo.codegen.SPEED;

/**
 * Created by florian on 10/12/15.
 */
public class GoToLabelNode extends Node {
    private LabelDeclaration target;
    private SPEED speed;
    private LabelDeclaration backLabel;

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

    public LabelDeclaration getBackLabel() {
        return backLabel;
    }

    public void setBackLabel(LabelDeclaration backLabel) {
        this.backLabel = backLabel;
    }
}

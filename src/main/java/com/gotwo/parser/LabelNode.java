package com.gotwo.parser;

/**
 * Created by florian on 09/12/15.
 */
public class LabelNode extends Node {
    private LabelDeclaration labelDeclaration;

    public LabelNode(LabelDeclaration labelDeclaration) {
        super(TYPE.LABEL);
        this.labelDeclaration = labelDeclaration;
    }

    public LabelDeclaration getLabelDeclaration() {
        return labelDeclaration;
    }

    @Override
    public boolean equals(Object other) {
        if(!other.getClass().equals(this.getClass())) {
            return false;
        }
        LabelNode otherLabel = (LabelNode) other;
        return this.labelDeclaration.equals(otherLabel.labelDeclaration);
    }

}

package com.gotwo.parser;

/**
 * Created by florian on 09/12/15.
 */
public class LabelNode extends Node {
    LabelDeclaration labelDeclaration;

    public LabelNode(LabelDeclaration labelDeclaration) {
        super(TYPE.LABEL);
        this.labelDeclaration = labelDeclaration;
    }

    public LabelDeclaration getLabelDeclaration() {
        return labelDeclaration;
    }
}

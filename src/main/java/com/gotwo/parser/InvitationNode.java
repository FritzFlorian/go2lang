package com.gotwo.parser;

import com.gotwo.codegen.SPEED;

/**
 * Created by florian on 06/01/16.
 */
public class InvitationNode extends Node {
    private LabelDeclaration internalTarget;
    private String externalName;
    private SPEED speed;

    public InvitationNode(String externalName, LabelDeclaration internalTarget, SPEED speed) {
        super(TYPE.INVITATION);
        this.internalTarget = internalTarget;
        this.externalName = externalName;
        this.speed = speed;
    }

    public String getExternalName() {
        return externalName;
    }

    public SPEED getSpeed() {
        return speed;
    }

    public LabelDeclaration getInternalTarget() {
        return internalTarget;
    }
}

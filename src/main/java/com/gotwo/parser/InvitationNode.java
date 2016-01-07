package com.gotwo.parser;

import com.gotwo.codegen.SPEED;

/**
 * Created by florian on 06/01/16.
 */
public class InvitationNode extends Node {
    private GoToLabelNode jumpInstruction;
    private String externalName;
    private SPEED speed;

    public InvitationNode(String externalName, GoToLabelNode jumpInstruction, SPEED speed) {
        super(TYPE.INVITATION);
        this.jumpInstruction = jumpInstruction;
        this.externalName = externalName;
        this.speed = speed;
    }


    public GoToLabelNode getJumpInstruction() {
        return jumpInstruction;
    }

    public String getExternalName() {
        return externalName;
    }

    public SPEED getSpeed() {
        return speed;
    }
}

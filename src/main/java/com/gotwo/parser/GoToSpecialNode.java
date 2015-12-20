package com.gotwo.parser;

/**
 * Created by florian on 17/12/15.
 */
public class GoToSpecialNode extends Node {
    public enum SPECIAL {CONSOLE}

    private SPECIAL special;
    private GoToLabelNode.SPEED speed;

    public GoToSpecialNode(GoToLabelNode.SPEED speed, SPECIAL special) {
        super(TYPE.GOTOSPECIAL);
        this.speed = speed;
        this.special = special;
    }

    public GoToLabelNode.SPEED getSpeed() {
        return  speed;
    }

    public SPECIAL getSpecial() {
        return special;
    }
}

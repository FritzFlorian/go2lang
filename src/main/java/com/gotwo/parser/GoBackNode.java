package com.gotwo.parser;

import com.gotwo.codegen.SPEED;

/**
 * Created by florian on 30/12/15.
 */
public class GoBackNode extends Node {
    private SPEED speed;

    public GoBackNode(SPEED speed) {
        super(TYPE.GOBACK);
        this.speed = speed;
    }

    public SPEED getSpeed() {
        return speed;
    }
}

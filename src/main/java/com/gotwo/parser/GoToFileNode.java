package com.gotwo.parser;

import com.gotwo.codegen.SPEED;

/**
 * Created by florian on 29/12/15.
 */
public class GoToFileNode extends Node {
    private SPEED speed;
    private String targetFile, targetLabel;
    private LabelDeclaration backLabel;

    public GoToFileNode (SPEED speed, String targetFile, String targetLabel, LabelDeclaration backLabel) {
        super(TYPE.GOTOFILE);
        this.speed = speed;
        this.targetFile = targetFile;
        this.targetLabel = targetLabel;
        this.backLabel = backLabel;
    }

    public SPEED getSpeed() {
        return speed;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public String getTargetLabel() {
        return targetLabel;
    }

    public LabelDeclaration getBackLabel() {
        return backLabel;
    }
}

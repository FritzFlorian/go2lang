package com.gotwo.parser;

/**
 * Created by florian on 07/12/15.
 */
public abstract class ConditionNode extends Node {
    public enum BRANCHES{IF}
    private BRANCHES branches;

    public ConditionNode(BRANCHES branches) {
        super(TYPE.CONDITION);
        this.branches = branches;
    }

    public BRANCHES getBranches() {
        return branches;
    }

    public static ConditionNode NewConditionNode(ExpressionNode expression, ScopeNode ifScope) {
        return new IfConditionNode(expression, ifScope);
    }
}

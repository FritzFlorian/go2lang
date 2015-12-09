package com.gotwo.parser;

/**
 * Created by florian on 07/12/15.
 */
public abstract class ConditionNode extends Node {
    public ConditionNode() {
        super(TYPE.CONDITION);
    }

    public static ConditionNode NewConditionNode(ExpressionNode expression, ScopeNode ifScope) {
        return new IfConditionNode(expression, ifScope);
    }

    /*public static NewConditionNode(ScopeNode ifScope, ScopeNode elseScope) {

    }*/
}

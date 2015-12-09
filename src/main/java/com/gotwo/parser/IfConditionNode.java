package com.gotwo.parser;

/**
 * Created by florian on 07/12/15.
 */
public class IfConditionNode extends ConditionNode {
    private ScopeNode ifScope;
    private ExpressionNode expression;

    public IfConditionNode(ExpressionNode expression, ScopeNode ifScope) {
        this.expression = expression;
        this.ifScope = ifScope;
    }
}

package com.gotwo.parser;

/**
 * Created by florian on 07/12/15.
 */
public class IfConditionNode extends ConditionNode {
    private ScopeNode ifScope;
    private ExpressionNode expression;

    public IfConditionNode(ExpressionNode expression, ScopeNode ifScope) {
        super(BRANCHES.IF);
        this.expression = expression;
        this.ifScope = ifScope;
    }

    public ScopeNode getIfScope() {
        return ifScope;
    }

    public ExpressionNode getExpression() {
        return expression;
    }
}

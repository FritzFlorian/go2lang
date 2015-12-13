package com.gotwo.parser;

/**
 * Created by florian on 09/12/15.
 *
 * Represents a simple variable assignment.
 * We assign an value to an Integer declaration(for now).
 * This value is an expression, this can be as complex as needed.
 */
public class AssignmentNode extends Node {
    private IntegerDeclaration integerDeclaration;
    private ExpressionNode expressionNode;

    public AssignmentNode(IntegerDeclaration integerDeclaration, ExpressionNode expressionNode) {
        super(TYPE.ASSIGNMENT);
        this.integerDeclaration = integerDeclaration;
        this.expressionNode = expressionNode;
    }


    public IntegerDeclaration getIntegerDeclaration() {
        return integerDeclaration;
    }

    public ExpressionNode getExpressionNode() {
        return expressionNode;
    }
}

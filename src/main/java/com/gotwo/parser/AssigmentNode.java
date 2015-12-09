package com.gotwo.parser;

/**
 * Created by florian on 09/12/15.
 *
 * Represents a simple variable assignment.
 * We assign an value to an Integer declaration(for now).
 * This value is an expression, this can be as complex as needed.
 */
public class AssigmentNode extends Node {
    private IntegerDeclaration integerDeclaration;
    private ExpressionNode expressionNode;

    public AssigmentNode(IntegerDeclaration integerDeclaration, ExpressionNode expressionNode) {
        super(TYPE.ASSIGMENT);
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

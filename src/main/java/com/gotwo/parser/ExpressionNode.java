package com.gotwo.parser;

import com.gotwo.lexer.Operator;

/**
 * Created by florian on 07/12/15.
 */
public abstract class ExpressionNode extends Node {

    public ExpressionNode() {
        super(TYPE.EXPRESSION);
    }

    /**
     * An expression with an left hand and right hand sub node.
     */
    public static class SubExpressionNode extends ExpressionNode {
        private ExpressionNode left, right;
        private Operator operator;

        public SubExpressionNode(ExpressionNode left, Operator operator, ExpressionNode right) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        public ExpressionNode getLeft() {
            return left;
        }

        public ExpressionNode getRight() {
            return right;
        }

        public Operator getOperator() {
            return operator;
        }
    }

    /**
     * An expression with an Fixed value.
     */
    public static class ConstIntExpressionNode extends ExpressionNode {
        private int value;

        public ConstIntExpressionNode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * An expression with an Int variable as its value.
     */
    public static class IntExpressionNode extends ExpressionNode {
        private IntegerDeclaration integerDeclaration;

        public IntExpressionNode(IntegerDeclaration integerDeclaration) {
            this.integerDeclaration = integerDeclaration;
        }

        public IntegerDeclaration getIntegerDeclaration() {
            return integerDeclaration;
        }
    }
}


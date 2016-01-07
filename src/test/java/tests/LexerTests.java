package tests;

import com.gotwo.lexer.*;
import com.gotwo.lexer.Integer;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class LexerTests {

    /**
     * @param str The program code
     * @return BufferReader for lexer processing.
     */
    private static BufferedReader getReader(String str) {
        InputStream is = new ByteArrayInputStream(str.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        return br;
    }

    @Test
    public void recognizeSimpleKeywords() {
        recognizeKeyword("end", 0, Keyword.KEY.END);
        recognizeKeyword("if", 0, Keyword.KEY.IF);
        recognizeKeyword("int", 0, Keyword.KEY.INT);
        recognizeKeyword("label", 0, Keyword.KEY.LABEL);
        recognizeKeyword("scope", 0, Keyword.KEY.SCOPE);
        recognizeKeyword("go", 0, Keyword.KEY.GO);
        recognizeKeyword("to", 0, Keyword.KEY.TO);
        recognizeKeyword("run", 0, Keyword.KEY.RUN);
        recognizeKeyword("back", 0, Keyword.KEY.BACK);
        recognizeKeyword("other", 0, Keyword.KEY.OTHER);
        recognizeKeyword("invite", 0, Keyword.KEY.INVITE);
    }

    @Test
    public void recognizeKeywordsInContext() {
        recognizeKeyword("end test", 0, Keyword.KEY.END);
        recognizeKeyword("test end", 1, Keyword.KEY.END);
        recognizeKeyword("test>end", 2, Keyword.KEY.END);
        recognizeKeyword("if\n int x = 10\n x = x + 4\n end", 2, Keyword.KEY.INT);
    }

    private void recognizeKeyword(String input, int pos, Keyword.KEY key) {
        Lexer lexer;
        List<Token> res;

        lexer = new Lexer(getReader(input));
        try {
            res = lexer.lexAll();
            assertEquals("Lexer should detected " + key + " keyword.(Input: " + input + " )", res.get(pos), new Keyword(key, 0));
        } catch (Exception e) {
            org.junit.Assert.fail("Lexer should detected " + key + " keyword.(Input: " + input + " )");
        }
    }

    @Test
    public void recognizeSimpleIdentifiers() {
        recognizeIdentifier("test", 0, "test");
    }

    @Test
    public void recognizeIdentifiersInContext() {
        recognizeIdentifier("end test", 1, "test");
        recognizeIdentifier("test end", 0, "test");
        recognizeIdentifier("endtest", 0, "endtest");
        recognizeIdentifier("test<test", 2, "test");
        recognizeIdentifier("if\n int x = 10\n x = x + 4\n end", 9, "x");
    }

    private void recognizeIdentifier(String input, int pos, String identifier) {
        Lexer lexer;
        List<Token> res;

        lexer = new Lexer(getReader(input));
        try {
            res = lexer.lexAll();
            assertEquals("Lexer should detected " + identifier + " identifier.(Input: " + input + " )", res.get(pos), new Identifier(identifier, 0));
        } catch (Exception e) {
            org.junit.Assert.fail("Lexer should detected " + identifier + " identifier.(Input: " + input + " )");
        }
    }

    @Test
    public void recognizeSimpleAssignments() {
        recognizeAssignment("=", 0);
    }

    @Test
    public void recognizeAssigmentsInContext() {
        recognizeAssignment("aoeu=531", 1);
        recognizeAssignment("end=end== =", 4);
        recognizeAssignment("5=+3", 1);
    }

    private void recognizeAssignment(String input, int pos) {
        Lexer lexer;
        List<Token> res;

        lexer = new Lexer(getReader(input));
        try {
            res = lexer.lexAll();
            assertEquals("Lexer should detected assignment identifier.(Input: " + input + " )", res.get(pos), new Assignment(0));
        } catch (Exception e) {
            org.junit.Assert.fail("Lexer should detected assignment identifier.(Input: " + input + " )");
        }
    }

    @Test
    public void removeDoubleNewlines() {
        recognizeAssignment("\n\n\n=\n", 0);
        recognizeKeyword("end\n\n\n\n\nend", 2, Keyword.KEY.END);
        recognizeIdentifier("\nend\n\n\nend\nend\ntest", 6, "test");
    }

    @Test
    public void recognizeSimpleOperator() {
        recognizeOperator("+", 0, Operator.OP.ADD);
        recognizeOperator("-", 0, Operator.OP.SUB);
        recognizeOperator("/", 0, Operator.OP.DIV);
        recognizeOperator("*", 0, Operator.OP.MUL);
        recognizeOperator("%", 0, Operator.OP.MOD);
        recognizeOperator("==", 0, Operator.OP.EQU);
        recognizeOperator("!=", 0, Operator.OP.NOTEQU);
        recognizeOperator(">=", 0, Operator.OP.GREATEREQU);
        recognizeOperator("<=", 0, Operator.OP.LESSEQU);
        recognizeOperator(">", 0, Operator.OP.GREATER);
        recognizeOperator("<", 0, Operator.OP.LESS);
        recognizeOperator("!", 0, Operator.OP.NOT);
    }

    @Test
    public void recognizeOperatorInContext() {
        recognizeOperator("5==4", 1, Operator.OP.EQU);
        recognizeOperator("\nscope\nint x = 6 + 5\nend", 6, Operator.OP.ADD);
        recognizeOperator("+-*/>=< =", 5, Operator.OP.LESS);
    }

    private void recognizeOperator(String input, int pos, Operator.OP operator) {
        Lexer lexer;
        List<Token> res;

        lexer = new Lexer(getReader(input));
        try {
            res = lexer.lexAll();
            assertEquals("Lexer should detected " + operator + " operator.(Input: " + input + " )", res.get(pos), new Operator(operator, 0));
        } catch (Exception e) {
            org.junit.Assert.fail("Lexer should detected " + operator + " operator.(Input: " + input + " )");
        }
    }

    @Test
    public void recognizeSimpleIntegers() {
        for(int i = 0; i < 100; i++) {
            recognizeInteger("" + i, 0, i);
        }
        recognizeInteger("" + java.lang.Integer.MAX_VALUE, 0, java.lang.Integer.MAX_VALUE);
    }

    @Test
    public void recognizeIntegerInContext() {
        recognizeInteger("int i = 4", 3, 4);
        recognizeInteger("535-100+531", 2, 100);
        recognizeInteger("<= \n\n\n\nend4", 3, 4);
    }


    private void recognizeInteger(String input, int pos, int value) {
        Lexer lexer;
        List<Token> res;

        lexer = new Lexer(getReader(input));
        try {
            res = lexer.lexAll();
            assertEquals("Lexer should detected " + value + " integer.(Input: " + input + " )", res.get(pos), new Integer(value, 0));
        } catch (Exception e) {
            org.junit.Assert.fail("Lexer should detected " + value + " integer.(Input: " + input + " )");
        }
    }
}

package tests;

import com.gotwo.lexer.Keyword;
import com.gotwo.lexer.Lexer;
import com.gotwo.lexer.Token;
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
    public void recognizeKeywords() {
        recognizeKeyword("end", 0, Keyword.KEY.END);
        recognizeKeyword("if", 0, Keyword.KEY.IF);
        recognizeKeyword("else", 0, Keyword.KEY.ELSE);
        recognizeKeyword("int", 0, Keyword.KEY.INT);
        recognizeKeyword("label", 0, Keyword.KEY.LABEL);
        recognizeKeyword("scope", 0, Keyword.KEY.SCOPE);
    }

    private void recognizeKeyword(String input, int pos, Keyword.KEY key) {
        Lexer lexer;
        List<Token> res;

        lexer = new Lexer(getReader(input));
        try {
            res = lexer.lexAll();
            assertEquals("Lexer detected " + key + " keyword.(Input: " + input + " )", res.get(pos), new Keyword(key));
        } catch (Exception e) {
            org.junit.Assert.fail("Lexer detected " + key + " keyword.(Input: " + input + " )");
        }
    }

}

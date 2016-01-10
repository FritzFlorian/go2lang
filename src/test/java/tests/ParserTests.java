package tests;

import com.gotwo.codegen.SPEED;
import com.gotwo.codegen.Scope;
import com.gotwo.error.*;
import com.gotwo.lexer.Identifier;
import com.gotwo.lexer.Keyword;
import com.gotwo.lexer.Newline;
import com.gotwo.lexer.Token;
import com.gotwo.parser.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by florian on 10/12/15.
 */
public class ParserTests {

    @Test
    public void shouldParseEmptyProgram() {
        List<Token> tokenList = new ArrayList<>(Arrays.asList(
                //Empty token list
        ));
        try {
            Parser parser = new Parser(tokenList);
            ParsingResult result = parser.parseTokens();

            resultNotNull(result);
            assertEquals(0, result.getLabelList().size());
            assertEquals(1, result.getRootScope().getChildNodes().size());
            checkEmptyScope((ScopeNode)result.getRootScope().getChildNodes().get(0));

        } catch (CompilerException e) {
            e.printStackTrace();
            fail("Compiling error in syntactical correct program.");
        }
    }

    //Some helpers to check the structure faster

    /**
     * Checks if a given ScopeNode is empty.
     * This means no variable declarations or child nodes.
     *
     * @param scopeNode The scope to be checked
     */
    private void checkEmptyScope(ScopeNode scopeNode) {
        assertNotNull(scopeNode.getChildNodes());
        assertEquals(0, scopeNode.getChildNodes().size());
        assertNotNull(scopeNode.getIntegerDeclarations());
        assertEquals(0, scopeNode.getIntegerDeclarations().size());
    }

    /**
     * Checks if a given ScopeNode is not null and
     * also its child attributes are not null.
     * Used to make sure there are no null pointers in
     * further tests.
     *
     * @param scopeNode The scope to be checked
     */
    private void scopeNotNull(ScopeNode scopeNode) {
        assertNotNull(scopeNode);
        assertNotNull(scopeNode.getChildNodes());
        assertNotNull(scopeNode.getIntegerDeclarations());
    }

    /**
     * Checks a result to contain no null values.
     *
     * @param result The scope to be checked
     */
    private void resultNotNull(ParsingResult result) {
        assertNotNull(result);
        scopeNotNull(result.getRootScope());
        assertNotNull(result.getContext());
        assertNotNull(result.getLabelList());
        assertNotNull(result.getTargetLabels());
        assertNotNull(result.getTargetScopes());
    }
}

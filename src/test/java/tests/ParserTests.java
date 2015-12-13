package tests;

import com.gotwo.error.DuplicatedIdentifier;
import com.gotwo.error.IllegalTokenException;
import com.gotwo.error.RequireTokenException;
import com.gotwo.error.UndeclearedIdentifier;
import com.gotwo.lexer.Token;
import com.gotwo.parser.Parser;
import com.gotwo.parser.ParsingResult;
import com.gotwo.parser.ScopeNode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by florian on 10/12/15.
 */
public class ParserTests {

    @Test
    public void parseEmptyProgram() {
        List<Token> tokenList = new ArrayList<>(Arrays.asList(
                //Empty token list
        ));
        try {
            Parser parser = new Parser(tokenList);
            ParsingResult result = parser.parseTokens();

            resultNotNull(result);
            assertEquals(0, result.getRootScope().getChildNodes().size());
            assertEquals(0, result.getLabelList().size());
            checkEmptyScope(result.getRootScope());

        } catch (DuplicatedIdentifier duplicatedIdentifier) {
            duplicatedIdentifier.printStackTrace();
        } catch (IllegalTokenException e) {
            e.printStackTrace();
        } catch (RequireTokenException e) {
            e.printStackTrace();
        } catch (UndeclearedIdentifier undeclearedIdentifier) {
            undeclearedIdentifier.printStackTrace();
        }
    }

    private void checkEmptyScope(ScopeNode scopeNode) {
        assertNotNull(scopeNode.getChildNodes());
        assertEquals(0, scopeNode.getChildNodes().size());
        assertNotNull(scopeNode.getIntegerDeclarations());
        assertEquals(0, scopeNode.getIntegerDeclarations().size());
    }

    private void resultNotNull(ParsingResult result) {
        assertNotNull(result);
        assertNotNull(result.getRootScope());
        assertNotNull(result.getRootScope().getChildNodes());
        assertNotNull(result.getRootScope().getIntegerDeclarations());
        assertNotNull(result.getContext());
        assertNotNull(result.getLabelList());
        assertNotNull(result.getTargetLabels());
        assertNotNull(result.getTargetScopes());
    }
}

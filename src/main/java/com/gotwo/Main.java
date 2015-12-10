package com.gotwo;

import com.gotwo.error.*;
import com.gotwo.lexer.Lexer;
import com.gotwo.lexer.Token;
import com.gotwo.parser.Parser;
import com.gotwo.parser.ParsingResult;
import com.gotwo.parser.ScopeNode;

import java.io.*;
import java.util.List;

/**
 * Created by florian on 25/11/15.
 */
public class Main {

    public static void main(String[] args) {
        String str = "int x = 531\nint y = 0\nx = (y + 4) * y\nlabel start\nscope\nint x = 4\ny = 4\nend\nrun to start";

        // convert String into InputStream
        InputStream is = new ByteArrayInputStream(str.getBytes());

        // read it with BufferedReader
        BufferedReader br = new BufferedReader(new InputStreamReader(is));


        Lexer lexer = new Lexer(br);
        try {
            List<Token> tokenList = lexer.lexAll();
            for(Token t : tokenList) {
                System.out.print("  "  + t.toString());
            }

            Parser parser = new Parser(tokenList);
            ParsingResult res;
            res = parser.parseTokens();
            System.out.print(res);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LexerException e) {
            System.err.println("Syntax Error: " + e.getMessage());
        } catch (IllegalTokenException e) {
            e.printStackTrace();
        } catch (RequireTokenException e) {
            e.printStackTrace();
        } catch (UndeclearedIdentifier undeclearedIdentifier) {
            undeclearedIdentifier.printStackTrace();
        } catch (DuplicatedIdentifier duplicatedIdentifier) {
            duplicatedIdentifier.printStackTrace();
        }


    }
}

package com.gotwo;

import com.gotwo.error.IllegalTokenException;
import com.gotwo.error.RequireTokenException;
import com.gotwo.error.UndeclearedIdentifier;
import com.gotwo.lexer.Lexer;
import com.gotwo.error.LexerException;
import com.gotwo.lexer.Token;
import com.gotwo.parser.Parser;
import com.gotwo.parser.ScopeNode;

import java.io.*;
import java.util.List;

/**
 * Created by florian on 25/11/15.
 */
public class Main {

    public static void main(String[] args) {
        String str = "int x = 531\nint y = 0\nx = (y + 4) * y\nscope\nint x = 4\ny = 4\nend";

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
            ScopeNode rootScope;
            rootScope = parser.parseTokens();
            System.out.print(rootScope);
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
        }


    }
}

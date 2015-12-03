package com.gotwo;

import com.gotwo.lexer.Lexer;
import com.gotwo.lexer.SyntaxErrorException;
import com.gotwo.lexer.Token;

import java.io.*;
import java.util.List;

/**
 * Created by florian on 25/11/15.
 */
public class Main {

    public static void main(String[] args) {
        String str = "int x = 531\nint y\n\nif a > b\nstuff\n  else\nother\nend";

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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SyntaxErrorException e) {
            System.err.println("Syntax Error: " + e.getMessage());
        }
    }
}

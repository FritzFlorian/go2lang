package com.gotwo;

import com.gotwo.codegen.CodeGenerator;
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

    public static void main(String[] args) throws FileNotFoundException {
        // read it with BufferedReader
        BufferedReader br = new BufferedReader(new FileReader("build/input/Dummy.go2"));


        Lexer lexer = new Lexer(br);
        try {
            List<Token> tokenList = lexer.lexAll();
            for(Token t : tokenList) {
                System.out.print("  "  + t.toString());
            }

            Parser parser = new Parser(tokenList);
            ParsingResult res;
            res = parser.parseTokens();

            CodeGenerator codeGenerator = new CodeGenerator(res, "/com/gotwo/Dummy");
            codeGenerator.generateClassFile("build/output");
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

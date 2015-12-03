package com.gotwo.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 27/11/15.
 *
 * A lexer for the go2 language.
 * Version 0.1 is quite simple and only consists of simple tokens.
 */
public class Lexer {
    private BufferedReader reader;

    public Lexer(BufferedReader reader) {
        this.reader = reader;
    }

    /**
     * Lexes the whole input to an List.
     * Might add on the fly lexing later if needed.
     *
     * @return A list of the tokens in the given input.
     */
    public List<Token> lexAll() throws IOException, SyntaxErrorException {
        List<Token> tokenList = new ArrayList<>();

        String line;
        int lineNr = 0;
        while((line = reader.readLine()) != null) {
            //Go2 supports exactly one statement per line
            //So we can simply parse it line by line
            lineNr++;
            try {
                String[] lines = line.split("\\n");
                for(String l : lines) {
                    tokenList = parseLine(l, tokenList);
                }
            } catch (SyntaxErrorException e ) {
                throw new SyntaxErrorException(e.getMessage() + " in line " + lineNr);
            }
        }
        return  tokenList;
    }

    private List<Token> parseLine(String line) throws IOException, SyntaxErrorException {
        return parseLine(line, new ArrayList<>());
    }

    private List<Token> parseLine(String line, List<Token> tokenList) throws IOException, SyntaxErrorException {
        if(line.length() < 1) {
            return tokenList;
        }

        PushbackReader reader = new PushbackReader(new StringReader(line));

        int i;
        char c;
        while((i = reader.read()) != -1 && i != 65535) {
            c = (char)i;

            if(Character.isLetter(c)) {
                //See if we have to handle a string token...

                //tokens should only consist of characters for now
                String name = "" + c;
                while(Character.isAlphabetic(i = reader.read())) {
                    name += (char)i;
                }
                reader.unread(i);

                Token token = Keyword.getKeyword(name);
                if(token == null) {
                    token = new Identifier(name);
                }
                tokenList.add(token);
            } else if(Character.isDigit(c)) {
                //...or a number...

                //lets only support integers for now
                String num = "" + c;
                while(Character.isDigit(i = reader.read())) {
                    num += (char)i;
                }
                reader.unread(i);

                tokenList.add(new Integer(java.lang.Integer.parseInt(num)));
            } else {
                //...or an operator
                switch (c) {
                    case '+':
                        tokenList.add(new Operator(Operator.OP.ADD));
                        break;
                    case '-':
                        tokenList.add(new Operator(Operator.OP.SUB));
                        break;
                    case '*':
                        tokenList.add(new Operator(Operator.OP.MUL));
                        break;
                    case '/':
                        tokenList.add(new Operator(Operator.OP.DIV));
                        break;
                    case '=':
                        tokenList.add(matchEqul(reader));
                        break;
                    case '!':
                        tokenList.add(mathExclamation(reader));
                        break;
                    case '<':
                        tokenList.add(mathLess(reader));
                        break;
                    case '>':
                        tokenList.add(mathGreater(reader));
                        break;
                    case ' ':
                        break;
                    default:
                        throw new SyntaxErrorException("Illegal Character '" + c + "'");
                }
            }
        }

        tokenList.add(new Newline());
        return tokenList;
    }

    private Token matchEqul(PushbackReader reader) throws IOException, SyntaxErrorException {
        int i = reader.read();
        if(i == -1) {
            throw SyntaxErrorException.newEndOfLineException("==");
        }

        char c = (char)i;
        switch (c) {
            case '=':
                return new Operator(Operator.OP.EQU);
            default:
                //found an equal sing
                reader.unread(c);
                return new Assignment();
        }
    }

    private Token mathExclamation(PushbackReader reader) throws IOException, SyntaxErrorException {
        int i = reader.read();

        char c = (char) i;
        switch (c) {
            case '=':
                return new Operator(Operator.OP.NOTEQU);
            default:
                reader.unread(c);
                return new Operator(Operator.OP.NOT);
        }
    }

    private Token mathGreater(PushbackReader reader) throws IOException, SyntaxErrorException {
        int i = reader.read();

        char c = (char) i;
        switch (c) {
            case '=':
                return new Operator(Operator.OP.GREATEREQU);
            default:
                reader.unread(c);
                return new Operator(Operator.OP.GREATER);
        }
    }

    private Token mathLess(PushbackReader reader) throws IOException, SyntaxErrorException {
        int i = reader.read();

        char c = (char) i;
        switch (c) {
            case '=':
                return new Operator(Operator.OP.LESSEQU);
            default:
                reader.unread(c);
                return new Operator(Operator.OP.LESS);
        }
    }
}
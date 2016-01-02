package com.gotwo.lexer;

import com.gotwo.error.LexerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by florian on 27/11/15.
 *
 * A lexer for the go2 language.
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
    public List<Token> lexAll() throws IOException, LexerException {
        List<Token> tokenList = new LinkedList<>();

        String line;
        int lineNr = 0;
        while((line = reader.readLine()) != null) {
            //Go2 supports exactly one statement per line
            //So we can simply parse it line by line
            lineNr++;
            String[] lines = line.split("\\n");
            for(String l : lines) {
                tokenList = parseLine(l, tokenList, lineNr);
            }
        }
        tokenList.add(new Newline(lineNr + 1)); //Each statement has to be "closed" with a newline
        return  tokenList;
    }

    private List<Token> parseLine(String line, int lineNr) throws IOException, LexerException {
        return parseLine(line, new ArrayList<>(), lineNr);
    }

    private List<Token> parseLine(String line, List<Token> tokenList, int lineNr) throws IOException, LexerException {
        if(line.length() < 1) {
            return tokenList;
        }

        PushbackReader reader = new PushbackReader(new StringReader(line));

        int i;
        char c;
        while((i = reader.read()) != -1 && i != 65535) {
            c = (char)i;

            if(Character.isLetter(c) || c == '.') {
                //See if we have to handle a string token...

                //tokens should only consist of characters for now
                String name = "" + c;
                while(Character.isAlphabetic(i = reader.read()) || i == '.') {
                    name += (char)i;
                }
                reader.unread(i);

                Token token = Keyword.getKeyword(name, lineNr);
                if(token == null) {
                    token = new Identifier(name, lineNr);
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

                tokenList.add(new Integer(java.lang.Integer.parseInt(num), lineNr));
            } else {
                //...or an operator
                switch (c) {
                    case '+':
                        tokenList.add(new Operator(Operator.OP.ADD, lineNr));
                        break;
                    case '-':
                        tokenList.add(new Operator(Operator.OP.SUB, lineNr));
                        break;
                    case '*':
                        tokenList.add(new Operator(Operator.OP.MUL, lineNr));
                        break;
                    case '%':
                        tokenList.add(new Operator(Operator.OP.MOD, lineNr));
                        break;
                    case '/':
                        tokenList.add(new Operator(Operator.OP.DIV, lineNr));
                        break;
                    case '=':
                        tokenList.add(matchEqul(reader, lineNr));
                        break;
                    case '!':
                        tokenList.add(mathExclamation(reader, lineNr));
                        break;
                    case '<':
                        tokenList.add(mathLess(reader, lineNr));
                        break;
                    case '>':
                        tokenList.add(mathGreater(reader, lineNr));
                        break;
                    case ' ':
                        break;
                    case '(':
                        tokenList.add(new Bracket(Bracket.BRACKET.OPEN, lineNr));
                        break;
                    case ')':
                        tokenList.add(new Bracket(Bracket.BRACKET.CLOSE, lineNr));
                        break;
                    default:
                        throw new LexerException("Illegal Character '" + c + "'.", lineNr);
                }
            }
        }

        tokenList.add(new Newline(lineNr));
        return tokenList;
    }

    private Token matchEqul(PushbackReader reader, int lineNr) throws IOException {
        int i = reader.read();
        if(i == -1) {
            return new Assignment(lineNr);
        }

        char c = (char)i;
        switch (c) {
            case '=':
                return new Operator(Operator.OP.EQU, lineNr);
            default:
                //found an equal sing
                reader.unread(c);
                return new Assignment(lineNr);
        }
    }

    private Token mathExclamation(PushbackReader reader, int lineNr) throws IOException, LexerException {
        int i = reader.read();

        char c = (char) i;
        switch (c) {
            case '=':
                return new Operator(Operator.OP.NOTEQU, lineNr);
            default:
                reader.unread(c);
                return new Operator(Operator.OP.NOT, lineNr);
        }
    }

    private Token mathGreater(PushbackReader reader, int lineNr) throws IOException, LexerException {
        int i = reader.read();

        char c = (char) i;
        switch (c) {
            case '=':
                return new Operator(Operator.OP.GREATEREQU, lineNr);
            default:
                reader.unread(c);
                return new Operator(Operator.OP.GREATER, lineNr);
        }
    }

    private Token mathLess(PushbackReader reader, int lineNr) throws IOException, LexerException {
        int i = reader.read();

        char c = (char) i;
        switch (c) {
            case '=':
                return new Operator(Operator.OP.LESSEQU, lineNr);
            default:
                reader.unread(c);
                return new Operator(Operator.OP.LESS, lineNr);
        }
    }
}
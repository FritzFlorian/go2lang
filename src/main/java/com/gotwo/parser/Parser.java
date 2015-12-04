package com.gotwo.parser;

import com.gotwo.error.IllegalTokenException;
import com.gotwo.error.RequireTokenException;
import com.gotwo.lexer.*;
import com.gotwo.lexer.Integer;

import java.util.List;

/**
 * Created by florian on 03/12/15.
 *
 * Parser of the go2lang.
 * Generates an AST representation of an given list of tokens.
 * Also collects information about types, symbols and jump marks.
 */
public class Parser {
    private List<Token> tokenList;

    public Parser(List<Token> tokenList) {
        this.tokenList = tokenList;

        //Artificially add an end token to make parsing the
        //global outer scope easy
        tokenList.add(new Keyword(Keyword.KEY.END));
    }

    public ScopeNode parseTokens() throws RequireTokenException, IllegalTokenException {
        return parseScope(null); //The root scope is the special, most outer scope
                                  //It never has a parent scope(during compile time)
    }

    /**
     * @param parent The parent scope in which the compiler currently is
     * @return The fully featured AST for the parsed sub-scope.
     */
    private ScopeNode parseScope(ScopeNode parent) throws RequireTokenException, IllegalTokenException {
        ScopeNode currentScope = new ScopeNode(parent);

        Token currentToken;
        //Lets parse until we reach the end of the scope
        //This can mean an empty token list(an error in syntax),
        //or an end keyword, such an complete scope
        while( true ) {
            //The bad case, end of file before each scope was closed
            //Will throw error if no token is left
            currentToken = requireToken();

            switch (currentToken.getType()) {
                case KEYWORD:
                    Keyword keyword = (Keyword)currentToken;
                    //First handle one special case, we finished parsing an complete scope
                    //Do not remove the end/else keyword, this might be used by the calling method
                    if(keyword.getKey() == Keyword.KEY.END
                            || keyword.getKey() == Keyword.KEY.ELSE) {
                        return currentScope;
                    }
                    tokenList.remove(0);
                    //Handle all usual keywords...
                    handleKeyword(keyword, currentScope);
                    break;

                //Error cases, these are for sure illegal syntax
                case INTEGER:
                case ASSIGNMENT:
                case OPERATOR:
                    //An operator/assigment at the beginnig of a line?
                    //This makes no sense
                    throw new IllegalTokenException(null, currentToken);

                case NEWLINE:
                    //A blank line without any command in it
                    //Continue with next statement
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Handles the given Keyword.
     *
     * @param keyword The current keyword, already removed from the list
     * @param currentScope The current scope we work in, used for symbol tables
     * @throws IllegalTokenException
     * @throws RequireTokenException
     */
    private void handleKeyword(Keyword keyword, ScopeNode currentScope) throws IllegalTokenException, RequireTokenException {
        switch (keyword.getKey()) {
            case INT:
                //Parse the integer
                handleIntegerDeclaration();
                break;

            case LABEL:
                //Read the label into our jump table
                //TODO: add entry to current scope
                handleLabelDeclaration();
                break;

            case SCOPE:
                //Recursively parse sub-scopes
                //TODO: add entry to current scope
                parseScope(currentScope);
                break;

            case IF:
                handleIfBlock(currentScope);
                break;


            default:
                break;
        }
    }

    private void handleIfBlock(ScopeNode currentScope) throws IllegalTokenException, RequireTokenException {
        //Read the if condition
        //TODO: Parse condition
        //Now get the if block
        ScopeNode ifScope = parseScope(currentScope);
        //Get the next token to see if there is an else block
        //There has to be an ELSE or an END for valid syntax
        Token currentToken = tokenList.get(0);
        if(currentToken.getType() != Token.TYPE.KEYWORD) {
            throw new IllegalTokenException(new Keyword(Keyword.KEY.END), currentToken);
        }
        //We are happy, take the token from the list
        tokenList.remove(0);
        Keyword endKeyword = (Keyword)currentToken;
        if(endKeyword.getKey() == Keyword.KEY.END) {
            //TODO: Add if node without else block
        } else if(endKeyword.getKey() == Keyword.KEY.ELSE) {
            ScopeNode elseScope = parseScope(currentScope);
            //Now we need an end for sure
            currentToken = tokenList.get(0);
            if(currentToken.getType() != Token.TYPE.KEYWORD) {
                throw new IllegalTokenException(new Keyword(Keyword.KEY.END), currentToken);
            }
            //We are happy, take the token from the list
            tokenList.remove(0);
            endKeyword = (Keyword)currentToken;
            if(endKeyword.getKey() != Keyword.KEY.END) {
                throw new IllegalTokenException(new Keyword(Keyword.KEY.END), currentToken);
            }
            //We finished the else block
            //TODO: Add if-else node
        }
    }

    /**
     * Reads an label declaration and adds it to the symbol table.
     *
     * @throws IllegalTokenException
     * @throws RequireTokenException
     */
    private void handleLabelDeclaration() throws IllegalTokenException, RequireTokenException {
        Token currentToken = requireToken();
        if(currentToken.getType() != Token.TYPE.IDENTIFIER) {
            throw new IllegalTokenException(new Identifier("ID"), currentToken);
        }
        tokenList.remove(0);

        Identifier identifier = (Identifier)currentToken;
        //Add the identifier to our symbol list
        //TODO: Add symbol table entry
    }

    /**
     * Reads the expected statement/tokens that have to appear after we
     * Found AND already consumed an integer keyword.
     *
     * @throws IllegalTokenException
     * @throws RequireTokenException
     */
    private void handleIntegerDeclaration() throws IllegalTokenException, RequireTokenException {
        //We got an simple integer declaration here.
        //Note that we enforce one definition per line for now.
        //We also enforce a default value to be set,
        //as people can jump into a scope at any time.
        //This also permits calculations in there.
        //This gives us: INT name = constant-value
        Token currentToken = requireToken();
        if(currentToken.getType() != Token.TYPE.ASSIGNMENT) {
            throw new IllegalTokenException(new Assignment(), currentToken);
        }
        tokenList.remove(0);
        currentToken = requireToken();
        if(currentToken.getType() != Token.TYPE.INTEGER) {
            throw new IllegalTokenException(new Integer(0), currentToken);
        }

        Integer integer = (Integer)currentToken;
        //Save the integer to the symbol table
        //TODO: Add symbol table entry
    }

    /**
     * Looks if there are still tokens to parse, if so, return it.
     * If not return null;
     */
    private Token nextToken() {
        return tokenList.get(0);
    }

    /**
     * Calls nextToken, but enforces that there still is a Token to parse.
     * Throw an exception otherwise.
     */
    private Token requireToken() throws RequireTokenException {
        Token token = nextToken();
        if(token == null) {
            throw new RequireTokenException();
        }
        return token;
    }
}

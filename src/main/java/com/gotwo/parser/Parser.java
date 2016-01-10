package com.gotwo.parser;

import com.gotwo.codegen.SPEED;
import com.gotwo.error.DuplicatedIdentifier;
import com.gotwo.error.IllegalTokenException;
import com.gotwo.error.RequireTokenException;
import com.gotwo.error.UndeclearedIdentifier;
import com.gotwo.lexer.*;
import com.gotwo.lexer.Integer;

import java.util.*;

/**
 * Created by florian on 03/12/15.
 *
 * Parser of the go2lang.
 * Generates an AST representation of an given list of tokens.
 * Also collects information about types, symbols and jump marks.
 */
public class Parser {
    private List<Token> tokenList;
    private List<LabelDeclaration> labelList;
    private Map<LabelDeclaration, ScopeNode> targetScopes;
    private Map<String, LabelDeclaration> targetLabels;
    private Map<String, List<LabelDeclaration>> goBackLabels;
    private List<ScopeNode> scopeNodes;
    private ParsingContext context;
    private List<InvitationNode> invitations;

    public Parser(List<Token> tokenList) {
        this.tokenList = tokenList;
        this.context = new ParsingContext();
        this.labelList = new ArrayList<>();
        this.scopeNodes = new ArrayList<>();
        this.targetScopes = new HashMap<>();
        this.targetLabels = new HashMap<>();
        this.goBackLabels = new HashMap<>();
        this.invitations = new ArrayList<>();

        //Artificially add an end token to make parsing the
        //global outer scope easy
        tokenList.add(new Keyword(Keyword.KEY.END, -1));
    }

    public ParsingResult parseTokens() throws RequireTokenException, IllegalTokenException, UndeclearedIdentifier, DuplicatedIdentifier {
        ScopeNode externalRoot = new ScopeNode(null, context);
        ScopeNode root = parseScope(externalRoot);  //The root scope is the special, most outer scope
                                            //It never has a "real" parent scope(during compile time)
                                            //Its parent scope is a simple helper for external jumps
        //Add external jumps to the externalRoot
        addExternalJumps(externalRoot);
        if(targetLabels.containsKey("start")) {
            //Generate start goto
            externalRoot.addChildNode(new GoToLabelNode(SPEED.RUN, targetLabels.get("start")));
        }
        externalRoot.addChildNode(root);

        return new ParsingResult(externalRoot, labelList, targetScopes, targetLabels, context, scopeNodes, goBackLabels);
    }

    private void addExternalJumps(ScopeNode scopeNode) {
        for(InvitationNode invitationNode : invitations) {
            scopeNode.addChildNode(invitationNode);
        }
    }

    /**
     * @param parent The parent scope in which the compiler currently is
     * @return The fully featured AST for the parsed sub-scope.
     */
    private ScopeNode parseScope(ScopeNode parent) throws RequireTokenException, IllegalTokenException, UndeclearedIdentifier, DuplicatedIdentifier {
        ScopeNode currentScope = new ScopeNode(parent, context);
        scopeNodes.add(currentScope);

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
                    //Do not remove the end keyword, this might be used by the calling method
                    if(keyword.getKey() == Keyword.KEY.END) {
                        return currentScope;
                    }
                    tokenList.remove(0);
                    //Handle all usual keywords...
                    handleKeyword(keyword, currentScope);
                    break;

                case IDENTIFIER:
                    Identifier identifier = (Identifier)currentToken;
                    tokenList.remove(0);
                    //Here we should now find an assignment to the variable
                    handleAssignment(currentScope, identifier);
                    break;

                //Error cases, these are for sure illegal syntax
                case INTEGER:
                case ASSIGNMENT:
                case OPERATOR:
                    //An operator/assignment at the beginning of a line?
                    //This makes no sense
                    throw new IllegalTokenException(null, currentToken);

                case NEWLINE:
                    //A blank line without any command in it
                    //Continue with next statement, remove this newline
                    tokenList.remove(0);
                    break;

                default:
                    throw new IllegalTokenException(null, currentToken);
            }

            //We successfully parsed a statement
            //There has to be a NEWLINE unless the last statement was a blank line
            if(currentToken.getType() != Token.TYPE.NEWLINE) {
                requireToken(Token.TYPE.NEWLINE);
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
    private void handleKeyword(Keyword keyword, ScopeNode currentScope) throws IllegalTokenException, RequireTokenException, UndeclearedIdentifier, DuplicatedIdentifier {
        switch (keyword.getKey()) {
            case INT:
                //Parse the integer
                handleIntegerDeclaration(currentScope);
                break;

            case LABEL:
                //Read the label into our jump table
                handleLabelDeclaration(currentScope, keyword);
                break;

            case SCOPE:
                //Recursively parse sub-scopes
                ScopeNode subScope = parseScope(currentScope);
                currentScope.addChildNode(subScope);
                Keyword innerEnd = (Keyword)requireToken(Token.TYPE.KEYWORD);
                if(innerEnd.getKey() != Keyword.KEY.END) {
                    throw new IllegalTokenException(new Keyword(Keyword.KEY.END, innerEnd.getLine()), innerEnd);
                }
                //Finished parsing the inner scope, remove the end token
                tokenList.remove(0);
                break;

            case RUN:
                handleGoToNode(SPEED.RUN, currentScope);
                break;
            case GO:
                handleGoToNode(SPEED.GO, currentScope);
                break;

            case IF:
                handleIfBlock(currentScope);
                break;

            case INVITE:
                handleInviteNode(currentScope);
                break;

            default:
                throw new IllegalTokenException(null, keyword);
        }
    }

    private void handleInviteNode(ScopeNode currentScope) throws RequireTokenException, IllegalTokenException, DuplicatedIdentifier {
        String firstIdentifier = requireFullLabelName();

        Keyword firstTo = (Keyword) requireToken(Token.TYPE.KEYWORD);
        tokenList.remove(0);
        Keyword keyword = (Keyword) requireToken(Token.TYPE.KEYWORD);
        tokenList.remove(0);
        Keyword secondTo = (Keyword) requireToken(Token.TYPE.KEYWORD);
        tokenList.remove(0);

        SPEED speed;

        switch (keyword.getKey()) {
            case GO:
                speed = SPEED.GO;
                break;
            case RUN:
                speed = SPEED.RUN;
                break;
            case SPRINT:
                speed = SPEED.SPRINT;
                break;
            default:
                throw new IllegalTokenException(new Keyword(Keyword.KEY.GO, keyword.getLine()), keyword);
        }

        if(firstTo.getKey() != Keyword.KEY.TO) {
            throw new IllegalTokenException(new Keyword(Keyword.KEY.TO, firstTo.getLine()), firstTo);
        }
        if(secondTo.getKey() != Keyword.KEY.TO) {
            throw new IllegalTokenException(new Keyword(Keyword.KEY.TO, firstTo.getLine()), firstTo);
        }

        String secondIdentifier = requireFullLabelName();

        addInviteNode(secondIdentifier, firstIdentifier, speed, currentScope, keyword);
    }

    private InvitationNode addInviteNode(String targetName, String externalName, SPEED speed, ScopeNode currentScope, Token keyword) throws DuplicatedIdentifier {
        //Here wo got everything we need to get started
        LabelDeclaration targetLabel = declareLabel(targetName, currentScope, false, -1, keyword);
        InvitationNode invitationNode = new InvitationNode(externalName, targetLabel, speed);
        invitations.add(invitationNode);
        return invitationNode;
    }

    private void handleGoToNode(SPEED speed, ScopeNode currentScope) throws RequireTokenException, IllegalTokenException, DuplicatedIdentifier {
        Keyword keyword = (Keyword)requireToken(Token.TYPE.KEYWORD);

        switch (keyword.getKey()) {
            case TO:
                tokenList.remove(0);
                handleGoToLabelNode(speed, currentScope);
                break;
            case BACK:
                tokenList.remove(0);
                handleGoBackNode(speed, currentScope);
                break;
            default:
                throw new IllegalTokenException(new Keyword(Keyword.KEY.TO, keyword.getLine()), keyword);
        }
    }

    private void handleGoToLabelNode(SPEED speed, ScopeNode currentScope) throws RequireTokenException, IllegalTokenException, DuplicatedIdentifier {
        Token currentToken = requireToken();
        if(currentToken.getType() == Token.TYPE.KEYWORD) {
            //Special case for language keywords/bindings
            //We will replace this with runtime calls later, but it should be good for now
            tokenList.remove(0);
            handleGoToFile(speed, currentScope, (Keyword)currentToken);
            return;
        }

        String fullLabelName = requireFullLabelName();

        LabelDeclaration targetLabel = targetLabels.get(fullLabelName);
        if(targetLabel == null) {
            targetLabel = declareLabel(fullLabelName, currentScope, false, -1, currentToken);
        }

        GoToLabelNode goToLabelNode = new GoToLabelNode(speed, targetLabel);
        currentScope.addChildNode(goToLabelNode);

        goToLabelNode.setBackLabel(handleLocalGoBackLabel(currentScope, targetLabel, currentToken, "goto", true));
    }

    private void handleGoBackNode(SPEED speed, ScopeNode currentScope) throws RequireTokenException, IllegalTokenException, DuplicatedIdentifier {
        currentScope.addChildNode(new GoBackNode(speed));
    }


    private LabelDeclaration handleLocalGoBackLabel(ScopeNode currentScope, LabelDeclaration targetLabel, Token currentToken, String suffix, boolean add) throws DuplicatedIdentifier {
        int id = context.getGoBackLabelId();
        //Go back semantic, add a jump back label & keep track of possible go backs
        LabelDeclaration backLabelDeclaration = declareLabel("#" + id + "#" + suffix, currentScope, false, id, currentToken);
        if(!goBackLabels.containsKey(targetLabel.getName())) {
            goBackLabels.put(targetLabel.getName(), new LinkedList<>());
        }
        goBackLabels.get(targetLabel.getName()).add(backLabelDeclaration);
        if(add) {
            currentScope.addChildNode(new LabelNode(backLabelDeclaration));
        }

        return backLabelDeclaration;
    }

    private void handleGoToFile(SPEED speed, ScopeNode currentScope, Keyword keyword) throws IllegalTokenException, RequireTokenException, DuplicatedIdentifier {
        switch (keyword.getKey()) {
            case OTHER:
                Identifier identifier = (Identifier)requireToken(Token.TYPE.IDENTIFIER);
                tokenList.remove(0);
                String fullLabelName = requireFullLabelName();


                GoToFileNode goToFileNode = new GoToFileNode(speed, identifier.getName(),fullLabelName);
                currentScope.addChildNode(goToFileNode);

                int id = context.getGoBackLabelId();
                LabelDeclaration backLabel = declareLabel("$" + id + "$", currentScope, false, id, keyword);
                goToFileNode.setBackLabel(backLabel);

                for(SPEED backSpeed : SPEED.values()) {
                    addInviteNode(backLabel.getName(), backLabel.getName(), backSpeed, currentScope, keyword);
                }

                backLabel.markAsDeclared(currentScope);
                currentScope.addChildNode(new LabelNode(backLabel));
                break;
            default:
                throw new IllegalTokenException(null, keyword);
        }
    }

    /**
     * Will handle an variable assignment.
     * Here we also take care to not use any undefined variables.
     */
    private void handleAssignment(ScopeNode currentScope, Identifier identifier) throws UndeclearedIdentifier, RequireTokenException, IllegalTokenException {
        IntegerDeclaration integerDeclaration = currentScope.getIntegerDeclaration(identifier.getName());

        if(integerDeclaration == null) {
            throw new UndeclearedIdentifier(identifier.getName(), identifier);
        }
        requireToken(Token.TYPE.ASSIGNMENT);
        tokenList.remove(0);

        currentScope.addChildNode(new AssignmentNode(integerDeclaration, handleExpression(currentScope)));
    }

    private ExpressionNode handleExpression(ScopeNode currentScope) throws IllegalTokenException, RequireTokenException, UndeclearedIdentifier {
        /**
         * Operator Precedence:
         * 0) !
         * 1) *, /
         * 2) +, -
         * 3) <=, >=, ==, !=
         */

        ExpressionNode left = handleSum(currentScope);

        Token currentToken = tokenList.get(0);
        if(currentToken.getType() == Token.TYPE.OPERATOR) {
            Operator operator = (Operator)currentToken;

            if(operator.getOp() == Operator.OP.GREATER || operator.getOp() == Operator.OP.LESS
                    || operator.getOp() == Operator.OP.GREATEREQU || operator.getOp() == Operator.OP.LESSEQU
                    || operator.getOp() == Operator.OP.EQU || operator.getOp() == Operator.OP.NOTEQU) {
                tokenList.remove(0);
                return new ExpressionNode.SubExpressionNode(left, operator, handleExpression(currentScope));
            }
        }

        return left;
    }

    private ExpressionNode handleSum(ScopeNode currentScope) throws IllegalTokenException, RequireTokenException, UndeclearedIdentifier {
        ExpressionNode left = handleTerm(currentScope);

        Token currentToken = tokenList.get(0);
        if(currentToken.getType() == Token.TYPE.OPERATOR) {
            Operator operator = (Operator)currentToken;

            if(operator.getOp() == Operator.OP.ADD || operator.getOp() == Operator.OP.SUB) {
                tokenList.remove(0);
                return new ExpressionNode.SubExpressionNode(left, operator, handleSum(currentScope));
            }
        }

        return left;
    }

    private ExpressionNode handleTerm(ScopeNode currentScope) throws RequireTokenException, UndeclearedIdentifier, IllegalTokenException {
        ExpressionNode left = handleUnary(currentScope);

        Token currentToken = tokenList.get(0);
        if(currentToken.getType() == Token.TYPE.OPERATOR) {
            Operator operator = (Operator)currentToken;

            if(operator.getOp() == Operator.OP.MUL || operator.getOp() == Operator.OP.DIV || operator.getOp() == Operator.OP.MOD) {
                tokenList.remove(0);
                return new ExpressionNode.SubExpressionNode(left, operator, handleTerm(currentScope));
            }
        }

        return left;
    }

    private ExpressionNode handleUnary(ScopeNode currentScope) throws RequireTokenException, UndeclearedIdentifier, IllegalTokenException {
        Token currentToken = tokenList.get(0);
        if(currentToken.getType() == Token.TYPE.OPERATOR) {
            Operator operator = (Operator)currentToken;

            if(operator.getOp() == Operator.OP.NOT) {
                tokenList.remove(0);
                return new ExpressionNode.UnarySubExpressionNode(operator, handleUnary(currentScope));
            }
        }

        return handleFactor(currentScope);
    }

    private ExpressionNode handleFactor(ScopeNode currentScope) throws UndeclearedIdentifier, IllegalTokenException, RequireTokenException {
        Token currentToken = tokenList.get(0);

        switch (currentToken.getType()) {
            case INTEGER:
                Integer integer = (Integer)currentToken;
                tokenList.remove(0);
                return new ExpressionNode.ConstIntExpressionNode(integer.getValue());

            case IDENTIFIER:
                Identifier identifier = (Identifier)currentToken;
                IntegerDeclaration integerDeclaration = currentScope.getIntegerDeclaration(identifier.getName());
                if(integerDeclaration == null) {
                    throw new UndeclearedIdentifier(identifier.getName(), identifier);
                }
                tokenList.remove(0);
                return new ExpressionNode.IntExpressionNode(integerDeclaration);

            case BRACKET:
                Bracket bracket = (Bracket)currentToken;
                if(bracket.getBracket() != Bracket.BRACKET.OPEN) {
                    throw new IllegalTokenException(new Bracket(Bracket.BRACKET.OPEN, currentToken.getLine()), currentToken);
                }
                tokenList.remove(0);
                //Read the inner term
                ExpressionNode result = handleExpression(currentScope);

                currentToken = requireToken(Token.TYPE.BRACKET);
                bracket = (Bracket)currentToken;
                if(bracket.getBracket() != Bracket.BRACKET.CLOSE) {
                    throw new IllegalTokenException(new Bracket(Bracket.BRACKET.CLOSE, currentToken.getLine()), currentToken);
                }
                tokenList.remove(0);
                return result;

            default:
                throw new IllegalTokenException(null, currentToken);
        }
    }

    /**
     * Parse an if block.
     * Adds the blocks to the current scope.
     */
    private void handleIfBlock(ScopeNode currentScope) throws IllegalTokenException, RequireTokenException, UndeclearedIdentifier, DuplicatedIdentifier {
        //Read the if condition
        ExpressionNode expression = handleExpression(currentScope);
        //Now get the if block
        ScopeNode ifScope = parseScope(currentScope);
        //There has to be an END for valid syntax
        Token currentToken = tokenList.get(0);
        if(currentToken.getType() != Token.TYPE.KEYWORD) {
            throw new IllegalTokenException(new Keyword(Keyword.KEY.END, currentToken.getLine()), currentToken);
        }
        //We are happy, take the token from the list
        tokenList.remove(0);
        Keyword endKeyword = (Keyword)currentToken;
        if(endKeyword.getKey() == Keyword.KEY.END) {
            currentScope.addChildNode(ConditionNode.NewConditionNode(expression, ifScope));
        }
    }

    /**
     * Reads an label declaration and adds it to the symbol table.
     *
     * @throws IllegalTokenException
     * @throws RequireTokenException
     */
    private void handleLabelDeclaration(ScopeNode currentScope, Token currentToken) throws IllegalTokenException, RequireTokenException, DuplicatedIdentifier {
        String fullLabelName = requireFullLabelName();

        currentScope.addChildNode(new LabelNode(declareLabel(fullLabelName, currentScope, true, -1, currentToken)));
    }

    private String requireFullLabelName() throws RequireTokenException, IllegalTokenException {
        Identifier identifier = (Identifier)requireToken(Token.TYPE.IDENTIFIER);
        tokenList.remove(0);

        String fullLabelName = identifier.getName();
        Token currentToken;
        while((currentToken = tokenList.get(0)).getType() == Token.TYPE.IDENTIFIER) {
            identifier = (Identifier)currentToken;
            tokenList.remove(0);
            fullLabelName += " " + identifier.getName();
        }

        return fullLabelName;
    }

    private LabelDeclaration declareLabel(String name, ScopeNode currentScope, boolean newLabel, int numericValue, Token currentToken) throws DuplicatedIdentifier {
        LabelDeclaration labelDeclaration = targetLabels.get(name);

        if(labelDeclaration != null) {
            if(newLabel) {
                if(labelDeclaration.isDeclared()) {
                    throw new DuplicatedIdentifier(name, currentToken);
                } else {
                    labelDeclaration.markAsDeclared(currentScope);
                }
            }

            return  labelDeclaration;
        }


        labelDeclaration = new LabelDeclaration(name, currentScope, context.getNextLabelId(),  currentScope.getChildNodes().size(), newLabel, numericValue);

        labelList.add(labelDeclaration);
        targetScopes.put(labelDeclaration, currentScope);
        targetLabels.put(name, labelDeclaration);

        return labelDeclaration;
    }

    /**
     * Reads the expected statement/tokens that have to appear after we
     * Found AND already consumed an integer keyword.
     *
     * @throws IllegalTokenException
     * @throws RequireTokenException
     */
    private void handleIntegerDeclaration(ScopeNode currentScope) throws IllegalTokenException, RequireTokenException, DuplicatedIdentifier {
        //We got an simple integer declaration here.
        //Note that we enforce one definition per line for now.
        //We also enforce a default value to be set,
        //as people can jump into a scope at any time.
        //This also permits calculations in there.
        //This gives us: INT name = constant-value
        Token currentToken = requireToken();
        if(currentToken.getType() != Token.TYPE.IDENTIFIER) {
            throw new IllegalTokenException(new Assignment(0), currentToken);
        }
        tokenList.remove(0);
        Identifier identifier = (Identifier)currentToken;
        currentToken = requireToken();
        if(currentToken.getType() != Token.TYPE.ASSIGNMENT) {
            throw new IllegalTokenException(new Assignment(0), currentToken);
        }
        tokenList.remove(0);
        currentToken = requireToken();
        if(currentToken.getType() != Token.TYPE.INTEGER) {
            throw new IllegalTokenException(new Integer(0, 0), currentToken);
        }
        tokenList.remove(0);
        Integer integer = (Integer)currentToken;
        //Save the integer to the symbol table
        if(currentScope.getLocalIntegerDeclaration(identifier.getName()) != null) {
            throw new DuplicatedIdentifier(identifier.getName(), identifier);
        }
        currentScope.addInteger(identifier.getName(), integer.getValue());
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

    /**
     * Calls nextToken, but enforces that there still is a Token to parse.
     * Also enforces a specific Type of token.
     * Throw an exception otherwise.
     */
    private Token requireToken(Token.TYPE type) throws RequireTokenException, IllegalTokenException {
        Token token = nextToken();
        if(token == null) {
            throw new RequireTokenException();
        }
        if(token.getType() != type) {
            throw new IllegalTokenException(new Token(type, 0), token);
        }
        return token;
    }
}

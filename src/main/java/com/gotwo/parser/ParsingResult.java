package com.gotwo.parser;

import com.gotwo.lexer.Token;

import java.util.List;
import java.util.Map;

/**
 * Created by florian on 10/12/15.
 */
public class ParsingResult {
    private ScopeNode rootScope;
    private List<Token> tokenList;
    private List<LabelDeclaration> labelList;
    private Map<LabelDeclaration, ScopeNode> targetScopes;
    private Map<String, LabelDeclaration> targetLabels;
    private ParsingContext context;


    public ParsingResult(ScopeNode rootScope, List<Token> tokenList, List<LabelDeclaration> labelList,
                         Map<LabelDeclaration, ScopeNode> targetScopes, Map<String, LabelDeclaration> targetLabels,
                         ParsingContext context) {
        this.rootScope = rootScope;
        this.tokenList = tokenList;
        this.labelList = labelList;
        this.targetScopes = targetScopes;
        this.targetLabels = targetLabels;
        this.context = context;
    }

    public ScopeNode getRootScope() {
        return rootScope;
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    public List<LabelDeclaration> getLabelList() {
        return labelList;
    }

    public Map<LabelDeclaration, ScopeNode> getTargetScopes() {
        return targetScopes;
    }

    public Map<String, LabelDeclaration> getTargetLabels() {
        return targetLabels;
    }

    public ParsingContext getContext() {
        return context;
    }
}

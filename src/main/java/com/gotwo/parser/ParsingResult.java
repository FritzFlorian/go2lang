package com.gotwo.parser;

import java.util.List;
import java.util.Map;

/**
 * Created by florian on 10/12/15.
 */
public class ParsingResult {
    private ScopeNode rootScope;
    private List<LabelDeclaration> labelList;
    private Map<LabelDeclaration, ScopeNode> targetScopes;
    private Map<String, LabelDeclaration> targetLabels;
    private Map<String, List<LabelDeclaration>> goBackLabels;
    private List<ScopeNode> scopeNodes;
    private ParsingContext context;


    public ParsingResult(ScopeNode rootScope, List<LabelDeclaration> labelList,
                         Map<LabelDeclaration, ScopeNode> targetScopes, Map<String, LabelDeclaration> targetLabels,
                         ParsingContext context, List<ScopeNode> scopeNodes,
                         Map<String, List<LabelDeclaration>> goBackLabels) {
        this.rootScope = rootScope;
        this.labelList = labelList;
        this.targetScopes = targetScopes;
        this.targetLabels = targetLabels;
        this.context = context;
        this.scopeNodes = scopeNodes;
        this.goBackLabels = goBackLabels;
    }

    public ScopeNode getRootScope() {
        return rootScope;
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

    public List<ScopeNode> getScopeNodes() {
        return scopeNodes;
    }

    public Map<String, List<LabelDeclaration>> getGoBackLabels() {
        return goBackLabels;
    }
}

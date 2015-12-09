package com.gotwo.parser;

/**
 * Created by florian on 05/12/15.
 *
 * A parsing context simply contains some information
 * about the current indexing of symbols.
 */
public class ParsingContext {
    private int currentIntegerId;
    private int currentLabelId;

    public ParsingContext() {
        currentIntegerId = 0;
        currentLabelId = 0;
    }

    public int getNextIntegerId() {
        return ++currentIntegerId;
    }

    public int getNextLabelId() {
        return ++currentLabelId;
    }
}

package com.gotwo.error;

import com.gotwo.lexer.Token;

/**
 * Created by florian on 09/12/15.
 *
 * Thrown if the parser founds multiple declarations with the same name in one scope.
 */
public class DuplicatedIdentifier extends CompilerException {

    public DuplicatedIdentifier(String name, Token token) {
        super("Found duplicate identifier with name " + name + ".", token);
    }

}

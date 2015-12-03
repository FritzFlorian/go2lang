package com.gotwo.lexer;

/**
 * Created by florian on 27/11/15.
 *
 * Represents a language keyword.
 * Used to determine when specific internal constructs start or end.
 */
public class Keyword extends Token {
    public enum KEY {IF, END, ELSE, LABEL, SCOPE, INT}
    private KEY key;

    public Keyword(KEY key) {
        super(TYPE.KEYWORD);
        this.key = key;
    }

    public KEY getKey() {
        return key;
    }

    @Override
    public String toString() {
        return getKey().toString();
    }

    public static Keyword getKeyword(String name) {
        KEY[] keys = KEY.values();
        for(KEY k : keys) {
            if(name.equalsIgnoreCase(k.toString())) {
                return new Keyword(k);
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if(super.equals(o) == false) {
            return false;
        }

        return key == ((Keyword)o).key;
    }
}

package Frontend.Symbols;

import Frontend.Inter.Id;
import Frontend.Lexer.Token;

import java.util.Hashtable;

public class Env {

    private Hashtable<Token,Id> symbolTable;
    private Env parent;

    public Env(Env n) {
        symbolTable = new Hashtable<>();
        parent = n;
    }

    public void put(Token w, Id i) {
        symbolTable.put(w, i);
    }

    public Id get(Token w) {
        for (Env e = this; e != null; e = e.parent) {
            Id found = e.symbolTable.get(w);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

}

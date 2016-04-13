package Symbols;

import Inter.Id;
import Lexer.Token;

import java.util.Hashtable;

public class Env {

    private Hashtable table;
    protected Env previous;

    public Env(Env n) {
        table = new Hashtable();
        previous = n;
    }

    public void put(Token w, Id i) {
        table.put(w, i);
    }

    public Id get(Token w) {
        for (Env e = this; e != null; e = e.previous) {
            Id found = (Id) (e.table.get(w));
            if (found != null) {
                return found;
            }
        }
        return null;
    }

}

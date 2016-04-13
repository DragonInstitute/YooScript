package Inter;

import Lexer.Token;
import Symbols.Type;

public class Expr extends Node {

    public Token op;
    public Type type;

    Expr(Token token, Type p) {
        op = token;
        type = p;
    }

    // r of a three addresses expression
    public Expr gen() {
        return this;
    }

    // reduce an expression
    public Expr reduce() {
        return this;
    }

    public void jumping(int t, int f) {
        emitjumps(toString(), t, f);
    }

    public void emitjumps(String test, int t, int f) {
        // t and f are labels
        if (t != 0 && f != 0) {
            emit("if " + test + " goto L" + t);
            emit("goto L" + f);
        } else if (t != 0) {
            emit("if " + test + " goto L" + t);
        } else if (f != 0) {
            emit("iffalse " + test + " goto L" + f);
        }
        else ; // nothing since both t and f fall through
    }

    public String toString() {
        return op.toString();
    }


}

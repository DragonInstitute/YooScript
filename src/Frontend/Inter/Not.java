package Frontend.Inter;

import Frontend.Lexer.Token;

public class Not extends Logical {

    public Not(Token token, Expr expr) {
        super(token, expr, expr);
    }

    public void jumping(int t, int f) {
        expr2.jumping(f, t);
    }

    public String toString() {
        return op.toString() + " " + expr2.toString();
    }
}

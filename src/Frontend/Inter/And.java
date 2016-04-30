package Frontend.Inter;

import Frontend.Lexer.Token;

public class And extends Logical {

    public And(Token token, Expr x1, Expr x2) {
        super(token, x1, x2);
    }

    public void jumping(int t, int f) {
        int label = f != 0 ? f : newLabel();
        expr1.jumping(0, label);
        expr2.jumping(t, f);
        if (f == 0) {
            emitLabel(label);
        }
    }

}

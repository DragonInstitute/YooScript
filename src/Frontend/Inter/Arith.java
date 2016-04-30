package Frontend.Inter;

import Frontend.Lexer.Token;
import Frontend.Symbols.Type;

public class Arith extends Op {

    public Expr expr1, expr2;

    public Arith(Token token, Expr x1, Expr x2) {
        super(token, null);
        expr1 = x1;
        expr2 = x2;
        type = Type.maxSize(expr1.type, expr2.type);
        if (type == null) {
            error("type error");
        }
    }

    public Expr gen() {
        return new Arith(op, expr1.reduce(), expr2.reduce());
    }

    public String toString() {
        return expr1.toString() + " " + op.toString() + " " + expr2.toString();
    }

}

package Frontend.Inter;

import Frontend.Lexer.Num;
import Frontend.Lexer.Token;
import Frontend.Lexer.Word;
import Frontend.Symbols.Type;

public class Constant extends Expr {

    public Constant(Token token, Type p) {
        super(token, p);
    }

    public Constant(int i) {
        super(new Num(i), Type.Int);
    }

    public static final Constant
            True = new Constant(Word.True, Type.Bool),
            False = new Constant(Word.False, Type.Bool);

    public void jumping(int t, int f) {
        if (this == True && t != 0) {
            emit("goto L" + t);
        } else if (this == False && f != 0) {
            emit("goto L" + f);
        }
    }

}

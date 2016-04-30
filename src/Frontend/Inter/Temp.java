package Frontend.Inter;

import Frontend.Lexer.Word;
import Frontend.Symbols.Type;

public class Temp extends Expr {
    static int count = 0;
    int number = 0;

    public Temp(Type p) {
        super(Word.temp, p);
        number = ++count;
    }

    public String toString() {
        return "t" + number;
    }
}

package Frontend.Inter;

import Frontend.Lexer.Word;
import Frontend.Symbols.Type;

public class Id extends Expr {

    public int offset;

    public Id(Word id, Type p, int b) {
        super(id, p);
        offset = b;
    }

}

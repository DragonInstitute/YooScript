package Frontend.Inter;

import Frontend.Lexer.Tag;
import Frontend.Lexer.Word;
import Frontend.Symbols.Type;

public class Access extends Op {

    public Id array;
    public Expr index;

    public Access(Id a, Expr i, Type p) { // p is element type after
        super(new Word("[]", Tag.INDEX), p); // flattening the array
        array = a;
        index = i;
    }

    public Expr gen() {
        return new Access(array, index.reduce(), type);
    }

    public void jumping(int t, int f) {
        emitjumps(reduce().toString(), t, f);
    }

    public String toString() {
        return array.toString() + " [ " + index.toString() + " ]";
    }

}

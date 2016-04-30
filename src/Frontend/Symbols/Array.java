package Frontend.Symbols;

import Frontend.Lexer.Tag;

public class Array extends Type {

    public Type of;
    public int size = 1;

    public Array(int size, Type p) {
        super("[]", Tag.INDEX, size * p.width);
        this.size = size;
        of = p;
    }

    public String toString() {
        return "[" + size + "]" + of.toString();
    }

}

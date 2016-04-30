package Frontend.Symbols;

import Frontend.Lexer.Tag;
import Frontend.Lexer.Word;

public class Type extends Word {
    public int width = 0;

    public Type(String s, int tag, int width) {
        super(s, tag);
        this.width = width;
    }

    public static final Type
            Int = new Type("int", Tag.BASIC, 4),
            Float = new Type("float", Tag.BASIC, 8),
            Char = new Type("char", Tag.BASIC, 1),
            Bool = new Type("bool", Tag.BASIC, 1);

    public static boolean numeric(Type p) {
        return (p == Char || p == Int || p == Float);
    }

    public static Type maxSize(Type p1, Type p2) {
        if (!numeric(p1) || !numeric(p2)) {
            return null;
        } else if (p1 == Float || p2 == Float) {
            return Float;
        } else if (p1 == Int || p2 == Int) {
            return Int;
        } else {
            return Char;
        }
    }

}

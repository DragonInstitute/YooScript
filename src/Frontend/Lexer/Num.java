package Frontend.Lexer;

public class Num extends Token{

    public final int value;

    public Num(int t) {
        super(Tag.NUM);
        value = t;
    }

    public String toString() {
        return "" + value;
    }

}

package Frontend.Lexer;

public class Real extends Token{

    public final float value;

    public Real(float t) {
        super(Tag.REAL);
        value = t;
    }

    public String toString() {
        return "" + value;
    }

}

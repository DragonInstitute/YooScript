package Frontend.Lexer;

public class Real extends Token{

    public final double value;

    public Real(double t) {
        super(Tag.REAL);
        value = t;
    }

    public String toString() {
        return "" + value;
    }

}

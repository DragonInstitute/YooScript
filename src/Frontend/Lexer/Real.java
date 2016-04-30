package Frontend.Lexer;

import java.math.BigInteger;

public class Real extends Token{

    public final double value;

    public Real(double t) {
        super(Tag.REAL);
        value = t;
    }

    public String toString() {
        String text = String.valueOf(value);
        BigInteger bigInt = new BigInteger(text.getBytes());
        return "" + bigInt.toString();
    }

}

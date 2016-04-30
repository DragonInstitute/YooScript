package Frontend.Lexer;

public class Token {

    public final int tag;

    public Token(int t) {
        tag = t;
    }

    public static Token build(int n){
        return new Num(n);
    }
    static Token build(float n){
        return new Real(n);
    }

    public String toString() {
        return "" + (char) tag;
    }

}

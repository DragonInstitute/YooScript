package Frontend.Inter;

import Frontend.Lexer.Token;
import Frontend.Symbols.Type;

public class Op extends Expr{

    public Op(Token token, Type p){
        super(token,p);
    }

    public Expr reduce(){
        Expr x = gen();
        Temp t = new Temp(type);
        emit(t.toString()+" = "+x.toString());
        return t;
    }

}

package Frontend.Main;

import Frontend.Lexer.Lexer;
import Frontend.Parser.Parser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException{
        Lexer lexer = new Lexer();
        Parser parser= new Parser(lexer);
        parser.program();
        System.out.println();
    }

}

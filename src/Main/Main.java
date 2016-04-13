package Main;

import Lexer.Lexer;
import Parser.Parser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException{
        System.out.println("Start!");
        Lexer lexer = new Lexer();
        Parser parser= new Parser(lexer);
        parser.program();
        System.out.write('\n');
        System.out.println("Finish!");
    }

}

package Frontend.Main;

import Frontend.Lexer.Lexer;
import Frontend.Parser.Parser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static Backend.Generator.Util.getInput;
import static Backend.Generator.Util.getOutput;

public class Main {
    public static void main(String[] args) throws IOException{
        String output = getOutput(args, "temp.inter");
        String input = getInput(args, null);
        PrintStream ps=new PrintStream(new FileOutputStream(output));
        System.setOut(ps);
        FileInputStream fis=new FileInputStream(input);
        System.setIn(fis);
        Lexer lexer = new Lexer();
        Parser parser= new Parser(lexer);
        parser.program();
        System.out.flush();
    }

}
